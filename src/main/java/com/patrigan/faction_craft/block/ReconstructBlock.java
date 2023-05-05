package com.patrigan.faction_craft.block;

import com.patrigan.faction_craft.blockentity.ReconstructBlockEntity;
import com.patrigan.faction_craft.event.CalculateStrengthEvent;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.patrigan.faction_craft.registry.ModBlocks.RECONSTRUCT_BLOCK;
import static com.patrigan.faction_craft.registry.ModBlockEntityTypes.RECONSTRUCT_BLOCK_ENTITY;
import static com.patrigan.faction_craft.config.FactionCraftConfig.ENABLE_RECONSTRUCT_BLOCKS;

public class ReconstructBlock extends Block implements EntityBlock {

    public ReconstructBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return RECONSTRUCT_BLOCK_ENTITY.get().create(blockPos, blockState);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return false;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(isCollisionPlayerCreativeAndCrouching(pContext)){
            return Shapes.empty();
        }
        return super.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(isCollisionPlayerCreativeAndCrouching(pContext)){
            return Shapes.empty();
        }
        return super.getCollisionShape(pState, pLevel, pPos, pContext);
    }

    private boolean isCollisionPlayerCreativeAndCrouching(CollisionContext pContext) {
        return pContext instanceof EntityCollisionContext entityCollisionContext
                && entityCollisionContext.getEntity() instanceof Player player
                && (!player.isCreative() || !player.isCrouching());
    }

    public static void setReconstructBlock(Level level, BlockPos blockPos, BlockState blockState, Raid raid, LivingEntity mob) {
        if(!ENABLE_RECONSTRUCT_BLOCKS.get()) return;
        if(blockState.isAir()) return;
        if(blockState.is(RECONSTRUCT_BLOCK.get())) return;
        level.setBlock(blockPos, RECONSTRUCT_BLOCK.get().defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(blockEntity instanceof ReconstructBlockEntity){
            ReconstructBlockEntity reconstructBlockEntity = (ReconstructBlockEntity) blockEntity;
            reconstructBlockEntity.setRaid(raid);
            reconstructBlockEntity.setReplacedBlockState(blockState);
            reconstructBlockEntity.setEntity(mob);
        }
        for (Direction direction : Direction.values()) {
            BlockPos relativePos = blockPos.relative(direction);
            BlockState neighbour = level.getBlockState(relativePos);
            if(!neighbour.canSurvive(level, relativePos)){
                setReconstructBlock(level, relativePos, neighbour, raid, null);
            }
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, RECONSTRUCT_BLOCK_ENTITY.get(), level.isClientSide ? ReconstructBlockEntity::clientTick : ReconstructBlockEntity::serverTick);
    }

    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
        return p_152134_ == p_152133_ ? (BlockEntityTicker<A>)p_152135_ : null;
    }

}