package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

import static com.patrigan.faction_craft.boost.BoostProviders.ATTRIBUTE;
import static net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES;

public class AttributeBoost extends Boost {

    public static final Codec<AttributeBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("attribute").forGetter(AttributeBoost::getAttributeLocation),
            Codec.DOUBLE.fieldOf("adjustment").forGetter(AttributeBoost::getAdjustment),
            Codec.INT.optionalFieldOf("strength_adjustment", 1).forGetter(AttributeBoost::getStrengthAdjustment),
            Rarity.CODEC.fieldOf("rarity").forGetter(AttributeBoost::getRarity)
    ).apply(instance, AttributeBoost::new));

    private final ResourceLocation attributeLocation;
    private final Attribute attribute;
    private final double adjustment;
    private final int strengthAdjustment;
    private final Rarity rarity;

    public AttributeBoost(ResourceLocation attributeLocation, double adjustment, int strengthAdjustment, Rarity rarity) {
        super(ATTRIBUTE);
        this.attributeLocation = attributeLocation;
        this.attribute = ATTRIBUTES.getValue(attributeLocation);
        this.adjustment = adjustment;
        this.strengthAdjustment = strengthAdjustment;
        this.rarity = rarity;
    }

    public ResourceLocation getAttributeLocation() {
        return attributeLocation;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public double getAdjustment() {
        return adjustment;
    }

    public int getStrengthAdjustment() {
        return strengthAdjustment;
    }

    @Override
    public BoostType getType() {
        return BoostType.ATTRIBUTE;
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public int apply(LivingEntity livingEntity) {
        if(!livingEntity.getAttributes().hasAttribute(attribute)){
            return 0;
        }
        AttributeModifier modifier = new AttributeModifier("Attribute Boost", adjustment, AttributeModifier.Operation.ADDITION);
        livingEntity.getAttribute(attribute).addPermanentModifier(modifier);
        super.apply(livingEntity);
        return strengthAdjustment;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return livingEntity.getAttributes().hasAttribute(attribute);
    }
}
