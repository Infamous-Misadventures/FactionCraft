package com.patrigan.faction_craft.entity.ai.brain.task;

import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.task.FindWalkTargetTask;
import net.minecraft.world.server.ServerWorld;

public class FindWalkTargetAfterRaidVictoryTask extends FindWalkTargetTask {
   public FindWalkTargetAfterRaidVictoryTask(float p_i50337_1_) {
      super(p_i50337_1_);
   }

   protected boolean checkExtraStartConditions(ServerWorld pLevel, CreatureEntity pOwner) {
      IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(pLevel);
      Raid raid = raidManagerCapability.getRaidAt(pOwner.blockPosition());
      return raid != null && raid.isVictory() && super.checkExtraStartConditions(pLevel, pOwner);
   }
}