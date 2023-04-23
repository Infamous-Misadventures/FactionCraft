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
import com.patrigan.faction_craft.mixin.LivingEntityAccessor;
import com.patrigan.faction_craft.registry.Factions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static net.minecraft.sounds.SoundEvents.SHIELD_BLOCK;

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
                    List<Faction> factions = Factions.getFactionData().values().stream().filter(faction -> faction.getDefaultEntities().contains(entity.getType())).toList();
                    if (!factions.isEmpty()) {
                        RandomSource randomSource = RandomSource.create(event.getLevel().getChunkAt(mob.blockPosition()).getPos().toLong());
                        factionEntity.setFaction(factions.get(randomSource.nextInt(factions.size())));
                    }
                }
            }
        }
    }

    private static boolean hasBrain(Mob mob) {
        return mob.getBrain().isActive(Activity.CORE);
    }

    @SubscribeEvent
    public static void onPlayLevelSoundEvent(PlayLevelSoundEvent.AtPosition event) {
        LivingEntity nearestEntity = event.getLevel().getNearestEntity(LivingEntity.class, TargetingConditions.forNonCombat(), null, event.getPosition().x, event.getPosition().y, event.getPosition().z, AABB.ofSize(event.getPosition(), 1, 1, 1));
        if (nearestEntity instanceof Mob mob) {
            SoundEvent soundEvent = ((LivingEntityAccessor) mob).invokeGetHurtSound(mob.getLastDamageSource());
            if(event.getSound() == soundEvent && mob.getUseItem().canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK)){
                event.setSound(SHIELD_BLOCK);
            }
        }
    }
}