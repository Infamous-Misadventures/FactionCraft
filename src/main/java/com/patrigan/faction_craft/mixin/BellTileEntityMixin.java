package com.patrigan.faction_craft.mixin;

import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BellBlockEntity.class, remap = false)
public abstract class BellTileEntityMixin extends BlockEntity {

    @Invoker("isRaiderWithinRange")
    public static boolean factioncraft_invokeIsRaiderWithinRange(BlockPos p_155197_, LivingEntity p_155198_){
        //noop
        throw new RuntimeException("should not be here");
    }

    public BellTileEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }


    @Inject(method = "areRaidersNearby(Lnet/minecraft/core/BlockPos;Ljava/util/List;)Z",
            at = @At(value = "RETURN"),
            cancellable = true)
    private static void factioncraft_areRaidersNearby(BlockPos blockPos, List<LivingEntity> nearbyEntities, CallbackInfoReturnable<Boolean> cir) {
        for(LivingEntity livingentity : nearbyEntities) {
            if(livingentity instanceof Mob mob) {
                Raider raiderCapability = RaiderHelper.getRaiderCapability(mob);
                if (livingentity.isAlive() && blockPos.closerThan(livingentity.position(), 32.0D) && raiderCapability != null && raiderCapability.hasActiveRaid()) {
                    cir.setReturnValue(true);
                    break;
                }
            }
        }
    }


    @Inject(method = "makeRaidersGlow(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/List;)V",
            at = @At(value = "TAIL"))
    private static void factioncraft_makeRaidersGlow(Level level, BlockPos blockPos, List<LivingEntity> nearbyEntities, CallbackInfo ci) {
        if (!level.isClientSide) {
            nearbyEntities.stream().filter(livingEntity -> BellTileEntityMixin.factioncraft_invokeIsRaiderWithinRange(blockPos, livingEntity)).forEach(BellTileEntityMixin::factioncraft_glow);
        }
    }

    @Inject(method = "isRaiderWithinRange(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At(value = "RETURN"),
            cancellable = true)
    private static void factioncraft_isRaiderWithinRange(BlockPos blockPos, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(livingEntity instanceof Mob mob) {
            Raider raiderCapability = RaiderHelper.getRaiderCapability(mob);
            cir.setReturnValue(livingEntity.isAlive()&& blockPos.closerThan(livingEntity.position(), 48.0D) && raiderCapability != null && raiderCapability.hasActiveRaid());
        }
    }

    private static void factioncraft_glow(LivingEntity p_222827_1_) {
        p_222827_1_.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
    }

}