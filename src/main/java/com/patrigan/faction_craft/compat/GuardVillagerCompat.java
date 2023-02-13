package com.patrigan.faction_craft.compat;

import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.event.CalculateStrengthEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static com.patrigan.faction_craft.config.FactionCraftConfig.VILLAGE_RAID_GUARD_VILLAGER_WEIGHT;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GuardVillagerCompat {
    public static final String COMPAT_MOD_ID = "guardvillagers";
    private static boolean IS_LOADED = false;
    private static Supplier<EntityType<?>> GUARD = () -> EntityType.PIG;

    @SubscribeEvent
    public static void onInterMod(InterModProcessEvent event) {
        if (ModList.get().isLoaded(COMPAT_MOD_ID)) {
            IS_LOADED = true;
            GUARD = () -> getRegisteredEntityType("guard");
        }
    }

    public static void registerEventHandlers() {
        MinecraftForge.EVENT_BUS.addListener(GuardVillagerCompat::onCalculateStrengthEvent);
        MinecraftForge.EVENT_BUS.addListener(GuardVillagerCompat::onEntityJoinWorld);
    }

    public static void onCalculateStrengthEvent(CalculateStrengthEvent event) {
        if(!isLoaded()) return;
        int strengthAdjustment = event.getLevel().getEntitiesOfClass(GUARD.get().getBaseClass(),
                new AABB(event.getBlockPos()).inflate(100),
                entity -> true).size() * VILLAGE_RAID_GUARD_VILLAGER_WEIGHT.get();
        event.setStrength(event.getStrength() + strengthAdjustment);
    }

    public static void onEntityJoinWorld(EntityJoinLevelEvent event){
        if(!isLoaded()) return;
        if(event.getEntity().getType() == GUARD.get() &&  event.getEntity() instanceof Mob){
            // add target to event.getEntity to make it attack any livingEntity with a predicate where it is currently part of a raid
            ((Mob) event.getEntity()).targetSelector.addGoal(3,
                    new NearestAttackableTargetGoal<>((Mob) event.getEntity(), LivingEntity.class, true,
                            GuardVillagerCompat::isRaider));
        }
    }

    private static boolean isRaider(LivingEntity livingEntity) {
        if(livingEntity instanceof Mob mob) {
            Raider raiderCapability = RaiderHelper.getRaiderCapability(mob);
            return livingEntity.isAlive() && raiderCapability != null && raiderCapability.hasActiveRaid();
        }else{
            return false;
        }
    }


    private static EntityType<?> getRegisteredEntityType(String item) {
        return ForgeRegistries.ENTITY_TYPES.getValue(getResource(item));
    }

    public static Supplier<EntityType<?>> getGuardVillager() {
        return GUARD;
    }

    public static boolean isLoaded() {
        return IS_LOADED;
    }

    private static ResourceLocation getResource(String id) {
        return new ResourceLocation(COMPAT_MOD_ID, id);
    }
}