package com.patrigan.faction_craft.registry;

import com.patrigan.faction_craft.block.ReconstructBlock;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final RegistryObject<Block> RECONSTRUCT_BLOCK = registerBlock("reconstruct_block", () -> new ReconstructBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.NONE).noCollission().noOcclusion().noLootTable()));

    private static RegistryObject<Block> registerBlock(String id, Supplier<Block> sup) {
        RegistryObject<Block> blockRegistryObject = BLOCKS.register(id, sup);
        return blockRegistryObject;
    }

    public static void initRenderTypes(){
        ItemBlockRenderTypes.setRenderLayer(RECONSTRUCT_BLOCK.get(), RenderType.translucent());
    }

}