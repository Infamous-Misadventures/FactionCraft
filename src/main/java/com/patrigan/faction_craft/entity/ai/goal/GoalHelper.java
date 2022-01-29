package com.patrigan.faction_craft.entity.ai.goal;

import com.patrigan.faction_craft.mixin.GoalSelectorAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import java.util.Set;

public class GoalHelper {
    public static Set<WrappedGoal> getAvailableGoals(Mob mobEntity){
        return castToAccessor(mobEntity.goalSelector).getAvailableGoals();
    }
    public static GoalSelectorAccessor castToAccessor(GoalSelector goalSelector) {
        //noinspection unchecked
        return (GoalSelectorAccessor) goalSelector;
    }
}
