package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import java.util.List;

import static com.patrigan.faction_craft.boost.BoostProviders.WEAR_HANDS;

public class WearHandsBoost extends Boost {

    public static final Codec<WearHandsBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.optionalFieldOf("main_hand", null).forGetter(WearHandsBoost::getMainHand),
            ItemStack.CODEC.optionalFieldOf("off_hand", null).forGetter(WearHandsBoost::getOffHand),
            Codec.INT.optionalFieldOf("strength_adjustment", 1).forGetter(WearHandsBoost::getStrengthAdjustment),
            BoostType.CODEC.optionalFieldOf("boost_type", BoostType.MAINHAND).forGetter(WearHandsBoost::getType),
            Rarity.CODEC.fieldOf("rarity").forGetter(WearHandsBoost::getRarity)
    ).apply(instance, WearHandsBoost::new));

    private final ItemStack mainHand;
    private final ItemStack offHand;
    private final int strengthAdjustment;
    private final BoostType boostType;
    private final Rarity rarity;

    public WearHandsBoost(ItemStack mainHand, ItemStack offHand, int strengthAdjustment, BoostType boostType, Rarity rarity) {
        super(WEAR_HANDS);
        this.mainHand = mainHand;
        this.offHand = offHand;
        this.strengthAdjustment = strengthAdjustment;
        this.boostType = boostType;
        this.rarity = rarity;
    }

    public ItemStack getMainHand() {
        return mainHand;
    }

    public ItemStack getOffHand() {
        return offHand;
    }

    public int getStrengthAdjustment() {
        return strengthAdjustment;
    }

    @Override
    public BoostType getType() {
        return boostType;
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public int apply(LivingEntity livingEntity) {
        if (!canApply(livingEntity)) {
            return 0;
        }
        if(mainHand != null){
            livingEntity.setItemSlot(EquipmentSlotType.MAINHAND, mainHand);
        }
        if(offHand != null){
            livingEntity.setItemSlot(EquipmentSlotType.OFFHAND, offHand);
        }
        super.apply(livingEntity);
        return strengthAdjustment;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return livingEntity instanceof MobEntity;
    }
}
