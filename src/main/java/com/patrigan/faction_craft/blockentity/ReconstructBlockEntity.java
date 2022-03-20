package com.patrigan.faction_craft.blockentity;

import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class ReconstructBlockEntity extends TileEntity implements ITickableTileEntity {

    BlockState blockState = null;
    CompoundNBT blockEntityCompound = null;
    Raid raid = null;
    int timer = -1000;

    public ReconstructBlockEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public ReconstructBlockEntity() {
        this(ModBlockEntityTypes.RECONSTRUCT_BLOCK_ENTITY.get());
    }

    @Override
    public TileEntityType<?> getType() {
        return ModBlockEntityTypes.RECONSTRUCT_BLOCK_ENTITY.get();
    }

    @Override
    public void tick() {
        if(this.getLevel() == null || this.getLevel().isClientSide()) return;
        if(timer == -1000){
            timer = FactionCraftConfig.RECONSTRUCT_TICK_DELAY.get() + this.getLevel().getRandom().nextInt(FactionCraftConfig.RECONSTRUCT_VARIABLE_TICK_DELAY.get());
        }
        if(raid != null && raid.isVictory()) {
            timer--;
        }
        if(raid != null && raid.isLoss()){
            if(FactionCraftConfig.RECONSTRUCT_ON_LOSS.get()){
                timer--;
            }else{
                this.getLevel().removeBlock(this.worldPosition, false);
            }
        }
        if(timer <= 0 || raid == null){
            this.getLevel().setBlockAndUpdate(this.worldPosition, blockState);
            TileEntity blockEntity = this.getLevel().getBlockEntity(worldPosition);
            if(blockEntity != null){
                blockEntity.load(blockState, this.blockEntityCompound);
            }
        }
    }

    @Override
    public BlockState getBlockState() {
        return blockState;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    public CompoundNBT getBlockEntityCompound() {
        return blockEntityCompound;
    }

    public void setBlockEntityCompound(CompoundNBT blockEntityCompound) {
        this.blockEntityCompound = blockEntityCompound;
    }

    public Raid getRaid() {
        return raid;
    }

    public void setRaid(Raid raid) {
        this.raid = raid;
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        pCompound = super.save(pCompound);
        if(blockState != null){
//            this.blockState.
            pCompound.putInt("BlockState", Block.getId(blockState));
        }
        if(blockEntityCompound != null){
            pCompound.put("BlockEntity", blockEntityCompound);
        }
        if(raid != null){
            pCompound.putInt("Raid", raid.getId());
        }
        return pCompound;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT pCompound) {
        super.load(blockState, pCompound);
        int blockStateId = pCompound.getInt("BlockState");
        if(blockStateId != 0){
            this.blockState = Block.stateById(blockStateId);
        }
        INBT blockEntity = pCompound.get("BlockEntity");
        if(blockEntity instanceof CompoundNBT){
            this.blockEntityCompound = (CompoundNBT) blockEntity;
        }
        IRaidManager raidManager = RaidManagerHelper.getRaidManagerCapability(this.getLevel());
        if(raidManager != null) {
            int raidId = pCompound.getInt("Raid");
            this.raid = raidManager.getRaids().get(raidId);
        }
    }
}