package com.patrigan.faction_craft.entity.ai.goal;

import com.google.common.collect.Lists;
import com.patrigan.faction_craft.capabilities.raider.IRaider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InvadeHomeGoal extends Goal {
    private final MobEntity raider;
    private final double speedModifier;
    private BlockPos poiPos;
    private final List<BlockPos> visited = Lists.newArrayList();
    private final int distanceToPoi;
    private boolean stuck;

    public InvadeHomeGoal(MobEntity p_i50570_1_, double p_i50570_2_, int p_i50570_4_) {
        this.raider = p_i50570_1_;
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
        return this.raider instanceof CreatureEntity && this.isValidRaid() && this.hasSuitablePoi() && this.raider.getTarget() == null;
    }

    private boolean isValidRaid() {
        LazyOptional<IRaider> raiderCapabilityLazy = RaiderHelper.getRaiderCapabilityLazy(this.raider);
        if(!raiderCapabilityLazy.isPresent()){
            return false;
        }
        IRaider raiderCapability = RaiderHelper.getRaiderCapability(this.raider);
        return raiderCapability.hasActiveRaid() && !raiderCapability.getRaid().isOver();
    }

    private boolean hasSuitablePoi() {
        ServerWorld serverworld = (ServerWorld)this.raider.level;
        BlockPos blockpos = this.raider.blockPosition();
        Optional<BlockPos> optional = serverworld.getPoiManager().getRandom((p_220859_0_) -> {
            return p_220859_0_ == PointOfInterestType.HOME;
        }, this::hasNotVisited, PointOfInterestManager.Status.ANY, blockpos, 48, this.raider.getRandom());
        if (!optional.isPresent()) {
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
        if (this.raider.getNavigation().isDone()) {
            return false;
        } else {
            return this.raider.getTarget() == null && !this.poiPos.closerThan(this.raider.position(), (double)(this.raider.getBbWidth() + (float)this.distanceToPoi)) && !this.stuck;
        }
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        if (this.poiPos.closerThan(this.raider.position(), (double)this.distanceToPoi)) {
            this.visited.add(this.poiPos);
        }

    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        super.start();
        this.raider.setNoActionTime(0);
        this.raider.getNavigation().moveTo((double)this.poiPos.getX(), (double)this.poiPos.getY(), (double)this.poiPos.getZ(), this.speedModifier);
        this.stuck = false;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        if (this.raider.getNavigation().isDone()) {
            Vector3d vector3d = Vector3d.atBottomCenterOf(this.poiPos);
            Vector3d vector3d1 = RandomPositionGenerator.getPosTowards((CreatureEntity) this.raider, 16, 7, vector3d, (double)((float)Math.PI / 10F));
            if (vector3d1 == null) {
                vector3d1 = RandomPositionGenerator.getPosTowards((CreatureEntity) this.raider, 8, 7, vector3d);
            }

            if (vector3d1 == null) {
                this.stuck = true;
                return;
            }

            this.raider.getNavigation().moveTo(vector3d1.x, vector3d1.y, vector3d1.z, this.speedModifier);
        }

    }

    private boolean hasNotVisited(BlockPos p_220860_1_) {
        for(BlockPos blockpos : this.visited) {
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