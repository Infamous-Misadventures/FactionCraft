package com.patrigan.faction_craft.entity.ai.brain.task.raider;

import com.google.common.collect.ImmutableMap;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.entity.ai.brain.ModActivities;
import com.patrigan.faction_craft.raid.Raid;
import com.patrigan.faction_craft.raid.target.RaidTarget;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;

public class BeginRaiderRaidPrepTask extends Behavior<LivingEntity> {
    public BeginRaiderRaidPrepTask() {
        super(ImmutableMap.of());
    }

    protected boolean checkExtraStartConditions(ServerLevel pLevel, LivingEntity entity) {
        if (entity instanceof Mob mob) {
            return RaiderHelper.getRaiderCapability(mob).hasActiveRaid();
        }
        return false;
    }

    protected void start(ServerLevel level, LivingEntity entity, long gameTime) {
        if (entity instanceof Mob mob) {
            Brain<?> brain = entity.getBrain();
            Raid raid = RaiderHelper.getRaiderCapability(mob).getRaid();
            if (raid != null) {
                brain.setDefaultActivity(ModActivities.FACTION_RAIDER_PREP.get());
                brain.setActiveActivityIfPossible(ModActivities.FACTION_RAIDER_PREP.get());
            }
        }
    }
}
