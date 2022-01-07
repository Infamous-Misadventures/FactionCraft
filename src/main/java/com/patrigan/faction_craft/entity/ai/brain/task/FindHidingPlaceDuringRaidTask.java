package com.patrigan.faction_craft.entity.ai.brain.task;

import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.FindHidingPlaceTask;
import net.minecraft.world.server.ServerWorld;

public class FindHidingPlaceDuringRaidTask extends FindHidingPlaceTask {
   public FindHidingPlaceDuringRaidTask(int p_i50360_1_, float p_i50360_2_) {
      super(p_i50360_1_, p_i50360_2_, 1);
   }

   protected boolean checkExtraStartConditions(ServerWorld pLevel, LivingEntity pOwner) {
      IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(pLevel);
      Raid raid = raidManagerCapability.getRaidAt(pOwner.blockPosition());
      return super.checkExtraStartConditions(pLevel, pOwner) && raid != null && raid.isActive() && !raid.isVictory() && !raid.isLoss();
   }
}