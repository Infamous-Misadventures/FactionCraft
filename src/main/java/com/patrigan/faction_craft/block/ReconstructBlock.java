package com.patrigan.faction_craft.block;

import com.patrigan.faction_craft.blockentity.ReconstructBlockEntity;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

import static com.patrigan.faction_craft.block.ModBlocks.RECONSTRUCT_BLOCK;
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

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
        return 0;
    }

    public static void setReconstructBlock(World world, BlockPos blockPos, BlockState blockState, Raid raid) {
        if(blockState.isAir()) return;
        if(blockState.is(RECONSTRUCT_BLOCK.get())) return;
        world.setBlock(blockPos, RECONSTRUCT_BLOCK.get().defaultBlockState(), Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.UPDATE_NEIGHBORS);
        TileEntity tileEntity = world.getBlockEntity(blockPos);
        if(tileEntity instanceof ReconstructBlockEntity){
            ReconstructBlockEntity reconstructBlockEntity = (ReconstructBlockEntity) tileEntity;
            reconstructBlockEntity.setRaid(raid);
            reconstructBlockEntity.setReplacedBlockState(blockState);
        }
        for (Direction direction : Direction.values()) {
            BlockPos relativePos = blockPos.relative(direction);
            BlockState neighbour = world.getBlockState(relativePos);
            if(!neighbour.canSurvive(world, relativePos)){
                setReconstructBlock(world, relativePos, neighbour, raid);
            }
        }
    }

}
