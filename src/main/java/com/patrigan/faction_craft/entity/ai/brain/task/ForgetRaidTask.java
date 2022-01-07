package com.patrigan.faction_craft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public class ForgetRaidTask extends Task<LivingEntity> {
   public ForgetRaidTask() {
      super(ImmutableMap.of());
   }

   protected boolean checkExtraStartConditions(ServerWorld pLevel, LivingEntity pOwner) {
      return pLevel.random.nextInt(20) == 0;
   }

   protected void start(ServerWorld pLevel, LivingEntity pEntity, long pGameTime) {
      Brain<?> brain = pEntity.getBrain();
      IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(pLevel);
      Raid raid = raidManagerCapability.getRaidAt(pEntity.blockPosition());
      if (raid == null || raid.isStopped() || raid.isLoss()) {
         brain.setDefaultActivity(Activity.IDLE);
         brain.updateActivityFromSchedule(pLevel.getDayTime(), pLevel.getGameTime());
      }

   }
}