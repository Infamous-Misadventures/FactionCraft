package com.patrigan.faction_craft.entity.ai.brain.task.villager;

import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.MoveToSkySeeingSpot;
import net.minecraft.server.level.ServerLevel;

public class GoOutsideAfterRaidTask extends MoveToSkySeeingSpot {
   public GoOutsideAfterRaidTask(float p_i50365_1_) {
      super(p_i50365_1_);
   }

   protected boolean checkExtraStartConditions(ServerLevel pLevel, LivingEntity pOwner) {
      RaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(pLevel);
      Raid raid = raidManagerCapability.getRaidAt(pOwner.blockPosition());
      return raid != null && raid.isVictory() && super.checkExtraStartConditions(pLevel, pOwner);
   }
}