package com.patrigan.faction_craft.entity.ai.brain.task;

import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.MoveToSkylightTask;
import net.minecraft.world.server.ServerWorld;

public class GoOutsideAfterRaidTask extends MoveToSkylightTask {
   public GoOutsideAfterRaidTask(float p_i50365_1_) {
      super(p_i50365_1_);
   }

   protected boolean checkExtraStartConditions(ServerWorld pLevel, LivingEntity pOwner) {
      IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(pLevel);
      Raid raid = raidManagerCapability.getRaidAt(pOwner.blockPosition());
      return raid != null && raid.isVictory() && super.checkExtraStartConditions(pLevel, pOwner);
   }
}