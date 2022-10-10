package com.patrigan.faction_craft.entity.ai.target;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

public class FactionAllyHurtTargetGoal extends TargetGoal {
    private LivingEntity attacker;
    private int timestamp;
    private int revengeTimer;
    /** This filter is applied to the Entity search. Only matching entities will be targeted. */
    protected EntityPredicate targetConditions;
    private final int randomInterval;

    public FactionAllyHurtTargetGoal(Mob mobEntity, int randomInterval, boolean mustSee, boolean mustReach) {
        super(mobEntity, mustSee, mustReach);
        this.randomInterval = randomInterval;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            FactionEntity sourceCap = FactionEntityHelper.getFactionEntityCapability(this.mob);
            if (sourceCap.getFaction() == null) {
                return false;
            }
            List<Mob> allies = findHurtAllies();
            if (allies.isEmpty()) {
                return false;
            }
            Mob hurtAlly = allies.get(this.mob.getRandom().nextInt(allies.size()));
            this.attacker = hurtAlly.getLastHurtByMob();
            this.revengeTimer = hurtAlly.getLastHurtByMobTimestamp();
            return revengeTimer != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT);
        }
    }

    protected List<Mob> findHurtAllies() {
        return this.mob.level.getEntitiesOfClass(Mob.class, new AABB(this.mob.blockPosition()).inflate(30),
                this::isPotentialHurtAlly);
    }

    private boolean isPotentialHurtAlly(Mob entity) {
        LivingEntity potentialAttacker = entity.getLastHurtByMob();
        int potentialRevengeTimer = entity.getLastHurtByMobTimestamp();
        return potentialAttacker != null && potentialAttacker != this.mob && potentialRevengeTimer != this.timestamp && this.canAttack(potentialAttacker, TargetingConditions.DEFAULT) && hasSameFaction(entity);
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        this.timestamp = revengeTimer;

        super.start();
    }

    private boolean hasSameFaction(LivingEntity livingEntity) {
        if(livingEntity instanceof Mob targetMob){
            FactionEntity targetCap = FactionEntityHelper.getFactionEntityCapability(targetMob);
            FactionEntity sourceCap = FactionEntityHelper.getFactionEntityCapability(this.mob);
            return sourceCap.getFaction() != null && targetCap.getFaction() != null && sourceCap.getFaction().equals(targetCap.getFaction());
        }
        return false;
    }
}
