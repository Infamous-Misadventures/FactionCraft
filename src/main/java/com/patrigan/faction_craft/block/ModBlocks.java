package com.patrigan.faction_craft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<Block> RECONSTRUCT_BLOCK = registerBlock("reconstruct_block", () -> new ReconstructBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.NONE).noCollission().noOcclusion().noDrops()));

    private static RegistryObject<Block> registerBlock(String id, Supplier<Block> sup) {
        RegistryObject<Block> blockRegistryObject = BLOCKS.register(id, sup);
        return blockRegistryObject;
    }

    public static void initRenderTypes(){
        RenderTypeLookup.setRenderLayer(RECONSTRUCT_BLOCK.get(), RenderType.translucent());
    }

}
