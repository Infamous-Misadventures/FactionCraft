package com.patrigan.faction_craft.mixin;

import com.patrigan.faction_craft.capabilities.raider.IRaider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BellTileEntity.class, remap = false)
public abstract class BellTileEntityMixin extends TileEntity {

    @Shadow
    private List<LivingEntity> nearbyEntities;

    public BellTileEntityMixin(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    @Shadow
    public abstract boolean isRaiderWithinRange(LivingEntity p_222832_1_);

    @Shadow
    public abstract void glow(LivingEntity p_222827_1_);

    @Inject(method = "areRaidersNearby()Z",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void factioncraft_areRaidersNearby(CallbackInfoReturnable<Boolean> cir) {
        for(LivingEntity livingentity : this.nearbyEntities) {
            if(livingentity instanceof MobEntity) {
                IRaider raiderCapability = RaiderHelper.getRaiderCapability((MobEntity) livingentity);
                if (livingentity.isAlive() && !livingentity.removed && this.getBlockPos().closerThan(livingentity.position(), 32.0D) && raiderCapability != null && raiderCapability.hasActiveRaid()) {
                    cir.setReturnValue(true);
                    break;
                }
            }
        }
    }


    @Inject(method = "makeRaidersGlow(Lnet/minecraft/world/World;)V",
            at = @At(value = "TAIL"))
    private void factioncraft_makeRaidersGlow(World level, CallbackInfo ci) {
        if (!level.isClientSide) {
            this.nearbyEntities.stream().filter(this::isRaiderWithinRange).forEach(this::factioncraft_glow);
        }
    }

    @Inject(method = "isRaiderWithinRange(Lnet/minecraft/entity/LivingEntity;)Z",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void factioncraft_isRaiderWithinRange(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(livingEntity instanceof MobEntity) {
            IRaider raiderCapability = RaiderHelper.getRaiderCapability((MobEntity) livingEntity);
            cir.setReturnValue(livingEntity.isAlive() && !livingEntity.removed && this.getBlockPos().closerThan(livingEntity.position(), 48.0D) && raiderCapability != null && raiderCapability.hasActiveRaid());
        }
    }

    private void factioncraft_glow(LivingEntity p_222827_1_) {
        p_222827_1_.addEffect(new EffectInstance(Effects.GLOWING, 60));
    }

}