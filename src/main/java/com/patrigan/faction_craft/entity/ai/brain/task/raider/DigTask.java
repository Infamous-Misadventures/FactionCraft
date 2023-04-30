package com.patrigan.faction_craft.entity.ai.brain.task.raider;

import com.google.common.collect.ImmutableMap;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import com.patrigan.faction_craft.registry.ModMemoryModuleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

import static com.patrigan.faction_craft.block.ReconstructBlock.setReconstructBlock;
import static com.patrigan.faction_craft.util.GeneralUtils.blockPosToVec3;
import static net.minecraftforge.common.ForgeMod.REACH_DISTANCE;

public class DigTask<E extends LivingEntity> extends Behavior<E> {

    private static final Set<ToolAction> digActions = Set.of(ToolActions.SHOVEL_DIG, ToolActions.AXE_DIG, ToolActions.PICKAXE_DIG);

    private final boolean requiresTool;
    private final boolean requiresProperTool;
    private final List<BlockPos> targetBlocks = new ArrayList<>();
    private final EquipmentSlot hand;
    private final FactionEntity factionEntityCapability;
    private BlockState currentBlockState = null;
    private float breakDuration = 0;
    private int destroyProgressStart = 0;
    private BlockPos targetPos = null;

    public DigTask(Mob entity, boolean requiresTool, boolean requiresProperTool, EquipmentSlot hand) {
        super(ImmutableMap.of(ModMemoryModuleTypes.RAID_WALK_TARGET.get(), MemoryStatus.VALUE_PRESENT, ModMemoryModuleTypes.IS_STUCK.get(), MemoryStatus.VALUE_PRESENT));
        this.factionEntityCapability = FactionEntityHelper.getFactionEntityCapability(entity);
        this.requiresTool = requiresTool;
        this.requiresProperTool = requiresProperTool;
        this.hand = hand;
    }

    protected boolean checkExtraStartConditions(ServerLevel pLevel, E entity) {
        if (this.requiresTool && !canToolDig(entity)) {
            return false;
        }
        if(!factionEntityCapability.isStuck()) {
            entity.getBrain().eraseMemory(ModMemoryModuleTypes.IS_STUCK.get());
            return false;
        }
        return true;
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, E entity, long pGameTime) {
        if (this.requiresTool && !canToolDig(entity)) {
            return false;
        }
        return !this.targetBlocks.isEmpty() && this.hasRequiredMemories(entity);
    }

    private boolean hasRequiredMemories(E pOwner) {
        for(Map.Entry<MemoryModuleType<?>, MemoryStatus> entry : this.entryCondition.entrySet()) {
            MemoryModuleType<?> memorymoduletype = entry.getKey();
            MemoryStatus memorystatus = entry.getValue();
            if (!pOwner.getBrain().checkMemory(memorymoduletype, memorystatus)) {
                return false;
            }
        }

        return true;
    }

    protected void start(ServerLevel level, E entity, long gameTime) {
        this.targetPos = entity.getBrain().getMemory(ModMemoryModuleTypes.RAID_WALK_TARGET.get()).get().pos();
        selectBlocksToDig(entity);
        if (!this.targetBlocks.isEmpty()) {
            initBlockBreak(entity);
        }else{
            factionEntityCapability.setStuck(false);
            entity.getBrain().eraseMemory(ModMemoryModuleTypes.IS_STUCK.get());
        }
    }

    private float getReachDistance(E entity) {
        AttributeInstance reachDistanceAttribute = entity.getAttribute(REACH_DISTANCE.get());
        return reachDistanceAttribute != null ? (float) reachDistanceAttribute.getValue() : 4.5F;
    }

    private void initBlockBreak(E entity) {
        this.currentBlockState = entity.level.getBlockState(this.targetBlocks.get(0));
        this.breakDuration = breakProgress(entity.level, this.targetBlocks.get(0), entity);
        entity.level.destroyBlockProgress(entity.getId(), targetBlocks.get(0), (int) this.breakDuration);
        this.destroyProgressStart = entity.tickCount;
    }

    private float breakProgress(Level pLevel, BlockPos pPos, E entity) {
        float f = this.currentBlockState.getDestroySpeed(pLevel, pPos);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = !this.requiresProperTool || isCorrectToolForDrops(entity) ? 30 : 100;
            return getDigSpeed(this.currentBlockState, pPos, entity) / f / (float) i;
        }
    }

    private boolean isCorrectToolForDrops(E entity) {
        if (!this.currentBlockState.requiresCorrectToolForDrops())
            return true;

        return getTool(entity).isCorrectToolForDrops(this.currentBlockState);
    }

    @Override
    public void tick(ServerLevel pLevel, E entity, long pGameTime) {
        if (this.targetBlocks.isEmpty())
            return;
        if (this.requiresProperTool && this.currentBlockState != null && !this.isCorrectToolForDrops(entity))
            return;
        if(entity instanceof Mob mob) {
            mob.getLookControl().setLookAt(this.targetBlocks.get(0).getX() + 0.5d, this.targetBlocks.get(0).getY() + 0.5d, this.targetBlocks.get(0).getZ() + 0.5d);
        }
        float reachDistance = this.getReachDistance(entity);
        if (entity.distanceToSqr(blockPosToVec3(this.targetBlocks.get(0))) > reachDistance * reachDistance) {
            Vec3 posTowards = DefaultRandomPos.getPosTowards((PathfinderMob) entity, 3, 4, Vec3.atBottomCenterOf(this.targetBlocks.get(0)), (double)((float)Math.PI / 2F));
            if (posTowards != null) {
                entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(posTowards, 0.5f, 1));
            }else{
                this.targetBlocks.clear();
                selectBlocksToDig(entity);
                if (!this.targetBlocks.isEmpty())
                    initBlockBreak(entity);
            }
            return;
        } else {
            entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        }
        int destroyTicks = entity.tickCount - this.destroyProgressStart;
        if (destroyTicks % 6 == 0) {
            entity.swing(InteractionHand.MAIN_HAND);
        }
        if (destroyTicks % 4 == 0) {
            SoundType soundType = this.currentBlockState.getSoundType(entity.level, this.targetBlocks.get(0), entity);
            entity.level.playSound(null, this.targetBlocks.get(0), soundType.getHitSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        }
        float f1 = this.breakDuration * (float) (destroyTicks + 1);
        if (f1 >= 0.7F) {
            RaidManager raidManager = RaidManagerHelper.getRaidManagerCapability(entity.level);
            Raid raid = raidManager.getRaidAt(targetBlocks.get(0));
            setReconstructBlock(entity.level, targetBlocks.get(0), entity.level.getBlockState(targetBlocks.get(0)), raid, entity);
            this.targetBlocks.remove(0);
            if (!this.targetBlocks.isEmpty()) {
                initBlockBreak(entity);
            }else{
                if(entity instanceof Mob mob && mob.getNavigation().createPath(this.targetPos, 0) == null){
                    selectBlocksToDig(entity);
                    if (!this.targetBlocks.isEmpty())
                        initBlockBreak(entity);
                }else{
                    factionEntityCapability.setStuck(false);
                    entity.getBrain().eraseMemory(ModMemoryModuleTypes.IS_STUCK.get());
                }
            }
        }
    }
    private void selectBlocksToDig(E entity) {
        int mobHeight = Mth.ceil(entity.getBbHeight());
        for (int i = 0; i < mobHeight; i++) {
            Vec3 vecFrom = entity.position().add(0, i+0.51D, 0);
            Vec3 vecTo = getVecTo(targetPos, vecFrom, i, entity);
            BlockHitResult rayTraceResult = entity.level.clip(new ClipContext(vecFrom, vecTo, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
            if (rayTraceResult.getType() == HitResult.Type.MISS)
                continue;
            if (this.targetBlocks.contains(rayTraceResult.getBlockPos()))
                continue;

            float reachDistance = getReachDistance(entity) + 2;
            double distance = entity.distanceToSqr(rayTraceResult.getLocation());
            if (distance > reachDistance * reachDistance)
                continue;

            BlockState state = entity.level.getBlockState(rayTraceResult.getBlockPos());

            if (state.hasBlockEntity() || state.getDestroySpeed(entity.level, rayTraceResult.getBlockPos()) == -1)
                continue;

            this.targetBlocks.add(rayTraceResult.getBlockPos());
        }
        // sort target blocks by distance to the mob
        this.targetBlocks.sort(Comparator.comparingDouble(value -> entity.distanceToSqr(blockPosToVec3(value))));
    }

    @NotNull
    private Vec3 getVecTo(BlockPos targetPos, Vec3 originVec, int i, E entity) {
        // Get the direction vector from the origin to the target position
        Vec3 directionVec = new Vec3(targetPos.getX(), targetPos.getY() + i, targetPos.getZ()).subtract(originVec).normalize();

        // Calculate the horizontal angle between the direction vector and the x-axis
        double horizontalAngle = Math.atan2(directionVec.z, directionVec.x);

        // Calculate the vertical angle between the direction vector and the y-axis
        double verticalAngle = Math.asin(directionVec.y);

        // Calculate the new direction vector with a 20-degree horizontal offset
        double newHorizontalAngle = horizontalAngle + Math.toRadians(45);
        double newX = Math.cos(newHorizontalAngle);
        double newZ = Math.sin(newHorizontalAngle);
        Vec3 newDirectionVec = new Vec3(newX, directionVec.y, newZ).normalize();

        // Calculate the new direction vector with a vertical offset limited to +/- 45 degrees
        double angleFromHorizontal = Math.acos(newDirectionVec.y);
        double angleLimit = Math.toRadians(45);
        double limitedAngleFromHorizontal = Math.min(Math.max(angleFromHorizontal, -angleLimit), angleLimit);
        double newY = Math.sin(limitedAngleFromHorizontal);
        double newMagnitude = Math.sqrt(newDirectionVec.x * newDirectionVec.x + newDirectionVec.z * newDirectionVec.z);
        Vec3 newVerticalVec = new Vec3(0, newY, 0).normalize().scale(newMagnitude);

        // Return the final vector with both horizontal and vertical offsets
        return newDirectionVec.add(newVerticalVec).scale(getReachDistance(entity)*2).add(originVec);
    }

    private boolean canToolDig(E entity) {
        for (ToolAction action : digActions) {
            if (this.getTool(entity).canPerformAction(action))
                return true;
        }
        return false;
    }

    public float getDigSpeed(BlockState pState, @Nullable BlockPos pos, E entity) {
        float f = this.getTool(entity).getDestroySpeed(pState);
        if (f > 1.0F) {
            int i = EnchantmentHelper.getBlockEfficiency(entity);
            ItemStack itemstack = getTool(entity);
            if (i > 0 && !itemstack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (MobEffectUtil.hasDigSpeed(entity)) {
            f *= 1.0F + (float) (MobEffectUtil.getDigSpeedAmplification(entity) + 1) * 0.2F;
        }

        if (entity.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float f1;
            switch (entity.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (entity.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(entity)) {
            f /= 5.0F;
        }

        if (!entity.isOnGround()) {
            f /= 5.0F;
        }

        return f;
    }

    private ItemStack getTool(E entity) {
        return entity.getItemBySlot(this.hand);
    }


}
