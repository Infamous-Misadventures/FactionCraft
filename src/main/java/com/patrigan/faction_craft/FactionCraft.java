package com.patrigan.faction_craft;

import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.boost.BoostProviders;
import com.patrigan.faction_craft.capabilities.ModCapabilities;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.entity.ai.brain.ModActivities;
import com.patrigan.faction_craft.network.NetworkHandler;
import com.patrigan.faction_craft.util.RegistryDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        ModCapabilities.setupCapabilities();
    }

    private void setup(final FMLCommonSetupEvent event){
        event.enqueueWork(NetworkHandler::init);
    }

}
