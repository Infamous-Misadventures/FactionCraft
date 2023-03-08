package com.patrigan.faction_craft.entity.ai.goal;

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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.Goal;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.patrigan.faction_craft.block.ReconstructBlock.setReconstructBlock;
import static net.minecraftforge.common.ForgeMod.REACH_DISTANCE;

public class FactionDigGoal extends Goal {

    private static final Set<ToolAction> digActions = Set.of(ToolActions.SHOVEL_DIG, ToolActions.AXE_DIG, ToolActions.PICKAXE_DIG);

    private final Mob mob;
    private final boolean requiresTool;
    private final boolean requiresProperTool;
    private final List<BlockPos> targetBlocks = new ArrayList<>();
    private final EquipmentSlot hand;
    private BlockState currentBlockState = null;
    private float breakDuration = 0;
    private int destroyProgressStart = 0;
    private int prevBreakingProgressTicks;
    private int lastStuckCheck = 0;
    private Vec3 lastStuckCheckPos;

    public FactionDigGoal(Mob mob, boolean requiresTool, boolean requiresProperTool, EquipmentSlot hand) {
        this.mob = mob;
        this.lastStuckCheck = mob.tickCount;
        this.lastStuckCheckPos = mob.position();
        this.requiresTool = requiresTool;
        this.requiresProperTool = requiresProperTool;
        this.hand = hand;
    }

    @Override
    public boolean canUse() {
        if (this.requiresTool && !canToolDig()) {
            return false;
        }
        if(this.mob.getNavigation().isDone() || this.mob.getNavigation().getTargetPos() == null){
            return false;
        }
        return doStuckCheck();
    }

    private boolean doStuckCheck() {
        if (this.mob.tickCount - this.lastStuckCheck > 75) {
            if (mob.position().distanceToSqr(this.lastStuckCheckPos) < 2.25D) {
                return true;
            }

            this.lastStuckCheck = this.mob.tickCount;
            this.lastStuckCheckPos = mob.position();
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        float reachDistance = getReachDistance();
        return this.canUse() && !this.targetBlocks.isEmpty()
                && this.targetBlocks.get(0).distSqr(this.mob.blockPosition()) < reachDistance * reachDistance;
    }

    private float getReachDistance() {
        AttributeInstance reachDistanceAttribute = this.mob.getAttribute(REACH_DISTANCE.get());
        return reachDistanceAttribute != null ? (float) reachDistanceAttribute.getValue() : 4.5F;
    }

    @Override
    public void start() {
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
            return getDigSpeed(this.currentBlockState, pPos) / f / (float)i;
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
        int destroyTicks = this.mob.tickCount - this.destroyProgressStart;
        if (destroyTicks % 6 == 0) {
            this.mob.swing(InteractionHand.MAIN_HAND);
        }
        if (destroyTicks % 4 == 0) {
            SoundType soundType = this.currentBlockState.getSoundType(this.mob.level, this.targetBlocks.get(0), this.mob);
            this.mob.level.playSound(null, this.targetBlocks.get(0), soundType.getHitSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        }
        float f1 = this.breakDuration * (float)(destroyTicks + 1);
        if (f1 >= 0.7F) {
//            this.mob.level.destroyBlock(targetBlocks.get(0), true, this.mob);
//            this.mob.level.destroyBlockProgress(this.mob.getId(), targetBlocks.get(0), -1);
            RaidManager raidManager = RaidManagerHelper.getRaidManagerCapability(this.mob.level);
            Raid raid = raidManager.getRaidAt(targetBlocks.get(0));
            if(raid != null && !raid.isOver()) {
                setReconstructBlock(this.mob.level, targetBlocks.get(0), this.mob.level.getBlockState(targetBlocks.get(0)), raid);
            }
            this.targetBlocks.remove(0);
            if (!this.targetBlocks.isEmpty())
                initBlockBreak();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private void selectBlocksToDig() {
        int mobHeight = Mth.ceil(this.mob.getBbHeight());
        BlockPos targetPos = this.mob.getNavigation().getTargetPos();
        for (int i = 0; i < mobHeight; i++) {
            BlockHitResult rayTraceResult = this.mob.level.clip(new ClipContext(this.mob.position().add(0, i + 0.5d, 0), new Vec3(targetPos.getX(), targetPos.getY()+i, targetPos.getZ()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob));
            if (rayTraceResult.getType() == HitResult.Type.MISS)
                continue;
            if (this.targetBlocks.contains(rayTraceResult.getBlockPos()))
                continue;
//			if (rayTraceResult.getBlockPos().getY() > Modules.zombie.diggerMob.maxYDig)
//				continue;

            float reachDistance = getReachDistance();
            double distance = this.mob.distanceToSqr(rayTraceResult.getLocation());
            if (distance > reachDistance * reachDistance)
                continue;

            BlockState state = this.mob.level.getBlockState(rayTraceResult.getBlockPos());

            if (state.hasBlockEntity() || state.getDestroySpeed(this.mob.level, rayTraceResult.getBlockPos()) == -1)
                continue;

//			if (Modules.zombie.diggerMob.blockBlacklist.isBlockBlackOrNotWhiteListed(state.getBlock()))
//				continue;

            this.targetBlocks.add(rayTraceResult.getBlockPos());
        }
        Collections.reverse(this.targetBlocks);
    }

    private boolean canToolDig() {
        for(ToolAction action : digActions) {
            if(this.getTool().canPerformAction(action))
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
                f += (float)(i * i + 1);
            }
        }

        if (MobEffectUtil.hasDigSpeed(this.mob)) {
            f *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(this.mob) + 1) * 0.2F;
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
