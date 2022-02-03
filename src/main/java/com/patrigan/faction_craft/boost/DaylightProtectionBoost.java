package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.entity.ai.goal.GoalHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.boost.Boost.BoostType.SPECIAL;
import static com.patrigan.faction_craft.boost.BoostProviders.DAYLIGHT_PROTECTION;

public class DaylightProtectionBoost extends Boost {

    public static final Codec<DaylightProtectionBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("headpiece").forGetter(DaylightProtectionBoost::getHeadpiece),
            Codec.INT.optionalFieldOf("strength_adjustment", 0).forGetter(DaylightProtectionBoost::getStrengthAdjustment)
    ).apply(instance, DaylightProtectionBoost::new));

    private final ItemStack headpiece;
    private final int strengthAdjustment;

    public DaylightProtectionBoost(ItemStack headpiece, int strengthAdjustment) {
        super(DAYLIGHT_PROTECTION);
        this.headpiece = headpiece;
        this.strengthAdjustment = strengthAdjustment;
    }

    public ItemStack getHeadpiece() {
        return headpiece;
    }

    public int getStrengthAdjustment() {
        return strengthAdjustment;
    }

    @Override
    public BoostType getType() {
        return SPECIAL;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.NONE;
    }

    @Override
    public int apply(LivingEntity livingEntity) {
        if (!canApply(livingEntity)) {
            return 0;
        }
        if(livingEntity instanceof MobEntity) {
            livingEntity.setItemSlot(MobEntity.getEquipmentSlotForItem(headpiece), headpiece);
            ((MobEntity) livingEntity).setDropChance(MobEntity.getEquipmentSlotForItem(headpiece), 0);
            applyAIChanges((MobEntity) livingEntity);
        }
        super.apply(livingEntity);
        return 1;
    }

    @Override
    public void applyAIChanges(MobEntity mobEntity) {
        List<Goal> toRemove = GoalHelper.getAvailableGoals(mobEntity).stream()
                .filter(prioritizedGoal -> prioritizedGoal.getGoal() instanceof FleeSunGoal || prioritizedGoal.getGoal() instanceof RestrictSunGoal)
                .map(PrioritizedGoal::getGoal)
                .collect(Collectors.toList());
        toRemove.forEach(mobEntity.goalSelector::removeGoal);
        super.applyAIChanges(mobEntity);
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return true;
    }
}
