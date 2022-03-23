package com.patrigan.faction_craft.block;

import com.patrigan.faction_craft.blockentity.ReconstructBlockEntity;
import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static com.patrigan.faction_craft.block.ModBlocks.RECONSTRUCT_BLOCK;

@Mod.EventBusSubscriber(modid = MODID)
public class BlockEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExplosionDetonateEvent(ExplosionEvent.Detonate event){
        if(event.getWorld().isClientSide()) return;
        IRaidManager raidManager = RaidManagerHelper.getRaidManagerCapability(event.getWorld());
        Raid raid = raidManager.getRaidAt(new BlockPos(event.getExplosion().getPosition()));
        if(raid != null) {
            List<BlockPos> blockPosList = event.getAffectedBlocks();
            blockPosList.forEach(blockPos -> {
                setReconstructBlock(event.getWorld(), blockPos, raid);
            });
            event.getAffectedBlocks().clear();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBreakEvent(BlockEvent.BreakEvent event){
        if(event.getWorld().isClientSide()) return;
        if(!(event.getWorld() instanceof World)) return;
        IRaidManager raidManager = RaidManagerHelper.getRaidManagerCapability((World) event.getWorld());
        Raid raid = raidManager.getRaidAt(new BlockPos(event.getPos()));
        if(raid != null) {
            setReconstructBlock((World) event.getWorld(), event.getPos(), raid);
            event.setCanceled(true);
        }
    }

    public static void setReconstructBlock(World world, BlockPos blockPos, Raid raid) {
        BlockState blockState = world.getBlockState(blockPos);
        if(blockState.isAir()) return;
        TileEntity blockEntity = world.getBlockEntity(blockPos);
        CompoundNBT compoundNBT = new CompoundNBT();
        if(blockEntity != null){
            compoundNBT = blockEntity.save(compoundNBT);
        }
        world.setBlockAndUpdate(blockPos, RECONSTRUCT_BLOCK.get().defaultBlockState());
        TileEntity tileEntity = world.getBlockEntity(blockPos);
        if(tileEntity instanceof ReconstructBlockEntity){
            ReconstructBlockEntity reconstructBlockEntity = (ReconstructBlockEntity) tileEntity;
            reconstructBlockEntity.setRaid(raid);
            reconstructBlockEntity.setBlockState(blockState);
            if(blockEntity != null){
                reconstructBlockEntity.setBlockEntityCompound(compoundNBT);
            }
        }
    }
}
