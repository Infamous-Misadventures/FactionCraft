package com.patrigan.faction_craft.block;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.level.ExplosionEvent;
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
        if(event.getLevel().isClientSide()) return;
        RaidManager raidManager = RaidManagerHelper.getRaidManagerCapability(event.getLevel());
        Raid raid = raidManager.getRaidAt(new BlockPos(event.getExplosion().getPosition()));
        if(canBecomeReconstructBlock(raid, event.getExplosion().getSourceMob())) {
            List<BlockPos> blockPosList = event.getAffectedBlocks();
            blockPosList.forEach(blockPos -> {
                setReconstructBlock(event.getLevel(), blockPos, event.getLevel().getBlockState(blockPos), raid, event.getExplosion().getSourceMob());
            });
            event.getAffectedBlocks().clear();
        }
    }

    private static boolean canBecomeReconstructBlock(Raid raid, LivingEntity livingEntity) {
        if(!FactionCraftConfig.ENABLE_RECONSTRUCT_BLOCKS.get()) {
            return false;
        }
        return (raid != null && !raid.isOver()) || ((FactionCraftConfig.ENABLE_RECONSTRUCT_BLOCKS_OUTSIDE_RAIDS.get()) &&
                checkGaiaFaction(livingEntity) && isPlayer(livingEntity));
    }

    private static boolean isPlayer(LivingEntity livingEntity) {
        return FactionCraftConfig.ENABLE_RECONSTRUCT_BLOCKS_FROM_PLAYERS.get() || !(livingEntity instanceof Player);
    }

    private static boolean checkGaiaFaction(LivingEntity livingEntity) {
        if(livingEntity == null) return FactionCraftConfig.ENABLE_RECONSTRUCT_BLOCKS_FROM_GAIA.get();
        Faction faction = FactionEntityHelper.getFactionEntityCapability(livingEntity).getFaction();
        return FactionCraftConfig.ENABLE_RECONSTRUCT_BLOCKS_FROM_GAIA.get() || (faction != null && faction != Faction.GAIA);
    }
}