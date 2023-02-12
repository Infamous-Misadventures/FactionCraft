package com.patrigan.faction_craft;

import com.patrigan.faction_craft.block.ModBlocks;
import com.patrigan.faction_craft.blockentity.ModBlockEntityTypes;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.boost.BoostProviders;
import com.patrigan.faction_craft.capabilities.ModCapabilities;
import com.patrigan.faction_craft.compat.GuardVillagerCompat;
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
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FactionCraftConfig.COMMON_SPEC);
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);
        // Register the doClientStuff method for modloading

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

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
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);

        registerCompatEvents();

        ModCapabilities.setupCapabilities();
    }

    private void registerCompatEvents() {
        GuardVillagerCompat.registerEventHandlers();
    }

    private void setup(final FMLCommonSetupEvent event){
        event.enqueueWork(NetworkHandler::init);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ModBlocks.initRenderTypes();
    }

}
