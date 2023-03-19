package com.patrigan.faction_craft.entity.ai.goal;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.faction.Faction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class NearestFactionAllyTargetGoal<T extends LivingEntity> extends TargetGoal {
   private static final int DEFAULT_RANDOM_INTERVAL = 10;
   protected final Class<T> targetType;
   protected final int randomInterval;
   @javax.annotation.Nullable
   protected LivingEntity target;
   /** This filter is applied to the Entity search. Only matching entities will be targeted. */
   protected TargetingConditions targetConditions;

   public NearestFactionAllyTargetGoal(Mob pMob, Class<T> pTargetType, boolean pMustSee) {
      this(pMob, pTargetType, DEFAULT_RANDOM_INTERVAL, pMustSee, false, (Predicate<LivingEntity>)null);
   }

   public NearestFactionAllyTargetGoal(Mob pMob, Class<T> pTargetType, boolean pMustSee, Predicate<LivingEntity> pTargetPredicate) {
      this(pMob, pTargetType, DEFAULT_RANDOM_INTERVAL, pMustSee, false, pTargetPredicate);
   }

   public NearestFactionAllyTargetGoal(Mob pMob, Class<T> pTargetType, boolean pMustSee, boolean pMustReach) {
      this(pMob, pTargetType, DEFAULT_RANDOM_INTERVAL, pMustSee, pMustReach, (Predicate<LivingEntity>)null);
   }

   public NearestFactionAllyTargetGoal(Mob pMob, Class<T> pTargetType, int pRandomInterval, boolean pMustSee, boolean pMustReach, @javax.annotation.Nullable Predicate<LivingEntity> pTargetPredicate) {
      super(pMob, pMustSee, pMustReach);
      this.targetType = pTargetType;
      this.randomInterval = reducedTickDelay(pRandomInterval);
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
      this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(pTargetPredicate);
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

   public boolean canContinueToUse() {
      FactionEntity factionEntityCapability = FactionEntityHelper.getFactionEntityCapability(this.mob);
      LivingEntity livingentity = factionEntityCapability.getNearestDamagedFactionAlly();
      if (livingentity == null) {
         livingentity = this.targetMob;
      }
      if(! (livingentity instanceof Mob Mob)) return false;
      FactionEntity targetFactionEntityCapability = FactionEntityHelper.getFactionEntityCapability(mob);

      if (livingentity == null) {
         return false;
      } else if (!this.mob.canAttack(livingentity)) {
         return false;
      } else {
         Team team = this.mob.getTeam();
         Team team1 = livingentity.getTeam();
         if (team != null && team1 == team) {
            return false;
         } else if (factionEntityCapability.getFaction() != null && ! factionEntityCapability.getFaction().equals(targetFactionEntityCapability.getFaction())){
            return false;
         } else if(!targetConditions.test(this.mob, livingentity)){
            return false;
         } else {
            double d0 = this.getFollowDistance();
            if (this.mob.distanceToSqr(livingentity) > d0 * d0) {
               return false;
            } else {
               if (this.mustSee) {
                  if (this.mob.getSensing().hasLineOfSight(livingentity)) {
                     this.unseenTicks = 0;
                  } else if (++this.unseenTicks > reducedTickDelay(this.unseenMemoryTicks)) {
                     return false;
                  }
               }

               factionEntityCapability.setNearestDamagedFactionAlly(livingentity);
               return true;
            }
         }
      }
   }

   protected AABB getTargetSearchArea(double pTargetDistance) {
      return this.mob.getBoundingBox().inflate(pTargetDistance, 4.0D, pTargetDistance);
   }

   protected void findTarget() {
      if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
         this.target = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), (p_148152_) -> {
            Faction faction = FactionEntityHelper.getFactionEntityCapability(this.mob).getFaction();
            return faction != null && p_148152_ instanceof Mob && faction.equals(FactionEntityHelper.getFactionEntityCapability((Mob) p_148152_).getFaction());
         }), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
      } else {
         this.target = this.mob.level.getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
      }

   }

   public void stop() {
      FactionEntity factionEntityCapability = FactionEntityHelper.getFactionEntityCapability(this.mob);
      factionEntityCapability.setNearestDamagedFactionAlly(null);
      this.targetMob = null;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void start() {
      FactionEntity factionEntityCapability = FactionEntityHelper.getFactionEntityCapability(this.mob);
      factionEntityCapability.setNearestDamagedFactionAlly(this.target);
      super.start();
   }

   public void setTarget(@Nullable LivingEntity pTarget) {
      this.target = pTarget;
   }
}