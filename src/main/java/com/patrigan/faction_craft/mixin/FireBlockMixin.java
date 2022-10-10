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

import static com.patrigan.faction_craft.block.ReconstructBlock.setReconstructBlock;

@Mixin(FireBlock.class)
public class FireBlockMixin {
    private static Raid raid = null;
    private static BlockState blockStateToReplace;

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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1),
            ordinal = 0)
    public BlockPos.MutableBlockPos factioncraft_tick_beforeSet(BlockPos.MutableBlockPos blockpos$mutable, BlockState pState, , ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
        blockStateToReplace = pLevel.getBlockState(blockpos$mutable);
        return blockpos$mutable;
    }

    @ModifyVariable(method = "Lnet/minecraft/world/level/block/FireBlock;tick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1))
    public BlockPos.MutableBlockPos factioncraft_tick_afterSet(BlockPos.MutableBlockPos blockpos$mutable, BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
        if(raid != null && !raid.isOver()){
            setReconstructBlock(pLevel, blockpos$mutable, blockStateToReplace, raid);
        }
        return blockpos$mutable;
    }

}