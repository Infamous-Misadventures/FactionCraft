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

    public FactionAllyHurtTargetGoal(Mob mobEntity, boolean mustSee, boolean mustReach) {
        super(mobEntity, mustSee, mustReach);
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        List<Mob> allies = findHurtAllies();
        if(allies.isEmpty()){
            return false;
        }
        Mob hurtAlly = allies.get(this.mob.getRandom().nextInt(allies.size()));
        this.attacker = hurtAlly.getLastHurtByMob();
        this.revengeTimer = hurtAlly.getLastHurtByMobTimestamp();
        return revengeTimer != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT);
    }

    protected List<Mob> findHurtAllies() {
        return this.mob.level.getEntitiesOfClass(Mob.class, new AABB(this.mob.blockPosition()).inflate(30),
                this::isPotentialHurtAlly);
    }

    private boolean isPotentialHurtAlly(Mob entity) {
        if(!hasSameFaction(entity)) return false;
        LivingEntity potentialAttacker = entity.getLastHurtByMob();
        int potentialRevengeTimer = entity.getLastHurtByMobTimestamp();
        return potentialAttacker != null && potentialAttacker != this.mob && potentialRevengeTimer != this.timestamp && this.canAttack(potentialAttacker, TargetingConditions.DEFAULT);
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        this.timestamp = revengeTimer;

        super.start();
    }

    private boolean hasSameFaction(LivingEntity livingEntity) {
        if(livingEntity instanceof Mob){
            Mob targetMob = (Mob) livingEntity;
            FactionEntity targetCap = FactionEntityHelper.getFactionEntityCapability(targetMob);
            FactionEntity sourceCap = FactionEntityHelper.getFactionEntityCapability(this.mob);
            if(sourceCap == null || targetCap == null){
                return false;
            }
            if(sourceCap.getFaction() != null && targetCap.getFaction() != null && sourceCap.getFaction().equals(targetCap.getFaction())){
                return true;
            }
        }
        return false;
    }
}
