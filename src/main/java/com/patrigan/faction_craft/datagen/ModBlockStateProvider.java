package com.patrigan.faction_craft.datagen;


import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static com.patrigan.faction_craft.block.ModBlocks.RECONSTRUCT_BLOCK;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(RECONSTRUCT_BLOCK.get());
    }

}
