package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.capabilities.appliedboosts.AppliedBoosts;
import com.patrigan.faction_craft.capabilities.appliedboosts.AppliedBoostsHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.util.LazyOptional;

import static com.patrigan.faction_craft.boost.BoostProviders.ATTRIBUTE;
import static net.minecraftforge.registries.ForgeRegistries.ATTRIBUTES;

public class AttributeBoost extends Boost {

    public static final Codec<AttributeModifier.Operation> ATTRIBUTE_MODIFIER_OPERATION_CODEC = Codec.INT.flatComapMap(AttributeModifier.Operation::fromValue, d -> DataResult.success(d.toValue()));

    public static final Codec<AttributeBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("attribute").forGetter(AttributeBoost::getAttributeLocation),
            Codec.DOUBLE.fieldOf("adjustment").forGetter(AttributeBoost::getAdjustment),
            Codec.DOUBLE.fieldOf("max_applications").forGetter(AttributeBoost::getMaxApplications),
            Codec.INT.optionalFieldOf("strength_adjustment", 1).forGetter(AttributeBoost::getStrengthAdjustment),
            ATTRIBUTE_MODIFIER_OPERATION_CODEC.optionalFieldOf("operation", AttributeModifier.Operation.ADDITION).forGetter(AttributeBoost::getOperation),
            Rarity.CODEC.fieldOf("rarity").forGetter(AttributeBoost::getRarity)
    ).apply(instance, AttributeBoost::new));

    private final ResourceLocation attributeLocation;
    private final Attribute attribute;
    private final double adjustment;
    private final double maxApplications;
    private final int strengthAdjustment;
    private final AttributeModifier.Operation operation;
    private final Rarity rarity;

    public AttributeBoost(ResourceLocation attributeLocation, double adjustment, double maxApplications, int strengthAdjustment, AttributeModifier.Operation operation, Rarity rarity) {
        super();
        this.attributeLocation = attributeLocation;
        this.attribute = ATTRIBUTES.getValue(attributeLocation);
        this.adjustment = adjustment;
        this.maxApplications = maxApplications;
        this.strengthAdjustment = strengthAdjustment;
        this.operation = operation;
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

    public double getMaxApplications() {
        return maxApplications;
    }

    public int getStrengthAdjustment() {
        return strengthAdjustment;
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    @Override
    public Codec<? extends Boost> getCodec() {
        return CODEC;
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
        AttributeModifier modifier = new AttributeModifier("Attribute Boost", adjustment, operation);
        livingEntity.getAttribute(attribute).addPermanentModifier(modifier);
        super.apply(livingEntity);
        return strengthAdjustment;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        LazyOptional<AppliedBoosts> lazyCap = AppliedBoostsHelper.getAppliedBoostsCapabilityLazy(livingEntity);
        if(!lazyCap.isPresent()){
            return false;
        }
        AppliedBoosts cap = lazyCap.resolve().get();
        return livingEntity.getAttributes().hasAttribute(attribute)  && cap.getAppliedBoosts().stream().filter(boost -> boost.equals(this)).count() < maxApplications;
    }
}
