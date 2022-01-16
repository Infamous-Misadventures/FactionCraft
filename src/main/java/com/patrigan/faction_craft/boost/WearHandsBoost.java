package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import static com.patrigan.faction_craft.boost.Boost.BoostType.MAINHAND;
import static com.patrigan.faction_craft.boost.Boost.BoostType.OFFHAND;
import static com.patrigan.faction_craft.boost.BoostProviders.WEAR_HANDS;

public class WearHandsBoost extends Boost {

    public static final Codec<WearHandsBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.optionalFieldOf("item", null).forGetter(WearHandsBoost::getItem),
            Codec.INT.optionalFieldOf("strength_adjustment", 1).forGetter(WearHandsBoost::getStrengthAdjustment),
            BoostType.CODEC.optionalFieldOf("boost_type", MAINHAND).forGetter(WearHandsBoost::getType),
            Rarity.CODEC.fieldOf("rarity").forGetter(WearHandsBoost::getRarity)
    ).apply(instance, WearHandsBoost::new));

    private final ItemStack item;
    private final int strengthAdjustment;
    private final BoostType boostType;
    private final Rarity rarity;

    public WearHandsBoost(ItemStack item, int strengthAdjustment, BoostType boostType, Rarity rarity) {
        super(WEAR_HANDS);
        this.item = item;
        this.strengthAdjustment = strengthAdjustment;
        this.boostType = boostType;
        this.rarity = rarity;
    }

    public ItemStack getItem() {
        return item;
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
        if(boostType.equals(OFFHAND)){
            livingEntity.setItemSlot(EquipmentSlotType.OFFHAND, item);
        }else {
            livingEntity.setItemSlot(EquipmentSlotType.MAINHAND, item);
        }
        super.apply(livingEntity);
        return strengthAdjustment;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return livingEntity instanceof MobEntity;
    }
}
