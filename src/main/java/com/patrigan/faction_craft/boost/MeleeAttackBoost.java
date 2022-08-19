package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.entity.ai.goal.GoalHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import java.util.List;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.boost.Boost.BoostType.*;
import static com.patrigan.faction_craft.boost.Boost.Rarity.NONE;
import static com.patrigan.faction_craft.boost.BoostProviders.WEAR_HANDS;

public class MeleeAttackBoost extends Boost {

    public static final Codec<MeleeAttackBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("strength_adjustment", 1).forGetter(MeleeAttackBoost::getStrengthAdjustment)
    ).apply(instance, MeleeAttackBoost::new));

    private final int strengthAdjustment;

    public MeleeAttackBoost(int strengthAdjustment) {
        super();
        this.strengthAdjustment = strengthAdjustment;
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
        return NONE;
    }

    @Override
    public int apply(LivingEntity livingEntity) {
        if (!canApply(livingEntity)) {
            return 0;
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
        if(mobEntity instanceof PathfinderMob pathfinder) {
            List<Goal> meleeGoals = GoalHelper.getAvailableGoals(pathfinder).stream().map(WrappedGoal::getGoal).filter(goal -> goal instanceof MeleeAttackGoal).collect(Collectors.toList());
            if(meleeGoals.isEmpty()) {
                MeleeAttackGoal meleeGoal = new MeleeAttackGoal(pathfinder, 1.2D, false);
                mobEntity.goalSelector.addGoal(3, meleeGoal);
            }
        }
    }
}
