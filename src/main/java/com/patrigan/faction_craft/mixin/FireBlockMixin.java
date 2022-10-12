package com.patrigan.faction_craft.mixin;

import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

import static com.patrigan.faction_craft.block.ReconstructBlock.setReconstructBlock;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    private static Raid raid = null;
    private Map<BlockPos, BlockState> blockStateToReplace = new HashMap<>();

    @Inject(method = "Lnet/minecraft/world/level/block/FireBlock;tick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V",
            at = @At("HEAD"))
    public void factioncraft_tick(BlockState j2, ServerLevel serverLevel, BlockPos blockPos, RandomSource l1, CallbackInfo ci){
        RaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(serverLevel);
        raid = raidManagerCapability.getRaidAt(blockPos);
    }

    @ModifyVariable(method = "Lnet/minecraft/world/level/block/FireBlock;tryCatchFire(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/util/RandomSource;ILnet/minecraft/core/Direction;)V",
            remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;onCaughtFire(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/entity/LivingEntity;)V", remap = false),
            ordinal = 0)
    public BlockState factioncraft_tryCatchFire_afterSet(BlockState blockstate, Level pLevel, BlockPos pPos, int pChance, RandomSource pRandom, int arg4, Direction face){
        if(raid != null && !raid.isOver()){
            setReconstructBlock(pLevel, pPos, blockstate, raid);
        }
        return blockstate;
    }

    @ModifyVariable(method = "Lnet/minecraft/world/level/block/FireBlock;tick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 1),
            ordinal = 0)
    public BlockPos.MutableBlockPos factioncraft_tick_beforeSet(BlockPos.MutableBlockPos blockpos$mutable, BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
        blockStateToReplace.put(blockpos$mutable.immutable(), pLevel.getBlockState(blockpos$mutable));
        return blockpos$mutable;
    }

    @Inject(method = "Lnet/minecraft/world/level/block/FireBlock;tick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V",
            at = @At(value = "TAIL"))
    public void factioncraft_tick_afterSet(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand, CallbackInfo ci) {
        if(raid != null && !raid.isOver()){
            for(Map.Entry<BlockPos, BlockState> entry : blockStateToReplace.entrySet()){
                setReconstructBlock(pLevel, entry.getKey(), entry.getValue(), raid);
            }
        }
    }

}