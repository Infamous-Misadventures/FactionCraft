package com.patrigan.faction_craft.entity.ai.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import javax.annotation.Nullable;
import java.util.UUID;

public class UseShieldGoal extends Goal {
    private static final UUID SPEED_MODIFIER_BLOCKING_UUID = UUID.fromString("05cd371b-0ff4-4ded-8630-b380232ed7b1");
    private static final AttributeModifier SPEED_MODIFIER_BLOCKING = new AttributeModifier(SPEED_MODIFIER_BLOCKING_UUID,
            "Blocking speed decrease", -0.1D, AttributeModifier.Operation.ADDITION);

    public int blockingFor;
    public int blockDuration;
    public int maxBlockDuration;
    public int blockChance;
    public int stopChanceAfterDurationEnds;
    public double blockDistance;
    public boolean guaranteedBlockIfTargetNotVisible;

    public PathfinderMob mob;
    @Nullable
    public LivingEntity target;

    public UseShieldGoal(PathfinderMob attackingMob, double blockDistance, int blockDuration, int maxBlockDuration, int stopChanceAfterDurationEnds, int blockChance, boolean guaranteedBlockIfTargetNotVisible) {
        this.blockDuration = maxBlockDuration;
        this.mob = attackingMob;
        this.target = attackingMob.getTarget();
        this.blockChance = blockChance;
        this.maxBlockDuration = maxBlockDuration;
        this.stopChanceAfterDurationEnds = stopChanceAfterDurationEnds;
        this.blockDistance = blockDistance;
        this.guaranteedBlockIfTargetNotVisible = guaranteedBlockIfTargetNotVisible;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public boolean shouldBlockForTarget(LivingEntity target) {
        return !(target instanceof Mob) || ((Mob) target).getTarget() == null || ((Mob) target).getTarget() == mob;
    }

    public boolean isShieldDisabled(PathfinderMob shieldUser) {
        return false;
//        return shieldUser instanceof IShieldUser && ((IShieldUser) shieldUser).isShieldDisabled();
    }

    @Override
    public boolean canUse() {
        target = mob.getTarget();
        return target != null && !isShieldDisabled(mob) && shouldBlockForTarget(target) && (((mob.getRandom().nextInt(this.blockChance) == 0 && mob.distanceTo(target) <= blockDistance && mob.hasLineOfSight(target) && mob.getOffhandItem().canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK)) || mob.isBlocking()) || (guaranteedBlockIfTargetNotVisible && !mob.hasLineOfSight(target)));
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && mob.invulnerableTime <= 0 && !isShieldDisabled(mob) && mob.getOffhandItem().canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK);
    }

    @Override
    public void start() {
        mob.startUsingItem(InteractionHand.OFF_HAND);
        AttributeInstance modifiableattributeinstance = this.mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (modifiableattributeinstance != null && !modifiableattributeinstance.hasModifier(SPEED_MODIFIER_BLOCKING)) {
            modifiableattributeinstance.addTransientModifier(SPEED_MODIFIER_BLOCKING);
        }
    }

    @Override
    public void tick() {
        target = mob.getTarget();
        this.blockingFor++;

        if ((this.blockingFor >= this.blockDuration && mob.getRandom().nextInt(this.stopChanceAfterDurationEnds) == 0) || this.blockingFor >= this.maxBlockDuration) {
            this.stop();
        }
    }

    @Override
    public void stop() {
        mob.stopUsingItem();
        AttributeInstance modifiableattributeinstance = this.mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (modifiableattributeinstance != null) {
            modifiableattributeinstance.removeModifier(SPEED_MODIFIER_BLOCKING);
        }
        this.blockingFor = 0;
    }

}
