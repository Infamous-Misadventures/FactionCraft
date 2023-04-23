package com.patrigan.faction_craft.registry;

import com.patrigan.faction_craft.blockentity.ReconstructBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static com.patrigan.faction_craft.registry.ModBlocks.RECONSTRUCT_BLOCK;

public class ModBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);


    public static final RegistryObject<BlockEntityType<ReconstructBlockEntity>> RECONSTRUCT_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("reconstruct_block_entity",
            () -> BlockEntityType.Builder.of(ReconstructBlockEntity::new, RECONSTRUCT_BLOCK.get()).build(null));
}