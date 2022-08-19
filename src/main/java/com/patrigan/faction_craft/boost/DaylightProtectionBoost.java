package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.entity.ai.goal.GoalHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.item.ItemStack;

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
        super();
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
    public Codec<? extends Boost> getCodec() {
        return CODEC;
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
        if(livingEntity instanceof Mob) {
            livingEntity.setItemSlot(Mob.getEquipmentSlotForItem(headpiece), headpiece);
            ((Mob) livingEntity).setDropChance(Mob.getEquipmentSlotForItem(headpiece), 0);
             applyAIChanges((Mob) livingEntity);
        }
        super.apply(livingEntity);
        return 1;
    }

    @Override
    public void applyAIChanges(Mob mobEntity) {
        List<Goal> toRemove = GoalHelper.getAvailableGoals(mobEntity).stream()
                .filter(prioritizedGoal -> prioritizedGoal.getGoal() instanceof FleeSunGoal || prioritizedGoal.getGoal() instanceof RestrictSunGoal)
                .map(WrappedGoal::getGoal)
                .collect(Collectors.toList());
        toRemove.forEach(mobEntity.goalSelector::removeGoal);
        super.applyAIChanges(mobEntity);
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return true;
    }
}
