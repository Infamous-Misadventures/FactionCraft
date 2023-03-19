package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class RoleBoost extends Boost {

    public static final Codec<RoleBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.listOf().fieldOf("boosts").forGetter(RoleBoost::getBoosts),
            ResourceLocation.CODEC.listOf().fieldOf("fallback_boosts").forGetter(RoleBoost::getFallbackBoosts),
            Codec.INT.optionalFieldOf("strength_adjustment", 0).forGetter(RoleBoost::getStrengthAdjustment),
            BoostType.CODEC.optionalFieldOf("boost_type", BoostType.ROLE).forGetter(RoleBoost::getType),
            Rarity.CODEC.fieldOf("rarity").forGetter(RoleBoost::getRarity)
    ).apply(instance, RoleBoost::new));

    private final List<ResourceLocation> boosts;
    private final List<ResourceLocation> fallbackBoosts;
    private final int strengthAdjustment;
    private final BoostType boostType;
    private final Rarity rarity;

    public RoleBoost(List<ResourceLocation> boosts, List<ResourceLocation> fallbackBoosts, int strengthAdjustment, BoostType boostType, Rarity rarity) {
        this.boosts = boosts;
        this.fallbackBoosts = fallbackBoosts;
        this.strengthAdjustment = strengthAdjustment;
        this.boostType = boostType;
        this.rarity = rarity;
    }

    public List<ResourceLocation> getBoosts() {
        return boosts;
    }

    public List<ResourceLocation> getFallbackBoosts() {
        return fallbackBoosts;
    }

    public int getStrengthAdjustment() {
        return strengthAdjustment;
    }

    public BoostType getBoostType() {
        return boostType;
    }

    @Override
    public Codec<? extends Boost> getCodec() {
        return CODEC;
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
        List<Boost> fallbacks = boosts.stream().map(Boosts::getBoost).filter(Objects::nonNull).toList();
        Integer boostsStrength = boosts.stream().map(Boosts::getBoost).filter(Objects::nonNull).flatMap(boost -> boost.canApply(livingEntity) ? Stream.of(boost) : fallbacks.stream()).map(boost -> boost.apply(livingEntity)).reduce(0, Integer::sum);
        super.apply(livingEntity);
        return boostsStrength + strengthAdjustment;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return livingEntity instanceof Mob;
    }
}
