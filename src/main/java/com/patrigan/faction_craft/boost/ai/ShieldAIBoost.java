package com.patrigan.faction_craft.boost.ai;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.entity.ai.goal.UseShieldGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.registries.ForgeRegistries;

import static com.patrigan.faction_craft.boost.Boost.BoostType.AI;
import static com.patrigan.faction_craft.boost.Boost.Rarity.NONE;
import static com.patrigan.faction_craft.tags.EntityTags.CAN_USE_SHIELD;

public class ShieldAIBoost extends Boost {

    public static final Codec<ShieldAIBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("strength_adjustment", 1).forGetter(ShieldAIBoost::getStrengthAdjustment),
            Rarity.CODEC.optionalFieldOf("rarity", NONE).forGetter(ShieldAIBoost::getRarity)
    ).apply(instance, ShieldAIBoost::new));

    private final int strengthAdjustment;
    private final Rarity rarity;

    public ShieldAIBoost(int strengthAdjustment, Rarity rarity) {
        this.strengthAdjustment = strengthAdjustment;
        this.rarity = rarity;
    }

    public int getStrengthAdjustment() {
        return strengthAdjustment;
    }

    @Override
    public Codec<? extends Boost> getCodec() {
        return CODEC;
    }

    @Override
    public BoostType getType() {
        return AI;
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
        if (livingEntity instanceof Mob mob) {
            applyAIChanges(mob);
        }
        super.apply(livingEntity);
        return strengthAdjustment;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return FactionCraftConfig.ENABLE_EXPERIMENTAL_FEATURES.get() && livingEntity instanceof PathfinderMob && ForgeRegistries.ENTITY_TYPES.tags().getTag(CAN_USE_SHIELD).contains(livingEntity.getType());
    }

    @Override
    public void applyAIChanges(Mob mobEntity) {
        UseShieldGoal useShieldGoal = new UseShieldGoal((PathfinderMob) mobEntity, 7.5D, 60, 160, 15, 1, false);
        mobEntity.goalSelector.addGoal(0, useShieldGoal);
    }

    private static boolean requiresDamagedSelector(LivingEntity livingEntity) {
        return livingEntity != null && livingEntity.isAlive() && livingEntity.getHealth() < livingEntity.getMaxHealth();
    }

    private static boolean anySelector(LivingEntity livingEntity) {
        return livingEntity != null && livingEntity.isAlive();
    }
}
