package com.patrigan.faction_craft.capabilities;

import com.patrigan.faction_craft.capabilities.appliedboosts.AppliedBoostsProvider;
import com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteractionProvider;
import com.patrigan.faction_craft.capabilities.patroller.PatrollerProvider;
import com.patrigan.faction_craft.capabilities.raider.RaiderProvider;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void onAttachLevelCapabilities(AttachCapabilitiesEvent<World> event) {
        if(event.getObject() instanceof ServerWorld) {
            event.addCapability(new ResourceLocation(MODID, "raid_manager"), new RaidManagerProvider((ServerWorld) event.getObject()));
        }
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof LivingEntity){
            event.addCapability(new ResourceLocation(MODID, "applied_boosts"), new AppliedBoostsProvider());
        }
        if(event.getObject() instanceof MobEntity) {
            event.addCapability(new ResourceLocation(MODID, "raider"), new RaiderProvider((MobEntity) event.getObject()));
            event.addCapability(new ResourceLocation(MODID, "patroller"), new PatrollerProvider((MobEntity) event.getObject()));
        }
        if(event.getObject() instanceof PlayerEntity){
            event.addCapability(new ResourceLocation(MODID, "faction_interaction"), new FactionInteractionProvider());
        }
    }
}
