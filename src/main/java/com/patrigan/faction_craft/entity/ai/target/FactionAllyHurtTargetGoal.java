package com.patrigan.faction_craft.entity.ai.target;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.IFactionEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.EnumSet;
import java.util.List;

public class FactionAllyHurtTargetGoal extends TargetGoal {
    private LivingEntity attacker;
    private int timestamp;
    private int revengeTimer;
    /** This filter is applied to the Entity search. Only matching entities will be targeted. */
    protected EntityPredicate targetConditions;
    private final int randomInterval;

    public FactionAllyHurtTargetGoal(MobEntity mobEntity, int randomInterval, boolean mustSee, boolean mustReach) {
        super(mobEntity, mustSee, mustReach);
        this.randomInterval = randomInterval;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            IFactionEntity sourceCap = FactionEntityHelper.getFactionEntityCapability(this.mob);
            if (sourceCap.getFaction() == null) {
                return false;
            }
            List<MobEntity> allies = findHurtAllies();
            if (allies.isEmpty()) {
                return false;
            }
            MobEntity hurtAlly = allies.get(this.mob.getRandom().nextInt(allies.size()));
            this.attacker = hurtAlly.getLastHurtByMob();
            this.revengeTimer = hurtAlly.getLastHurtByMobTimestamp();
            return revengeTimer != this.timestamp && this.canAttack(this.attacker, EntityPredicate.DEFAULT);
        }
    }

    protected List<MobEntity> findHurtAllies() {
        return this.mob.level.getLoadedEntitiesOfClass(MobEntity.class, new AxisAlignedBB(this.mob.blockPosition()).inflate(30),
                this::isPotentialHurtAlly);
    }

    private boolean isPotentialHurtAlly(MobEntity entity) {
        LivingEntity potentialAttacker = entity.getLastHurtByMob();
        int potentialRevengeTimer = entity.getLastHurtByMobTimestamp();
        return potentialAttacker != null && potentialAttacker != this.mob && potentialRevengeTimer != this.timestamp && this.canAttack(potentialAttacker, EntityPredicate.DEFAULT) && hasSameFaction(entity);
    }

    public void start() {
        this.mob.setTarget(this.attacker);
        this.timestamp = revengeTimer;

        super.start();
    }

    private boolean hasSameFaction(LivingEntity livingEntity) {
        if(livingEntity instanceof MobEntity){
            MobEntity targetMob = (MobEntity) livingEntity;
            IFactionEntity targetCap = FactionEntityHelper.getFactionEntityCapability(targetMob);
            IFactionEntity sourceCap = FactionEntityHelper.getFactionEntityCapability(this.mob);
            return sourceCap.getFaction() != null && targetCap.getFaction() != null && sourceCap.getFaction().equals(targetCap.getFaction());
        }
        return false;
    }
}
