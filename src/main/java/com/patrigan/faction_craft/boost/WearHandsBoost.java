package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
        super();
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
        if(boostType.equals(OFFHAND)){
            livingEntity.setItemSlot(EquipmentSlot.OFFHAND, item);
        }else {
            livingEntity.setItemSlot(EquipmentSlot.MAINHAND, item);
        }
        if(livingEntity instanceof Mob mob) {
            applyAIChanges(mob);
        }
        super.apply(livingEntity);
        return strengthAdjustment;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return livingEntity instanceof Mob;
    }

    @Override
    public void applyAIChanges(Mob mobEntity) {
        ItemStack itemstack = mobEntity.getItemInHand(ProjectileUtil.getWeaponHoldingHand(mobEntity, item -> item instanceof net.minecraft.world.item.BowItem));
        if (itemstack.is(Items.BOW) && mobEntity instanceof RangedAttackMob) {
//                RangedBowAttackGoal<RangedAttackMob> bowGoal = new RangedBowAttackGoal<>(mobEntity, 1.0D, 20, 15.0F);
//                int i = 20;
//                if (mobEntity.level.getDifficulty() != Difficulty.HARD) {
//                    i = 40;
//                }
//
//                mobEntity.bowGoal.setMinAttackInterval(i);
//                mobEntity.goalSelector.addGoal(4, mobEntity.bowGoal);
        } else {
            if(mobEntity instanceof PathfinderMob pathfinder) {
                List<Goal> meleeGoals = pathfinder.goalSelector.getAvailableGoals().stream().map(WrappedGoal::getGoal).filter(goal -> goal instanceof MeleeAttackGoal).collect(Collectors.toList());
                if(meleeGoals.isEmpty()) {
                    MeleeAttackGoal meleeGoal = new MeleeAttackGoal(pathfinder, 1.2D, false);
                    mobEntity.goalSelector.addGoal(4, meleeGoal);
                }
            }
        }

    }
}
