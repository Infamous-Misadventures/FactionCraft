package com.patrigan.faction_craft.entity.ai.goal;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.IFactionEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class NearestFactionEnemyTargetGoal<T extends LivingEntity> extends TargetGoal {
   protected final int randomInterval;
   protected LivingEntity target;
   /** This filter is applied to the Entity search. Only matching entities will be targeted. */
   protected EntityPredicate targetConditions;

   public NearestFactionEnemyTargetGoal(MobEntity mobEntity, int randomInterval, boolean mustSee, boolean mustReach) {
      super(mobEntity, mustSee, mustReach);
      this.randomInterval = randomInterval;
      this.setFlags(EnumSet.of(Flag.TARGET));
      this.targetConditions = (new EntityPredicate()).range(this.getFollowDistance()).selector(this::hasEnemyFaction);
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

   protected AxisAlignedBB getTargetSearchArea(double pTargetDistance) {
      return this.mob.getBoundingBox().inflate(pTargetDistance, 4.0D, pTargetDistance);
   }

   protected void findTarget() {
      this.target = this.mob.level.getNearestLoadedEntity(MobEntity.class, this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.getTargetSearchArea(this.getFollowDistance()));
   }

   private boolean hasEnemyFaction(LivingEntity livingEntity) {
      if(livingEntity instanceof MobEntity){
         MobEntity targetMob = (MobEntity) livingEntity;
         IFactionEntity targetCap = FactionEntityHelper.getFactionEntityCapability(targetMob);
         IFactionEntity sourceCap = FactionEntityHelper.getFactionEntityCapability(this.mob);
         if(sourceCap.getFaction() != null && targetCap.getFaction() != null && sourceCap.getFaction().getRelations().getEnemies().contains(targetCap.getFaction().getName())){
            return true;
         }
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