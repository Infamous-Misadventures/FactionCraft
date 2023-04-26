package com.patrigan.faction_craft.entity.ai.brain.task.raider;

import com.google.common.collect.ImmutableMap;
import com.patrigan.faction_craft.capabilities.patroller.Patroller;
import com.patrigan.faction_craft.capabilities.patroller.PatrollerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AcquirePatrolTarget<E extends LivingEntity> extends Behavior<E> {

    private MemoryModuleType<GlobalPos> memoryToAcquire;
    private int closeEnoughDist;
    private long wanderTime;

    public AcquirePatrolTarget(MemoryModuleType<GlobalPos> memoryToAcquire, int closeEnoughDist) {
        super(constructEntryConditionMap(memoryToAcquire));
        this.memoryToAcquire = memoryToAcquire;
        this.closeEnoughDist = closeEnoughDist;
    }

    private static ImmutableMap<MemoryModuleType<?>, MemoryStatus> constructEntryConditionMap(MemoryModuleType<GlobalPos> pMemoryToAcquire) {
        ImmutableMap.Builder<MemoryModuleType<?>, MemoryStatus> builder = ImmutableMap.builder();
        builder.put(pMemoryToAcquire, MemoryStatus.REGISTERED);

        return builder.build();
    }

    protected boolean checkExtraStartConditions(ServerLevel pLevel, PathfinderMob pEntity) {
        return PatrollerHelper.getPatrollerCapability(pEntity).isPatrolling();
    }


    @Override
    protected void start(ServerLevel pLevel, E pEntity, long pGameTime) {
        if(pEntity instanceof Mob mob) {
            Patroller cap = PatrollerHelper.getPatrollerCapability(mob);
            boolean flag = cap.isPatrolLeader();
            List<Mob> list = this.findPatrolCompanions(pEntity);
            if(cap.getPatrolTarget() == null) {
                if (flag) {
                    cap.findPatrolTarget();
                    for (Mob patrollerentity : list) {
                        Patroller patrollerCap = PatrollerHelper.getPatrollerCapability(patrollerentity);
                        patrollerCap.setPatrolTarget(cap.getPatrolTarget());
                    }
                }
            }
            if(cap.getPatrolTarget() != null && closeEnough(pLevel, pEntity, GlobalPos.of(pLevel.dimension(), cap.getPatrolTarget()))){
                cap.setPatrolTarget(null);
            }
            if (cap.getPatrolTarget() != null) {
                moveTowardsTarget(pLevel, pEntity, cap.getPatrolTarget());
            }else{
                this.moveRandomly(pLevel, pEntity);
            }
        }
        super.start(pLevel, pEntity, pGameTime);
    }

    private void moveTowardsTarget(ServerLevel pLevel, E pEntity, BlockPos patrolTarget) {
        Vec3 vector3d = Vec3.atBottomCenterOf(patrolTarget);
        Vec3 vector3d1 = pEntity.position();
        Vec3 vector3d2 = vector3d1.subtract(vector3d);
        vector3d = vector3d2.yRot(90.0F).scale(0.4D).add(vector3d);
        Vec3 vector3d3 = vector3d.subtract(vector3d1).normalize().scale(10.0D).add(vector3d1);
        BlockPos blockpos = new BlockPos(vector3d3);
        blockpos = pEntity.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockpos);
        pEntity.getBrain().setMemory(this.memoryToAcquire, GlobalPos.of(pLevel.dimension(), blockpos));
    }

    private boolean closeEnough(ServerLevel pLevel, E entity, GlobalPos pMemoryPos) {
        return pMemoryPos.dimension() == pLevel.dimension() && pMemoryPos.pos().distManhattan(entity.blockPosition()) <= this.closeEnoughDist;
    }

    private List<Mob> findPatrolCompanions(E pEntity) {
        return pEntity.level.getEntitiesOfClass(Mob.class, pEntity.getBoundingBox().inflate(32.0D), (p_226543_1_) -> {
            Patroller cap = PatrollerHelper.getPatrollerCapability(p_226543_1_);
            return pEntity instanceof Mob && cap.canJoinPatrol((Mob) pEntity) && !p_226543_1_.is(pEntity);
        });
    }

    private void moveRandomly(ServerLevel pLevel, E mob) {
        RandomSource random = mob.getRandom();
        BlockPos blockpos = mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mob.blockPosition().offset(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
        mob.getBrain().setMemory(this.memoryToAcquire, GlobalPos.of(pLevel.dimension(), blockpos));
    }
}