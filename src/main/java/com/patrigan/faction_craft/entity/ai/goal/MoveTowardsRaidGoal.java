package com.patrigan.faction_craft.entity.ai.goal;

import com.google.common.collect.Sets;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class MoveTowardsRaidGoal<T extends Mob> extends Goal {
    private final T mob;
    private final Raider raiderCapability;
    private final FactionEntity factionEntityCapability;
    private int lastStuckCheck = 0;
    private Vec3 lastStuckCheckPos;

    public MoveTowardsRaidGoal(T p_i50323_1_) {
        this.mob = p_i50323_1_;
        this.raiderCapability = RaiderHelper.getRaiderCapability(this.mob);
        this.factionEntityCapability = FactionEntityHelper.getFactionEntityCapability(this.mob);
        this.lastStuckCheck = mob.tickCount;
        this.lastStuckCheckPos = mob.position();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        return this.mob instanceof PathfinderMob
                && this.mob.getTarget() == null
                && !this.mob.isVehicle() && raiderCapability.hasActiveRaid()
                && !raiderCapability.getRaid().isOver()
                && !((ServerLevel)this.mob.level).isVillage(this.mob.blockPosition());
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return raiderCapability != null && raiderCapability.hasActiveRaid() && this.mob.getTarget() == null && !raiderCapability.getRaid().isOver() && this.mob.level instanceof ServerLevel && !((ServerLevel)this.mob.level).isVillage(this.mob.blockPosition());
    }

    @Override
    public void start() {
        if (raiderCapability.hasActiveRaid()) {
            Raid raid = raiderCapability.getRaid();
            factionEntityCapability.setTargetPosition(raid.getCenter());
        }
        super.start();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        if (raiderCapability.hasActiveRaid()) {
            Raid raid = raiderCapability.getRaid();
            factionEntityCapability.setTargetPosition(raid.getCenter());

            if(FactionCraftConfig.ENABLE_DIGGER_AI.get() && doStuckCheck()) {
                Faction faction = FactionEntityHelper.getFactionEntityCapability(this.mob).getFaction();
                raiderCapability.getRaid().spawnDigger(faction, mob.blockPosition(), this.mob);
                factionEntityCapability.setStuck(true);
            }else{
                factionEntityCapability.setStuck(false);
            }

            if (this.mob.tickCount % 20 == 0) {
                this.recruitNearby(raid);
            }

            if (this.mob.getNavigation().isDone()) {
                Vec3 vector3d = DefaultRandomPos.getPosTowards((PathfinderMob) this.mob, 15, 4, Vec3.atBottomCenterOf(raid.getCenter()), (double)((float)Math.PI / 2F));
                if (vector3d != null) {
                    this.mob.getNavigation().moveTo(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                }
            }
        }
    }

    private boolean doStuckCheck() {
        if (this.mob.tickCount - this.lastStuckCheck > 75) {
            if (mob.position().distanceToSqr(this.lastStuckCheckPos) < 2.25D) {
                this.lastStuckCheck = this.mob.tickCount;
                this.lastStuckCheckPos = mob.position();
                return true;
            }
            this.lastStuckCheck = this.mob.tickCount;
            this.lastStuckCheckPos = mob.position();
        }
        return false;
    }

    private void recruitNearby(Raid pRaid) {
        FactionEntity sourceFactionEntityCapability = FactionEntityHelper.getFactionEntityCapability(this.mob);
        if (pRaid.isActive()) {
            Set<Mob> set = Sets.newHashSet();
            List<Mob> list = this.mob.level.getEntitiesOfClass(Mob.class, this.mob.getBoundingBox().inflate(16.0D), (p_220742_1_) -> {
                FactionEntity targetFactionEntityCapability = FactionEntityHelper.getFactionEntityCapability(p_220742_1_);
                return sourceFactionEntityCapability.getFaction().equals(targetFactionEntityCapability.getFaction()) && !raiderCapability.hasActiveRaid() && RaidManager.canJoinRaid(p_220742_1_, pRaid);
            });
            set.addAll(list);

            for(Mob abstractraiderentity : set) {
                pRaid.joinRaid(pRaid.getGroupsSpawned(), abstractraiderentity, true);
            }
        }
    }
}