package com.patrigan.faction_craft.entity.ai.target;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;

import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class NearestFactionEnemyTargetGoal extends TargetGoal {
   protected final int randomInterval;
   protected LivingEntity target;
   /** This filter is applied to the Entity search. Only matching entities will be targeted. */
   protected TargetingConditions targetConditions;

   public NearestFactionEnemyTargetGoal(Mob mobEntity, int randomInterval, boolean mustSee, boolean mustReach) {
      super(mobEntity, mustSee, mustReach);
      this.randomInterval = randomInterval;
      this.setFlags(EnumSet.of(Flag.TARGET));
      this.targetConditions = (TargetingConditions.forCombat()).range(this.getFollowDistance()).selector(this::hasEnemyFaction);
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean canUse() {
      if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
         return false;
      } else {
         this.findTarget();
         return this.target != null;
      }
   }

   protected AABB getTargetSearchArea(double pTargetDistance) {
      return this.mob.getBoundingBox().inflate(pTargetDistance, 4.0D, pTargetDistance);
   }

   protected void findTarget() {
      this.target = this.mob.level.getNearestEntity(Mob.class, this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.getTargetSearchArea(this.getFollowDistance()));
   }

   private boolean hasEnemyFaction(LivingEntity livingEntity) {
      if(livingEntity instanceof Mob){
         Mob targetMob = (Mob) livingEntity;
         FactionEntity targetCap = FactionEntityHelper.getFactionEntityCapability(targetMob);
         FactionEntity sourceCap = FactionEntityHelper.getFactionEntityCapability(this.mob);
         if(sourceCap == null || targetCap == null){
            return false;
         }
         return sourceCap.getFaction() != null && targetCap.getFaction() != null && sourceCap.getFaction().getRelations().getEnemies().contains(targetCap.getFaction().getName());
      }
      return false;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void start() {
      this.mob.setTarget(this.target);
      super.start();
   }

   public void setTarget(@Nullable LivingEntity pTarget) {
      this.target = pTarget;
   }
}