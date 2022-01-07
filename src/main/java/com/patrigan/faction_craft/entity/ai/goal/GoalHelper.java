package com.patrigan.faction_craft.entity.ai.goal;

import com.patrigan.faction_craft.mixin.GoalSelectorAccessor;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;

import java.util.Set;

public class GoalHelper {
    public static Set<PrioritizedGoal> getAvailableGoals(MobEntity mobEntity){
        return castToAccessor(mobEntity.goalSelector).getAvailableGoals();
    }
    public static GoalSelectorAccessor castToAccessor(GoalSelector goalSelector) {
        //noinspection unchecked
        return (GoalSelectorAccessor) goalSelector;
    }
}
