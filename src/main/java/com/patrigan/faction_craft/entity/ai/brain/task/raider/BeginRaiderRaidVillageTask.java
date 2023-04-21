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

public class BeginRaiderRaidVillageTask extends Behavior<LivingEntity> {
    public BeginRaiderRaidVillageTask() {
        super(ImmutableMap.of());
    }

    protected boolean checkExtraStartConditions(ServerLevel pLevel, LivingEntity entity) {
        if (entity instanceof Mob mob) {
            Raid raid = RaiderHelper.getRaiderCapability(mob).getRaid();
            if (raid != null) {
                return raid.getRaidTarget().getRaidType() == RaidTarget.Type.VILLAGE && ((ServerLevel)entity.level).isVillage(entity.blockPosition());
            }
        }
        return false;
    }

    protected void start(ServerLevel level, LivingEntity entity, long gameTime) {
        if (entity instanceof Mob mob) {
            Brain<?> brain = entity.getBrain();
            Raid raid = RaiderHelper.getRaiderCapability(mob).getRaid();
            if (raid != null) {
                if(raid.getRaidTarget().getRaidType() == RaidTarget.Type.VILLAGE) {
                    brain.setDefaultActivity(ModActivities.FACTION_RAIDER_VILLAGE.get());
                    brain.setActiveActivityIfPossible(ModActivities.FACTION_RAIDER_VILLAGE.get());
                }
            }
        }
    }
}
