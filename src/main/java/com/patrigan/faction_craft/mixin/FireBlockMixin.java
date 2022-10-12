package com.patrigan.faction_craft.mixin;

import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.patrigan.faction_craft.block.ReconstructBlock.setReconstructBlock;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    private static Raid raid = null;
    private Map<BlockPos, BlockState> blockStateToReplace = new HashMap<>();

    @Inject(method = "Lnet/minecraft/block/FireBlock;tick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V",
            at = @At("HEAD"))
    public void factioncraft_tick(BlockState j2, ServerWorld serverWorld, BlockPos blockPos, Random l1, CallbackInfo ci){
        IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(serverWorld);
        raid = raidManagerCapability.getRaidAt(blockPos);
    }

    @ModifyVariable(method = "Lnet/minecraft/block/FireBlock;tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ILjava/util/Random;ILnet/minecraft/util/Direction;)V",
            remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;catchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;Lnet/minecraft/entity/LivingEntity;)V", remap = false),
            ordinal = 0)
    public BlockState factioncraft_tryCatchFire_afterSet(BlockState blockstate, World pLevel, BlockPos pPos, int pChance, Random pRandom, int pAge, Direction face){
        if(raid != null && !raid.isOver()){
            setReconstructBlock(pLevel, pPos, blockstate, raid);
        }
        return blockstate;
    }

    @ModifyVariable(method = "Lnet/minecraft/block/FireBlock;tick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 1),
            ordinal = 0)
    public BlockPos.Mutable factioncraft_tick_beforeSet(BlockPos.Mutable blockpos$mutable, BlockState pState, ServerWorld pLevel, BlockPos pPos, Random pRand) {
        blockStateToReplace.put(blockpos$mutable.immutable(), pLevel.getBlockState(blockpos$mutable));
        return blockpos$mutable;
    }

    @Inject(method = "Lnet/minecraft/block/FireBlock;tick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V",
            at = @At(value = "TAIL"))
    public void factioncraft_tick_afterSet(BlockState p_53449_, ServerWorld p_53450_, BlockPos p_53451_, Random p_53452_, CallbackInfo ci) {
        if(raid != null && !raid.isOver()){
            for(Map.Entry<BlockPos, BlockState> entry : blockStateToReplace.entrySet()){
                setReconstructBlock(p_53450_, entry.getKey(), entry.getValue(), raid);
            }
        }
    }

}
