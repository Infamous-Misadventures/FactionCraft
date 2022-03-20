package com.patrigan.faction_craft.blockentity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static com.patrigan.faction_craft.block.ModBlocks.RECONSTRUCT_BLOCK;

public class ModBlockEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);


    public static final RegistryObject<TileEntityType<ReconstructBlockEntity>> RECONSTRUCT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reconstruct_block_entity",
            () -> TileEntityType.Builder.of(ReconstructBlockEntity::new, RECONSTRUCT_BLOCK.get()).build(null));
}
