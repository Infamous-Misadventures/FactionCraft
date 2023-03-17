package com.patrigan.faction_craft.entity;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.patroller.Patroller;
import com.patrigan.faction_craft.capabilities.patroller.PatrollerHelper;
import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.entity.ai.target.FactionAllyHurtTargetGoal;
import com.patrigan.faction_craft.entity.ai.target.NearestFactionEnemyTargetGoal;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.registry.Factions;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class EntityEvents {

    @SubscribeEvent
    public static void onLivingConversionEvent(LivingConversionEvent.Pre event) {
        if (event.getEntity() instanceof Mob mob) {
            Raider raiderCap = RaiderHelper.getRaiderCapability(mob);
            Patroller patrollerCap = PatrollerHelper.getPatrollerCapability(mob);
            if (raiderCap.hasActiveRaid() || patrollerCap.isPatrolling()) {
                event.setCanceled(true);
                event.setConversionTimer(0);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        Entity entity = event.getEntity();
        if (entity instanceof Mob mob) {
            if(FactionCraftConfig.ENABLE_DEFAULT_FACTION.get()) {
                FactionEntity factionEntity = FactionEntityHelper.getFactionEntityCapability(mob);
                if (factionEntity.getFaction() == null) {
                    List<Faction> factions = Factions.getFactionData().values().stream().filter(faction -> faction.getDefaultEntities().contains(Holder.direct(entity.getType()))).collect(Collectors.toList());
                    if (!factions.isEmpty()) {
                        RandomSource randomSource = RandomSource.create(event.getLevel().getChunkAt(mob.blockPosition()).getPos().toLong());
                        factionEntity.setFaction(factions.get(randomSource.nextInt(factions.size())));
                    }
                }
            }
            mob.targetSelector.addGoal(2, new NearestFactionEnemyTargetGoal(mob, 10, true, false));
            mob.targetSelector.addGoal(2, new FactionAllyHurtTargetGoal(mob, 10, true, false));
        }
    }
}