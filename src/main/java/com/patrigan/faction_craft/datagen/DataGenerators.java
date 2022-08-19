package com.patrigan.faction_craft.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeClient()) {
            generator.addProvider(true, new ModBlockStateProvider(generator, event.getExistingFileHelper()));
            generator.addProvider(true, new ModLanguageProvider(generator, "en_us"));
        }
        if (event.includeServer()) {
        }
    }
}