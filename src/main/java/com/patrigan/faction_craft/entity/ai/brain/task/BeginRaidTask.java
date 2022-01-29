package com.patrigan.faction_craft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.entity.ai.brain.ModActivities;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.server.level.ServerLevel;

public class BeginRaidTask extends Behavior<LivingEntity> {
    public BeginRaidTask() {
        super(ImmutableMap.of());
    }

    protected boolean checkExtraStartConditions(ServerLevel pLevel, LivingEntity pOwner) {
        return pLevel.random.nextInt(20) == 0;
    }

    protected void start(ServerLevel level, LivingEntity entity, long gameTime) {
        Brain<?> brain = entity.getBrain();
        RaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(level);
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