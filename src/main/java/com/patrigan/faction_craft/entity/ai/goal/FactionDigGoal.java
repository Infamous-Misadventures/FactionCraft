package com.patrigan.faction_craft.entity.ai.goal;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.Goal;
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

public class FactionDigGoal extends Goal {

    private static final Set<ToolAction> digActions = Set.of(ToolActions.SHOVEL_DIG, ToolActions.AXE_DIG, ToolActions.PICKAXE_DIG);

    private final Mob mob;
    private final boolean requiresTool;
    private final boolean requiresProperTool;
    private final List<BlockPos> targetBlocks = new ArrayList<>();
    private final EquipmentSlot hand;
    private final FactionEntity factionEntityCapability;
    private BlockState currentBlockState = null;
    private float breakDuration = 0;
    private int destroyProgressStart = 0;
    private BlockPos targetPos = null;

    public FactionDigGoal(Mob mob, boolean requiresTool, boolean requiresProperTool, EquipmentSlot hand) {
        this.mob = mob;
        this.factionEntityCapability = FactionEntityHelper.getFactionEntityCapability(mob);
        this.requiresTool = requiresTool;
        this.requiresProperTool = requiresProperTool;
        this.hand = hand;
    }

    @Override
    public boolean canUse() {
        if (this.requiresTool && !canToolDig()) {
            return false;
        }
        if(!factionEntityCapability.isStuck()) {
            return false;
        }
        if(factionEntityCapability.getTargetPosition() == null) {
            if (this.mob.getNavigation().isDone() || this.mob.getNavigation().getTargetPos() == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.requiresTool && !canToolDig()) {
            return false;
        }
        return !this.targetBlocks.isEmpty();
    }

    private float getReachDistance() {
        AttributeInstance reachDistanceAttribute = this.mob.getAttribute(REACH_DISTANCE.get());
        return reachDistanceAttribute != null ? (float) reachDistanceAttribute.getValue() : 4.5F;
    }

    @Override
    public void start() {
        if(factionEntityCapability.getTargetPosition() != null) {
            this.targetPos = factionEntityCapability.getTargetPosition();
        }else{
            this.targetPos = this.mob.getNavigation().getTargetPos();
        }
        selectBlocksToDig();
        if (!this.targetBlocks.isEmpty())
            initBlockBreak();
    }

    private void initBlockBreak() {
        this.currentBlockState = this.mob.level.getBlockState(this.targetBlocks.get(0));
        this.breakDuration = breakProgress(mob.level, this.targetBlocks.get(0));
        this.mob.level.destroyBlockProgress(this.mob.getId(), targetBlocks.get(0), (int) this.breakDuration);
        this.destroyProgressStart = this.mob.tickCount;
    }

    private float breakProgress(Level pLevel, BlockPos pPos) {
        float f = this.currentBlockState.getDestroySpeed(pLevel, pPos);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = !this.requiresProperTool || isCorrectToolForDrops() ? 30 : 100;
            return getDigSpeed(this.currentBlockState, pPos) / f / (float) i;
        }
    }

    private boolean isCorrectToolForDrops() {
        if (!this.currentBlockState.requiresCorrectToolForDrops())
            return true;

        return getTool().isCorrectToolForDrops(this.currentBlockState);
    }

    public void tick() {
        if (this.targetBlocks.isEmpty())
            return;
        if (this.requiresProperTool && this.currentBlockState != null && !this.isCorrectToolForDrops())
            return;
        this.mob.getLookControl().setLookAt(this.targetBlocks.get(0).getX() + 0.5d, this.targetBlocks.get(0).getY() + 0.5d, this.targetBlocks.get(0).getZ() + 0.5d);
        float reachDistance = this.getReachDistance();
        if (this.mob.distanceToSqr(blockPosToVec3(this.targetBlocks.get(0))) > reachDistance * reachDistance) {
            Vec3 posTowards = DefaultRandomPos.getPosTowards((PathfinderMob) this.mob, 3, 4, Vec3.atBottomCenterOf(this.targetBlocks.get(0)), (double)((float)Math.PI / 2F));
            if (posTowards != null) {
                this.mob.getNavigation().moveTo(posTowards.x+0.5D, posTowards.y, posTowards.z+0.5D, 1.0d);
            }else{
                this.targetBlocks.clear();
                selectBlocksToDig();
                if (!this.targetBlocks.isEmpty())
                    initBlockBreak();
            }
            return;
        } else {
            this.mob.getNavigation().stop();
        }
        int destroyTicks = this.mob.tickCount - this.destroyProgressStart;
        if (destroyTicks % 6 == 0) {
            this.mob.swing(InteractionHand.MAIN_HAND);
        }
        if (destroyTicks % 4 == 0) {
            SoundType soundType = this.currentBlockState.getSoundType(this.mob.level, this.targetBlocks.get(0), this.mob);
            this.mob.level.playSound(null, this.targetBlocks.get(0), soundType.getHitSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        }
        float f1 = this.breakDuration * (float) (destroyTicks + 1);
        if (f1 >= 0.7F) {
            RaidManager raidManager = RaidManagerHelper.getRaidManagerCapability(this.mob.level);
            Raid raid = raidManager.getRaidAt(targetBlocks.get(0));
            setReconstructBlock(this.mob.level, targetBlocks.get(0), this.mob.level.getBlockState(targetBlocks.get(0)), raid, this.mob);
            this.targetBlocks.remove(0);
            if (!this.targetBlocks.isEmpty()) {
                initBlockBreak();
            }else{
                if(this.mob.getNavigation().createPath(this.targetPos, 0) == null){
                    selectBlocksToDig();
                    if (!this.targetBlocks.isEmpty())
                        initBlockBreak();
                }else{
                    factionEntityCapability.setStuck(false);
                }
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void selectBlocksToDig() {
        int mobHeight = Mth.ceil(this.mob.getBbHeight());
        for (int i = 0; i < mobHeight; i++) {
            Vec3 vecFrom = this.mob.position().add(0, i+0.51D, 0);
            Vec3 vecTo = getVecTo(targetPos, vecFrom, i);
            BlockHitResult rayTraceResult = this.mob.level.clip(new ClipContext(vecFrom, vecTo, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob));
            if (rayTraceResult.getType() == HitResult.Type.MISS)
                continue;
            if (this.targetBlocks.contains(rayTraceResult.getBlockPos()))
                continue;

            float reachDistance = getReachDistance() + 2;
            double distance = this.mob.distanceToSqr(rayTraceResult.getLocation());
            if (distance > reachDistance * reachDistance)
                continue;

            BlockState state = this.mob.level.getBlockState(rayTraceResult.getBlockPos());

            if (state.hasBlockEntity() || state.getDestroySpeed(this.mob.level, rayTraceResult.getBlockPos()) == -1)
                continue;

            this.targetBlocks.add(rayTraceResult.getBlockPos());
        }
        // sort target blocks by distance to the mob
        this.targetBlocks.sort(Comparator.comparingDouble(value -> this.mob.distanceToSqr(blockPosToVec3(value))));
    }

    @NotNull
    private Vec3 getVecTo(BlockPos targetPos, Vec3 originVec, int i) {
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
        return newDirectionVec.add(newVerticalVec).scale(getReachDistance()*2).add(originVec);
    }

    private boolean canToolDig() {
        for (ToolAction action : digActions) {
            if (this.getTool().canPerformAction(action))
                return true;
        }
        return false;
    }

    public float getDigSpeed(BlockState pState, @Nullable BlockPos pos) {
        float f = this.getTool().getDestroySpeed(pState);
        if (f > 1.0F) {
            int i = EnchantmentHelper.getBlockEfficiency(this.mob);
            ItemStack itemstack = getTool();
            if (i > 0 && !itemstack.isEmpty()) {
                f += (float) (i * i + 1);
            }
        }

        if (MobEffectUtil.hasDigSpeed(this.mob)) {
            f *= 1.0F + (float) (MobEffectUtil.getDigSpeedAmplification(this.mob) + 1) * 0.2F;
        }

        if (this.mob.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float f1;
            switch (this.mob.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
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

        if (this.mob.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this.mob)) {
            f /= 5.0F;
        }

        if (!this.mob.isOnGround()) {
            f /= 5.0F;
        }

        return f;
    }

    private ItemStack getTool() {
        return this.mob.getItemBySlot(this.hand);
    }
}
