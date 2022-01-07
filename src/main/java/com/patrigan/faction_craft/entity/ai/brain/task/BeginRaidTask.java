package com.patrigan.faction_craft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.entity.ai.brain.ModActivities;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public class BeginRaidTask extends Task<LivingEntity> {
    public BeginRaidTask() {
        super(ImmutableMap.of());
    }

    protected boolean checkExtraStartConditions(ServerWorld pLevel, LivingEntity pOwner) {
        return pLevel.random.nextInt(20) == 0;
    }

    protected void start(ServerWorld level, LivingEntity entity, long gameTime) {
        Brain<?> brain = entity.getBrain();
        IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(level);
        Raid raid = raidManagerCapability.getRaidAt(entity.blockPosition());
        if (raid != null) {
            if (raid.hasFirstWaveSpawned() && !raid.isBetweenWaves()) {
                brain.setDefaultActivity(ModActivities.FACTION_RAID.get());
                brain.setActiveActivityIfPossible(ModActivities.FACTION_RAID.get());
            } else {
                brain.setDefaultActivity(ModActivities.PRE_FACTION_RAID.get());
                brain.setActiveActivityIfPossible(ModActivities.PRE_FACTION_RAID.get());
            }
        }

    }
}