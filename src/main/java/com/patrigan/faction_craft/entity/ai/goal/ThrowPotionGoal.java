package com.patrigan.faction_craft.entity.ai.goal;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Supplier;

public class ThrowPotionGoal extends Goal {
    private final Mob mob;
    private final double speedModifier;
    private final int attackIntervalMin;
    private final int attackIntervalMax;
    private final float attackRadius;
    private final float attackRadiusSqr;
    private final Potion potion;
    @Nullable
    private LivingEntity target;
    private int attackTime = -1;
    private int seeTime;
    private Supplier<LivingEntity> targetSupplier;

    public ThrowPotionGoal(Mob pMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius, Potion potion, Supplier<LivingEntity> targetSupplier) {
        this(pMob, pSpeedModifier, pAttackInterval, pAttackInterval, pAttackRadius, potion, targetSupplier);
    }

    public ThrowPotionGoal(Mob pMob, double pSpeedModifier, int pAttackIntervalMin, int pAttackIntervalMax, float pAttackRadius, Potion potion, Supplier<LivingEntity> targetSupplier) {
        this.potion = potion;
        this.mob = (Mob) pMob;
        this.speedModifier = pSpeedModifier;
        this.attackIntervalMin = pAttackIntervalMin;
        this.attackIntervalMax = pAttackIntervalMax;
        this.attackRadius = pAttackRadius;
        this.attackRadiusSqr = pAttackRadius * pAttackRadius;
        this.targetSupplier = targetSupplier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        LivingEntity livingentity = targetSupplier.get();
        if (livingentity != null && livingentity.isAlive()) {
            this.target = livingentity;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.attackTime = -1;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
        if (flag) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.target, this.speedModifier);
        }

        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        if (--this.attackTime == 0) {
            if (!flag) {
                return;
            }

            float f = (float) Math.sqrt(d0) / this.attackRadius;
            float f1 = Mth.clamp(f, 0.1F, 1.0F);
            this.throwPotion(this.target, f1);
            this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
        } else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, (double) this.attackIntervalMin, (double) this.attackIntervalMax));
        }
    }

    private void throwPotion(LivingEntity pTarget, float f1) {
        Vec3 vec3 = pTarget.getDeltaMovement();
        double d0 = pTarget.getX() + vec3.x - this.mob.getX();
        double d1 = pTarget.getEyeY() - (double) 1.1F - this.mob.getY();
        double d2 = pTarget.getZ() + vec3.z - this.mob.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        ThrownPotion thrownpotion = new ThrownPotion(this.mob.level, this.mob);
        thrownpotion.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
        thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
        thrownpotion.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
        if (!this.mob.isSilent()) {
            this.mob.level.playSound((Player) null, this.mob.getX(), this.mob.getY(), this.mob.getZ(), SoundEvents.WITCH_THROW, this.mob.getSoundSource(), 1.0F, 0.8F + this.mob.getRandom().nextFloat() * 0.4F);
        }
        this.mob.level.addFreshEntity(thrownpotion);
    }
}