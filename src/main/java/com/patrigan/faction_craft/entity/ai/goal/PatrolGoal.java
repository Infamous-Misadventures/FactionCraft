package com.patrigan.faction_craft.entity.ai.goal;


import com.patrigan.faction_craft.capabilities.patroller.Patroller;
import com.patrigan.faction_craft.capabilities.patroller.PatrollerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;

import java.util.EnumSet;
import java.util.List;

public class PatrolGoal<T extends Mob> extends Goal {
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
        Patroller cap = PatrollerHelper.getPatrollerCapability(this.mob);
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
        Patroller cap = PatrollerHelper.getPatrollerCapability(this.mob);
        boolean flag = cap.isPatrolLeader();
        PathNavigation pathnavigator = this.mob.getNavigation();
        if (pathnavigator.isDone()) {
            List<Mob> list = this.findPatrolCompanions();
            if (flag && cap.getPatrolTarget().closerToCenterThan(this.mob.position(), 10.0D)) {
                cap.findPatrolTarget();
            } else {
                Vec3 vector3d = Vec3.atBottomCenterOf(cap.getPatrolTarget());
                Vec3 vector3d1 = this.mob.position();
                Vec3 vector3d2 = vector3d1.subtract(vector3d);
                vector3d = vector3d2.yRot(90.0F).scale(0.4D).add(vector3d);
                Vec3 vector3d3 = vector3d.subtract(vector3d1).normalize().scale(10.0D).add(vector3d1);
                BlockPos blockpos = new BlockPos(vector3d3);
                blockpos = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos);
                if (!pathnavigator.moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ(), flag ? this.leaderSpeedModifier : this.speedModifier)) {
                    this.moveRandomly();
                    this.cooldownUntil = this.mob.level.getGameTime() + 200L;
                } else if (flag) {
                    for(Mob patrollerentity : list) {
                        Patroller patrollerCap = PatrollerHelper.getPatrollerCapability(patrollerentity);
                        patrollerCap.setPatrolTarget(blockpos);
                    }
                }
            }
        }

    }

    private List<Mob> findPatrolCompanions() {
        return this.mob.level.getEntitiesOfClass(Mob.class, this.mob.getBoundingBox().inflate(32.0D), (p_226543_1_) -> {
            Patroller cap = PatrollerHelper.getPatrollerCapability(p_226543_1_);
            if(cap == null) return false;
            return cap.canJoinPatrol(this.mob) && !p_226543_1_.is(this.mob);
        });
    }

    private boolean moveRandomly() {
        RandomSource random = this.mob.getRandom();
        BlockPos blockpos = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, this.mob.blockPosition().offset(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
        return this.mob.getNavigation().moveTo(blockpos.getX(), blockpos.getY(), blockpos.getZ(), this.speedModifier);
    }
}
