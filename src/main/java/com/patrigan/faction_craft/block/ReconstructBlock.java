package com.patrigan.faction_craft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

import static com.patrigan.faction_craft.blockentity.ModBlockEntityTypes.RECONSTRUCT_BLOCK_ENTITY;

public class ReconstructBlock extends Block {

    public ReconstructBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return RECONSTRUCT_BLOCK_ENTITY.get().create();
    }
}
