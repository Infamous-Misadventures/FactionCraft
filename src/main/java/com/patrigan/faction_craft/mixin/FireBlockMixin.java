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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

import static com.patrigan.faction_craft.block.ReconstructBlock.setReconstructBlock;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    private static Raid raid = null;
    private static BlockState blockStateToReplace;

    @Inject(method = "Lnet/minecraft/block/FireBlock;tick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V",
            at = @At("HEAD"))
    public void factioncraft_tick(BlockState j2, ServerWorld serverWorld, BlockPos blockPos, Random l1, CallbackInfo ci){
        IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(serverWorld);
        raid = raidManagerCapability.getRaidAt(blockPos);
    }

    @Inject(method = "Lnet/minecraft/block/FireBlock;tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ILjava/util/Random;ILnet/minecraft/util/Direction;)V",
            remap = false,
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 1, remap = true), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void factioncraft_tryCatchFire_beforeSet(World pLevel, BlockPos pPos, int pChance, Random pRandom, int arg4, Direction face, CallbackInfo ci, int i, BlockState blockstate) {
        blockStateToReplace = blockstate;
    }

    @Inject(method = "Lnet/minecraft/block/FireBlock;tryCatchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ILjava/util/Random;ILnet/minecraft/util/Direction;)V",
            remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;catchFire(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;Lnet/minecraft/entity/LivingEntity;)V", remap = false))
    public void factioncraft_tryCatchFire_afterSet(World world, BlockPos pPos, int pChance, Random pRandom, int pAge, Direction face, CallbackInfo ci){
        if(raid != null && !raid.isOver()){
            setReconstructBlock(world, pPos, blockStateToReplace, raid);
        }
    }

    @Inject(method = "Lnet/minecraft/block/FireBlock;tick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;setBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void factioncraft_tick_beforeSet(BlockState pState, ServerWorld pLevel, BlockPos pPos, Random pRand, CallbackInfo ci, BlockState blockstate, boolean flag, int i, int j, boolean flag1, int k, BlockPos.Mutable blockpos$mutable, int l, int i1, int j1, int k1, int l1, int i2, int j2) {
        blockStateToReplace = pLevel.getBlockState(blockpos$mutable);
    }

    @Inject(method = "Lnet/minecraft/block/FireBlock;tick(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/server/ServerWorld;setBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void factioncraft_tick_afterSet(BlockState pState, ServerWorld pLevel, BlockPos pPos, Random pRand, CallbackInfo ci, BlockState blockstate, boolean flag, int i, int j, boolean flag1, int k, BlockPos.Mutable blockpos$mutable, int l, int i1, int j1, int k1, int l1, int i2, int j2) {
        if(raid != null && !raid.isOver()){
            setReconstructBlock(pLevel, pPos, blockStateToReplace, raid);
        }
    }

}
