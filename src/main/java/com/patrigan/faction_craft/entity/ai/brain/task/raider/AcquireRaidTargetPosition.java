package com.patrigan.faction_craft.entity.ai.brain.task.raider;

import com.google.common.collect.ImmutableMap;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.raid.Raid;
import com.patrigan.faction_craft.registry.ModMemoryModuleTypes;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class AcquireRaidTargetPosition<E extends LivingEntity> extends Behavior<E> {

   public AcquireRaidTargetPosition() {
      super(constructEntryConditionMap(ModMemoryModuleTypes.RAID_WALK_TARGET.get()));

   }

   private static ImmutableMap<MemoryModuleType<?>, MemoryStatus> constructEntryConditionMap(MemoryModuleType<GlobalPos> pMemoryToAcquire) {
      ImmutableMap.Builder<MemoryModuleType<?>, MemoryStatus> builder = ImmutableMap.builder();
      builder.put(pMemoryToAcquire, MemoryStatus.VALUE_ABSENT);

      return builder.build();
   }

   protected boolean checkExtraStartConditions(ServerLevel pLevel, PathfinderMob pEntity) {
      return RaiderHelper.getRaiderCapability(pEntity).getRaid() != null;
   }


   @Override
   protected void start(ServerLevel pLevel, E pEntity, long pGameTime) {
      if (pEntity instanceof Mob mob) {
         Raid raid = RaiderHelper.getRaiderCapability(mob).getRaid();
         if (raid != null) {
            pEntity.getBrain().setMemory(ModMemoryModuleTypes.RAID_WALK_TARGET.get(), GlobalPos.of(pLevel.dimension(), raid.getRaidTarget().getTargetBlockPos()));
         }
      }
      super.start(pLevel, pEntity, pGameTime);
   }
}