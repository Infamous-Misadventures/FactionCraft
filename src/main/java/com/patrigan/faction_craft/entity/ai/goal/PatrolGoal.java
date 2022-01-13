package com.patrigan.faction_craft.entity.ai.goal;


import com.patrigan.faction_craft.capabilities.patroller.IPatroller;
import com.patrigan.faction_craft.capabilities.patroller.PatrollerHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.util.LazyOptional;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class PatrolGoal<T extends MobEntity> extends Goal {
    private final T mob;
    private final double speedModifier;
    private final double leaderSpeedModifier;
    private long cooldownUntil;

    public PatrolGoal(T p_i50070_1_, double p_i50070_2_, double p_i50070_4_) {
        this.mob = p_i50070_1_;
        this.speedModifier = p_i50070_2_;
        this.leaderSpeedModifier = p_i50070_4_;
        this.cooldownUntil = -1L;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        boolean flag = this.mob.level.getGameTime() < this.cooldownUntil;
        LazyOptional<IPatroller> lazyCap = PatrollerHelper.getPatrollerCapabilityLazy(this.mob);
        if(!lazyCap.isPresent()){
            return false;
        }
        IPatroller cap = lazyCap.orElseThrow(() -> new IllegalStateException("Couldn't get the RaidManager capability from the world!"));
        return cap.isPatrolling() && this.mob.getTarget() == null && !this.mob.isVehicle() && cap.hasPatrolTarget() && !flag;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        IPatroller cap = PatrollerHelper.getPatrollerCapability(this.mob);
        boolean flag = cap.isPatrolLeader();
        PathNavigator pathnavigator = this.mob.getNavigation();
        if (pathnavigator.isDone()) {
            List<MobEntity> list = this.findPatrolCompanions();
            if (cap.isPatrolling() && list.isEmpty()) {
                cap.setPatrolling(false);
            } else if (flag && cap.getPatrolTarget().closerThan(this.mob.position(), 10.0D)) {
                cap.findPatrolTarget();
            } else {
                Vector3d vector3d = Vector3d.atBottomCenterOf(cap.getPatrolTarget());
                Vector3d vector3d1 = this.mob.position();
                Vector3d vector3d2 = vector3d1.subtract(vector3d);
                vector3d = vector3d2.yRot(90.0F).scale(0.4D).add(vector3d);
                Vector3d vector3d3 = vector3d.subtract(vector3d1).normalize().scale(10.0D).add(vector3d1);
                BlockPos blockpos = new BlockPos(vector3d3);
                blockpos = this.mob.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos);
                if (!pathnavigator.moveTo((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), flag ? this.leaderSpeedModifier : this.speedModifier)) {
                    this.moveRandomly();
                    this.cooldownUntil = this.mob.level.getGameTime() + 200L;
                } else if (flag) {
                    for(MobEntity patrollerentity : list) {
                        IPatroller patrollerCap = PatrollerHelper.getPatrollerCapability(patrollerentity);
                        patrollerCap.setPatrolTarget(blockpos);
                    }
                }
            }
        }

    }

    private List<MobEntity> findPatrolCompanions() {
        return this.mob.level.getEntitiesOfClass(MobEntity.class, this.mob.getBoundingBox().inflate(16.0D), (p_226543_1_) -> {
            IPatroller cap = PatrollerHelper.getPatrollerCapability(this.mob);
            return cap.canJoinPatrol(this.mob) && !p_226543_1_.is(this.mob);
        });
    }

    private boolean moveRandomly() {
        Random random = this.mob.getRandom();
        BlockPos blockpos = this.mob.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this.mob.blockPosition().offset(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
        return this.mob.getNavigation().moveTo((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), this.speedModifier);
    }
}
