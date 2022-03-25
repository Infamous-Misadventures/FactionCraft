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
import net.minecraft.world.Difficulty;
import net.minecraftforge.common.util.Constants;

public class ReconstructBlockEntity extends TileEntity implements ITickableTileEntity {

    private BlockState replacedBlockState = null;
    private int raidId = 0;
    private Raid raid = null;
    private int timer = -1000;

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
        if(this.level.isClientSide()) return;
        if(raid == null && raidId != 0) {
            IRaidManager raidManager = RaidManagerHelper.getRaidManagerCapability(this.getLevel());
            if (raidManager != null) {
                this.raid = raidManager.getRaids().get(raidId);
            }
        }
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
        if(timer <= 0 || raid == null || this.getLevel().getDifficulty().equals(Difficulty.PEACEFUL)){
            this.getLevel().setBlock(this.worldPosition, replacedBlockState, Constants.BlockFlags.BLOCK_UPDATE | Constants.BlockFlags.UPDATE_NEIGHBORS);
        }
    }

    @Override
    public BlockState getBlockState() {
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

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        pCompound = super.save(pCompound);
        if(replacedBlockState != null){
//            this.blockState.
            pCompound.putInt("BlockState", Block.getId(replacedBlockState));
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
            this.replacedBlockState = Block.stateById(blockStateId);
        }
        INBT blockEntity = pCompound.get("BlockEntity");
        this.raidId = pCompound.getInt("Raid");
    }
}