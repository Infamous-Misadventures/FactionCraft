package com.patrigan.faction_craft.entity.ai.brain.task.raider;

import com.google.common.collect.ImmutableMap;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.entity.ai.brain.ModActivities;
import com.patrigan.faction_craft.raid.Raid;
import com.patrigan.faction_craft.raid.target.RaidTarget;
import com.patrigan.faction_craft.registry.ModMemoryModuleTypes;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;

import java.util.ArrayList;
import java.util.List;

public class BeginRaiderRaidVillageTask extends Behavior<LivingEntity> {
    public BeginRaiderRaidVillageTask() {
        super(ImmutableMap.of());
    }

    protected boolean checkExtraStartConditions(ServerLevel pLevel, LivingEntity entity) {
        if (pLevel.random.nextInt(20) == 0 && entity instanceof Mob mob) {
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
                    brain.setMemory(ModMemoryModuleTypes.RAIDED_VILLAGE_POI.get(), new ArrayList<>(List.of(GlobalPos.of(level.dimension(), raid.getRaidTarget().getTargetBlockPos()))));
                    brain.eraseMemory(ModMemoryModuleTypes.RAID_WALK_TARGET.get());
                    brain.setDefaultActivity(ModActivities.FACTION_RAIDER_VILLAGE.get());
                    brain.setActiveActivityIfPossible(ModActivities.FACTION_RAIDER_VILLAGE.get());
                }
            }
        }
    }
}
