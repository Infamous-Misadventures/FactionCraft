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
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.BooleanSupplier;

import static com.patrigan.faction_craft.util.GeneralUtils.vec3ToBlockPos;

public class RaiderMoveThroughVillageGoal extends Goal {
    private final Mob mob;
    private final double speedModifier;
    private final List<BlockPos> visited = Lists.newArrayList();
    private final int distanceToPoi;
    private final BooleanSupplier canDealWithDoors;
    private BlockPos poiPos;
    private boolean stuck;
    private int lastStuckCheck = 0;
    private Vec3 lastStuckCheckPos;
    private Path path = null;
    private int uncertaintyDistance;

    public RaiderMoveThroughVillageGoal(Mob p_i50570_1_, double pSpeedModifier, int pDistanceToPoi, BooleanSupplier canDealWithDoors, int uncertaintyDistance) {
        this.mob = p_i50570_1_;
        this.uncertaintyDistance = uncertaintyDistance;
        this.lastStuckCheck = mob.tickCount;
        this.lastStuckCheckPos = mob.position();
        this.speedModifier = pSpeedModifier;
        this.distanceToPoi = pDistanceToPoi;
        this.canDealWithDoors = canDealWithDoors;
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
            return this.mob.getTarget() == null && !this.poiPos.closerToCenterThan(this.mob.position(), this.mob.getBbWidth() + (float) this.distanceToPoi) && !this.stuck;
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
        PathfinderMob pathfinderMob = (PathfinderMob) this.mob;
        GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
        this.stuck = true;
        boolean flag = groundPathNavigation.canOpenDoors();
        if (this.mob.blockPosition().closerThan(this.poiPos, uncertaintyDistance)) {
            this.path = getPathTowards(groundPathNavigation, flag, this.poiPos);
            if (this.path == null) {
                Vec3 vec31 = DefaultRandomPos.getPosTowards(pathfinderMob, 10, 7, Vec3.atBottomCenterOf(this.poiPos), (double) ((float) Math.PI / 2F));
                if (vec31 == null) {
                    return;
                }
                this.path = getPathTowards(groundPathNavigation, flag, vec3ToBlockPos(vec31));
            }
        } else {
            Vec3 vec31 = DefaultRandomPos.getPosTowards(pathfinderMob, 10, 7, Vec3.atBottomCenterOf(this.poiPos), (double) ((float) Math.PI / 2F));
            if (vec31 == null) {
                return;
            }
            this.path = getPathTowards(groundPathNavigation, flag, vec3ToBlockPos(vec31));
        }

        if (path == null) {
            Raider raiderCapability = RaiderHelper.getRaiderCapability(this.mob);
            Faction faction = FactionEntityHelper.getFactionEntityCapability(this.mob).getFaction();
            raiderCapability.getRaid().spawnDigger(faction, this.mob.blockPosition());
            return;
        }
        this.mob.getNavigation().moveTo(path, this.speedModifier);
        this.stuck = false;
    }

    private Path getPathTowards(GroundPathNavigation groundPathNavigation, boolean flag, BlockPos poiPos) {
        groundPathNavigation.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
        Path path = groundPathNavigation.createPath(poiPos, 0);
        groundPathNavigation.setCanOpenDoors(flag);
        return path;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        Raider raiderCapability = RaiderHelper.getRaiderCapability(this.mob);
        if (raiderCapability.hasActiveRaid()) {
            Raid raid = raiderCapability.getRaid();
            if (this.mob.tickCount % 20 == 0) {
                this.recruitNearby(raid);
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
            // Todo: add a check to see if the distance of the mob's postion to the targetPos is increasing compared to the lastStuckCheckPos
//            BlockPos targetBlockPos = this.mob.getNavigation().getTargetPos();
//            if (targetBlockPos != null) {
//                Vec3 targetPos = Vec3.atCenterOf(targetBlockPos);
//                if (this.mob.position().distanceToSqr(targetPos) - this.lastStuckCheckPos.distanceToSqr(targetPos) < 2.25D) {
//                    this.lastStuckCheck = this.mob.tickCount;
//                    this.lastStuckCheckPos = mob.position();
//                    return true;
//                }
//            }
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
        if (this.visited.size() > 15) {
            this.visited.remove(0);
        }

    }
}