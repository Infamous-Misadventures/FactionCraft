package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

import static com.patrigan.faction_craft.boost.BoostProviders.WEAR_ARMOR;

public class WearArmorBoost extends Boost {

    public static final Codec<WearArmorBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("item_stacks").forGetter(WearArmorBoost::getItemStacks),
            Codec.INT.optionalFieldOf("strength_adjustment", 1).forGetter(WearArmorBoost::getStrengthAdjustment),
            BoostType.CODEC.optionalFieldOf("boost_type", BoostType.ARMOR).forGetter(WearArmorBoost::getType),
            Rarity.CODEC.fieldOf("rarity").forGetter(WearArmorBoost::getRarity)
    ).apply(instance, WearArmorBoost::new));

    private final List<ItemStack> itemStacks;
    private final int strengthAdjustment;
    private final BoostType boostType;
    private final Rarity rarity;

    public WearArmorBoost(List<ItemStack> itemStacks, int strengthAdjustment, BoostType boostType, Rarity rarity) {
        super(WEAR_ARMOR);
        this.itemStacks = itemStacks;
        this.strengthAdjustment = strengthAdjustment;
        this.boostType = boostType;
        this.rarity = rarity;
    }

    public List<ItemStack> getItemStacks() {
        return itemStacks;
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
        itemStacks.forEach(itemStack -> ((MobEntity) livingEntity).setItemSlot(((MobEntity) livingEntity).getEquipmentSlotForItem(itemStack), itemStack));
        super.apply(livingEntity);
        return strengthAdjustment;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return livingEntity instanceof MobEntity;
    }
}
