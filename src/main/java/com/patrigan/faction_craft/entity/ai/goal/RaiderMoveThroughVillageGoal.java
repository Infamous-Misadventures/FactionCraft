package com.patrigan.faction_craft.entity.ai.goal;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class RaiderMoveThroughVillageGoal extends Goal {
    private final Mob mob;
    private final double speedModifier;
    private final List<BlockPos> visited = Lists.newArrayList();
    private final int distanceToPoi;
    private BlockPos poiPos;
    private boolean stuck;
    private int lastStuckCheck = 0;
    private Vec3 lastStuckCheckPos;

    public RaiderMoveThroughVillageGoal(Mob p_i50570_1_, double p_i50570_2_, int p_i50570_4_) {
        this.mob = p_i50570_1_;
        this.lastStuckCheck = mob.tickCount;
        this.lastStuckCheckPos = mob.position();
        this.speedModifier = p_i50570_2_;
        this.distanceToPoi = p_i50570_4_;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        this.updateVisited();
        return this.mob instanceof PathfinderMob && this.isValidRaid() && this.hasSuitablePoi() && this.mob.getTarget() == null;
    }

    private boolean isValidRaid() {
        Raider raiderCapability = RaiderHelper.getRaiderCapability(this.mob);
        return raiderCapability.hasActiveRaid() && !raiderCapability.getRaid().isOver();
    }

    private boolean hasSuitablePoi() {
        ServerLevel serverlevel = (ServerLevel) this.mob.level;
        BlockPos blockpos = this.mob.blockPosition();
        Optional<BlockPos> optional = serverlevel.getPoiManager().getRandom(poiType -> poiType.is(PoiTypes.HOME), this::hasNotVisited, PoiManager.Occupancy.ANY, blockpos, 48, this.mob.getRandom());
        if (optional.isEmpty()) {
            return false;
        } else {
            this.poiPos = optional.get().immutable();
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        if (this.mob.getNavigation().isDone()) {
            return false;
        } else {
            return this.mob.getTarget() == null && !this.poiPos.closerToCenterThan(this.mob.position(), this.mob.getBbWidth() + (float) this.distanceToPoi);
        }
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        if (this.poiPos.closerToCenterThan(this.mob.position(), this.distanceToPoi)) {
            this.visited.add(this.poiPos);
        }

    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        super.start();
        this.mob.setNoActionTime(0);
        this.mob.getNavigation().moveTo(this.poiPos.getX(), this.poiPos.getY(), this.poiPos.getZ(), this.speedModifier);
        this.stuck = false;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        Raider raiderCapability = RaiderHelper.getRaiderCapability(this.mob);
        if (raiderCapability.hasActiveRaid()) {
            Raid raid = raiderCapability.getRaid();
            if (doStuckCheck()) {

                Faction faction = FactionEntityHelper.getFactionEntityCapability(this.mob).getFaction();
                raiderCapability.getRaid().spawnDigger(faction, mob.blockPosition());
            }

            if (this.mob.tickCount % 20 == 0) {
                this.recruitNearby(raid);
            }
        }
        if (this.mob.getNavigation().isDone()) {
            Vec3 vector3d = Vec3.atBottomCenterOf(this.poiPos);
            Vec3 vector3d1 = DefaultRandomPos.getPosTowards((PathfinderMob) this.mob, 16, 7, vector3d, (double) ((float) Math.PI / 10F));
            if (vector3d1 == null) {
                vector3d1 = DefaultRandomPos.getPosTowards((PathfinderMob) this.mob, 8, 7, vector3d, (double) ((float) Math.PI / 2F));
            }

            if (vector3d1 == null) {
                this.stuck = true;
                return;
            }

            this.mob.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, this.speedModifier);
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
                Raider raiderCapability = RaiderHelper.getRaiderCapability(p_220742_1_);
                FactionEntity targetFactionEntityCapability = FactionEntityHelper.getFactionEntityCapability(p_220742_1_);
                return sourceFactionEntityCapability.getFaction().equals(targetFactionEntityCapability.getFaction()) && !raiderCapability.hasActiveRaid() && RaidManager.canJoinRaid(p_220742_1_, pRaid);
            });
            set.addAll(list);

            for (Mob abstractraiderentity : set) {
                pRaid.joinRaid(pRaid.getGroupsSpawned(), abstractraiderentity, (BlockPos) null, true);
            }
        }
    }

    private boolean hasNotVisited(BlockPos p_220860_1_) {
        for (BlockPos blockpos : this.visited) {
            if (Objects.equals(p_220860_1_, blockpos)) {
                return false;
            }
        }

        return true;
    }

    private void updateVisited() {
        if (this.visited.size() > 2) {
            this.visited.remove(0);
        }

    }
}