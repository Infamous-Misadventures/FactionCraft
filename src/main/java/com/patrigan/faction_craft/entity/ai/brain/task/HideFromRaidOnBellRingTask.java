package com.patrigan.faction_craft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public class HideFromRaidOnBellRingTask extends Task<LivingEntity> {
   public HideFromRaidOnBellRingTask() {
      super(ImmutableMap.of(MemoryModuleType.HEARD_BELL_TIME, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected void start(ServerWorld pLevel, LivingEntity pEntity, long pGameTime) {
      Brain<?> brain = pEntity.getBrain();
      IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(pLevel);
      Raid raid = raidManagerCapability.getRaidAt(pEntity.blockPosition());
      if (raid == null) {
         brain.setActiveActivityIfPossible(Activity.HIDE);
      }

   }
}