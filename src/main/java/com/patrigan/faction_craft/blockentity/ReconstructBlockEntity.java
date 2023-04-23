package com.patrigan.faction_craft.blockentity;

import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.raid.Raid;
import com.patrigan.faction_craft.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import static com.patrigan.faction_craft.config.FactionCraftConfig.ENABLE_RECONSTRUCT_BLOCKS;

public class ReconstructBlockEntity extends BlockEntity {

    private BlockState replacedBlockState = null;
    private int raidId = 0;
    private Raid raid = null;
    private int timer = -1000;
    private Mob mob = null;

    public ReconstructBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntityTypes.RECONSTRUCT_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    public static void clientTick(Level level, BlockPos blockPos, BlockState blockState, ReconstructBlockEntity blockEntity) {
        // NOOP
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, ReconstructBlockEntity blockEntity) {
        blockEntity.doServerTick((ServerLevel) level, blockPos);
    }

    private void doServerTick(ServerLevel level, BlockPos blockPos) {
        if(!ENABLE_RECONSTRUCT_BLOCKS.get()) {
            revertBlock(level);
            return;
        }
        loadRaid(level);
        if(raid == null){
            revertBlock(level);
            return;
        }
        if(timer == -1000){
            timer = FactionCraftConfig.RECONSTRUCT_TICK_DELAY.get() + level.getRandom().nextInt(FactionCraftConfig.RECONSTRUCT_VARIABLE_TICK_DELAY.get());
        }
        if(raid != null) {
            if (raid.isVictory() || raid.isStopped()) {
                timer--;
            }
            if (raid.isLoss()) {
                if (FactionCraftConfig.RECONSTRUCT_ON_LOSS.get()) {
                    timer--;
                } else {
                    level.removeBlock(this.worldPosition, false);
                }
            }
        }else if(mob != null){
            if(mob.isDeadOrDying()){
                timer--;
            }
        }else{
            timer--;
        }
        if(timer <= 0 || level.getDifficulty().equals(Difficulty.PEACEFUL)){
            revertBlock(level);
        }
    }

    private void loadRaid(ServerLevel level) {
        if(raid == null && raidId != 0) {
            RaidManager raidManager = RaidManagerHelper.getRaidManagerCapability(level);
            this.raid = raidManager.getRaids().get(raidId);
        }
    }

    private void revertBlock(ServerLevel level) {
        if(this.replacedBlockState != null) {
            level.setBlock(this.worldPosition, replacedBlockState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
        }else{
            level.removeBlock(this.worldPosition, false);
        }
    }

    public BlockState getReplacedBlockState() {
        return replacedBlockState;
    }

    public void setReplacedBlockState(BlockState replacedBlockState) {
        this.replacedBlockState = replacedBlockState;
    }

    public Raid getRaid() {
        return raid;
    }

    public void setRaid(Raid raid) {
        this.raid = raid;
    }

    public Mob getMob() {
        return mob;
    }

    public void setMob(Mob mob) {
        this.mob = mob;
    }

    @Override
    public void saveAdditional(CompoundTag pCompound) {
        super.saveAdditional(pCompound);
        if(replacedBlockState != null){
            pCompound.putInt("BlockState", Block.getId(replacedBlockState));
        }
        if(raid != null){
            pCompound.putInt("Raid", raid.getId());
        }
    }

    @Override
    public void load(CompoundTag pCompound) {
        super.load(pCompound);
        int blockStateId = pCompound.getInt("BlockState");
        if(blockStateId != 0){
            this.replacedBlockState = Block.stateById(blockStateId);
        }
        this.raidId = pCompound.getInt("Raid");
    }
}