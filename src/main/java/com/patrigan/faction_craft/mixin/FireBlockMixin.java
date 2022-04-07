package com.patrigan.faction_craft.mixin;

import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.core.jmx.Server;
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

    @Inject(method = "Lnet/minecraft/world/level/block/FireBlock;tick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Ljava/util/Random;)V",
            at = @At("HEAD"))
    public void factioncraft_tick(BlockState j2, ServerLevel serverWorld, BlockPos blockPos, Random l1, CallbackInfo ci){
        RaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(serverWorld);
        raid = raidManagerCapability.getRaidAt(blockPos);
    }

    @Inject(method = "Lnet/minecraft/world/level/block/FireBlock;tryCatchFire(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILjava/util/Random;ILnet/minecraft/core/Direction;)V",
            remap = false,
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 1, remap = true), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void factioncraft_tryCatchFire_beforeSet(Level pLevel, BlockPos pPos, int pChance, Random pRandom, int arg4, Direction face, CallbackInfo ci, int i, BlockState blockstate) {
        blockStateToReplace = blockstate;
    }

    @Inject(method = "Lnet/minecraft/world/level/block/FireBlock;tryCatchFire(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILjava/util/Random;ILnet/minecraft/core/Direction;)V",
            remap = false,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;onCaughtFire(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/entity/LivingEntity;)V", remap = false))
    public void factioncraft_tryCatchFire_afterSet(Level world, BlockPos pPos, int pChance, Random pRandom, int pAge, Direction face, CallbackInfo ci){
        if(raid != null && !raid.isOver()){
            setReconstructBlock(world, pPos, blockStateToReplace, raid);
        }
    }

    @Inject(method = "Lnet/minecraft/world/level/block/FireBlock;tick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Ljava/util/Random;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void factioncraft_tick_beforeSet(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRand, CallbackInfo ci, BlockState blockstate, boolean flag, int i, int j, boolean flag1, int k, BlockPos.MutableBlockPos blockpos$mutable, int l, int i1, int j1, int k1, int l1, int i2, int j2) {
        blockStateToReplace = pLevel.getBlockState(blockpos$mutable);
    }

    @Inject(method = "Lnet/minecraft/world/level/block/FireBlock;tick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Ljava/util/Random;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void factioncraft_tick_afterSet(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRand, CallbackInfo ci, BlockState blockstate, boolean flag, int i, int j, boolean flag1, int k, BlockPos.MutableBlockPos blockpos$mutable, int l, int i1, int j1, int k1, int l1, int i2, int j2) {
        if(raid != null && !raid.isOver()){
            setReconstructBlock(pLevel, pPos, blockStateToReplace, raid);
        }
    }

}