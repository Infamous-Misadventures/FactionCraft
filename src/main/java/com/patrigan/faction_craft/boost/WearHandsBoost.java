package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.entity.ai.goal.GoalHelper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;
import java.util.stream.Collectors;

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
        if(livingEntity instanceof MobEntity) {
            applyAIChanges((MobEntity) livingEntity);
        }
        super.apply(livingEntity);
        return strengthAdjustment;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return livingEntity instanceof MobEntity;
    }

    @Override
    public void applyAIChanges(MobEntity mobEntity) {
        ItemStack itemstack = mobEntity.getItemInHand(ProjectileHelper.getWeaponHoldingHand(mobEntity, item -> item instanceof net.minecraft.item.BowItem));
        if (itemstack.getItem() == Items.BOW) {
//                RangedBowAttackGoal<RangedAttackMob> bowGoal = new RangedBowAttackGoal<>(mobEntity, 1.0D, 20, 15.0F);
//                int i = 20;
//                if (mobEntity.level.getDifficulty() != Difficulty.HARD) {
//                    i = 40;
//                }
//
//                mobEntity.bowGoal.setMinAttackInterval(i);
//                mobEntity.goalSelector.addGoal(4, mobEntity.bowGoal);
        } else {
            if(mobEntity instanceof CreatureEntity) {
                CreatureEntity pathfinder = (CreatureEntity) mobEntity;
                List<Goal> meleeGoals = GoalHelper.getAvailableGoals(pathfinder).stream().map(PrioritizedGoal::getGoal).filter(goal -> goal instanceof MeleeAttackGoal).collect(Collectors.toList());
                if(meleeGoals.isEmpty()) {
                    MeleeAttackGoal meleeGoal = new MeleeAttackGoal(pathfinder, 1.2D, false);
                    mobEntity.goalSelector.addGoal(4, meleeGoal);
                }
            }
        }

    }
}
