package com.patrigan.faction_craft.block;

import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static com.patrigan.faction_craft.block.ReconstructBlock.setReconstructBlock;

@Mod.EventBusSubscriber(modid = MODID)
public class BlockEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExplosionDetonateEvent(ExplosionEvent.Detonate event){
        if(event.getWorld().isClientSide()) return;
        RaidManager raidManager = RaidManagerHelper.getRaidManagerCapability(event.getWorld());
        Raid raid = raidManager.getRaidAt(new BlockPos(event.getExplosion().getPosition()));
        if(raid != null && !raid.isOver()) {
            List<BlockPos> blockPosList = event.getAffectedBlocks();
            blockPosList.forEach(blockPos -> {
                setReconstructBlock(event.getWorld(), blockPos, event.getWorld().getBlockState(blockPos), raid);
            });
            event.getAffectedBlocks().clear();
        }
    }
}