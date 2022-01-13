package com.patrigan.faction_craft;

import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.boost.BoostProviders;
import com.patrigan.faction_craft.capabilities.appliedboosts.AppliedBoosts;
import com.patrigan.faction_craft.capabilities.appliedboosts.AppliedBoostsStorage;
import com.patrigan.faction_craft.capabilities.appliedboosts.IAppliedBoosts;
import com.patrigan.faction_craft.capabilities.patroller.IPatroller;
import com.patrigan.faction_craft.capabilities.patroller.Patroller;
import com.patrigan.faction_craft.capabilities.patroller.PatrollerStorage;
import com.patrigan.faction_craft.capabilities.raider.IRaider;
import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raider.RaiderStorage;
import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerStorage;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.entity.ai.brain.ModActivities;
import com.patrigan.faction_craft.network.NetworkHandler;
import com.patrigan.faction_craft.util.RegistryDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("faction_craft")
public class FactionCraft
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "faction_craft";
    public static RegistryDispatcher<Boost.Serializer<?>, Boost> BOOST_DISPATCHER;

    public FactionCraft() {
        // Register the setup method for modloading
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FactionCraftConfig.COMMON_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BOOST_DISPATCHER = RegistryDispatcher.makeDispatchForgeRegistry(
                modEventBus,
                Boost.class,
                new ResourceLocation(MODID, "boost"),
                builder -> builder
                        .disableSaving()
                        .disableSync()
        );
        ModActivities.ACTIVITIES.register(modEventBus);
        BoostProviders.BOOST_PROVIDERS.register(modEventBus);
    }

    private void setup(final FMLCommonSetupEvent event){
        CapabilityManager.INSTANCE.register(IRaidManager.class, new RaidManagerStorage(), RaidManager::new);
        CapabilityManager.INSTANCE.register(IRaider.class, new RaiderStorage(), Raider::new);
        CapabilityManager.INSTANCE.register(IPatroller.class, new PatrollerStorage(), Patroller::new);
        CapabilityManager.INSTANCE.register(IAppliedBoosts.class, new AppliedBoostsStorage(), AppliedBoosts::new);
        event.enqueueWork(NetworkHandler::init);
    }

}
