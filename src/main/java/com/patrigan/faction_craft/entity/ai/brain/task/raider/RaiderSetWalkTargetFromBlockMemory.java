package com.patrigan.faction_craft.entity.ai.brain.task.raider;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class RaiderSetWalkTargetFromBlockMemory<E extends LivingEntity> extends Behavior<E> {
   private final MemoryModuleType<GlobalPos> memoryType;
   private final float speedModifier;
   private final int closeEnoughDist;
   private final int tooFarDistance;
   private final int tooLongUnreachableDuration;

   public RaiderSetWalkTargetFromBlockMemory(MemoryModuleType<GlobalPos> pMemoryType, float pSpeedModifier, int pCloseEnoughDist, int pTooFarDistance, int pTooLongUnreachableDuration) {
      super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, pMemoryType, MemoryStatus.VALUE_PRESENT));
      this.memoryType = pMemoryType;
      this.speedModifier = pSpeedModifier;
      this.closeEnoughDist = pCloseEnoughDist;
      this.tooFarDistance = pTooFarDistance;
      this.tooLongUnreachableDuration = pTooLongUnreachableDuration;
   }

   protected void start(ServerLevel pLevel, E livingEntity, long pGameTime) {
      if(livingEntity instanceof PathfinderMob pathfinderMob) {
         Brain<?> brain = pathfinderMob.getBrain();
         brain.getMemory(this.memoryType).ifPresent((destination) -> {
            if (!this.wrongDimension(pLevel, destination) && !this.tiredOfTryingToFindTarget(pLevel, pathfinderMob)) {
               if (this.tooFar(pathfinderMob, destination)) {
                  Vec3 vec3 = null;
                  int i = 0;

                  for (int j = 1000; i < 1000 && (vec3 == null || this.tooFar(pathfinderMob, GlobalPos.of(pLevel.dimension(), new BlockPos(vec3)))); ++i) {
                     vec3 = DefaultRandomPos.getPosTowards(pathfinderMob, 15, 7, Vec3.atBottomCenterOf(destination.pos()), (double) ((float) Math.PI / 2F));
                  }

                  if (i == 1000) {
                     this.dropPOI(pathfinderMob, pGameTime);
                     return;
                  }

                  brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, this.speedModifier, this.closeEnoughDist));
               } else if (!this.closeEnough(pLevel, pathfinderMob, destination)) {
                  brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(destination.pos(), this.speedModifier, this.closeEnoughDist));
               }
            } else {
               this.dropPOI(pathfinderMob, pGameTime);
            }

         });
      }
   }

   private void dropPOI(PathfinderMob pathfinderMob, long pTime) {
      Brain<?> brain = pathfinderMob.getBrain();
      brain.eraseMemory(this.memoryType);
      brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, pTime);
   }

   private boolean tiredOfTryingToFindTarget(ServerLevel pLevel, PathfinderMob pathfinderMob) {
      Optional<Long> optional = pathfinderMob.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      if (optional.isPresent()) {
         return pLevel.getGameTime() - optional.get() > (long)this.tooLongUnreachableDuration;
      } else {
         return false;
      }
   }

   private boolean tooFar(PathfinderMob pathfinderMob, GlobalPos pMemoryType) {
      return pMemoryType.pos().distManhattan(pathfinderMob.blockPosition()) > this.tooFarDistance;
   }

   private boolean wrongDimension(ServerLevel pLevel, GlobalPos pMemoryPos) {
      return pMemoryPos.dimension() != pLevel.dimension();
   }

   private boolean closeEnough(ServerLevel pLevel, PathfinderMob pathfinderMob, GlobalPos pMemoryPos) {
      return pMemoryPos.dimension() == pLevel.dimension() && pMemoryPos.pos().distManhattan(pathfinderMob.blockPosition()) <= this.closeEnoughDist;
   }
}