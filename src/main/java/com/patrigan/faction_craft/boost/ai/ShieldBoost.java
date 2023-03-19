package com.patrigan.faction_craft.boost.ai;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.entity.ai.goal.NearestFactionAllyTargetGoal;
import com.patrigan.faction_craft.entity.ai.goal.ThrowPotionGoal;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.registries.ForgeRegistries;

import static com.patrigan.faction_craft.boost.Boost.BoostType.AI;
import static com.patrigan.faction_craft.boost.Boost.Rarity.NONE;
import static com.patrigan.faction_craft.tags.EntityTags.CAN_USE_SHIELD;

public class ShieldBoost extends Boost {

    public static final Codec<ShieldBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("strength_adjustment", 1).forGetter(ShieldBoost::getStrengthAdjustment),
            Rarity.CODEC.optionalFieldOf("rarity", NONE).forGetter(ShieldBoost::getRarity)
    ).apply(instance, ShieldBoost::new));

    private final int strengthAdjustment;
    private final Rarity rarity;

    public ShieldBoost(int strengthAdjustment, Rarity rarity) {
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
        return livingEntity instanceof Mob && ForgeRegistries.ENTITY_TYPES.tags().getTag(CAN_USE_SHIELD).contains(livingEntity.getType());
    }

    @Override
    public void applyAIChanges(Mob mobEntity) {
        FactionEntity factionEntity = FactionEntityHelper.getFactionEntityCapability(mobEntity);
        ThrowPotionGoal useShieldGoal = new ThrowPotionGoal(mobEntity, 1.0D, 60, 10.0F, potion, this.isBeneficial() ? factionEntity::getNearestDamagedFactionAlly : mobEntity::getTarget);
        mobEntity.goalSelector.addGoal(2, useShieldGoal);
    }

    private static boolean requiresDamagedSelector(LivingEntity livingEntity) {
        return livingEntity != null && livingEntity.isAlive() && livingEntity.getHealth() < livingEntity.getMaxHealth();
    }

    private static boolean anySelector(LivingEntity livingEntity) {
        return livingEntity != null && livingEntity.isAlive();
    }
}
 {
}
