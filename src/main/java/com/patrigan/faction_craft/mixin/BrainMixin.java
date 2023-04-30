package com.patrigan.faction_craft.mixin;

import com.mojang.datafixers.util.Pair;
import com.patrigan.faction_craft.entity.ai.brain.ModActivities;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(Brain.class)
public class BrainMixin {

    @Shadow
    @Final
    private Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> activityRequirements;

    @ModifyVariable(method = "setActiveActivityToFirstValid(Ljava/util/List;)V",
            at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    public List<Activity> factioncraft_addRaidActivities(List<Activity> pActivities) {
        List<Activity> result = new ArrayList<>(pActivities);
        List<Activity> activitiesToAdd = getActivitiesToAdd();
        int index = result.indexOf(Activity.FIGHT);
        if (index == -1) {
            activitiesToAdd.forEach(activity -> result.add(0, activity));
        } else {
            activitiesToAdd.forEach(activity -> result.add(index + 1, activity));
        }
        return result;
    }

    @NotNull
    private List<Activity> getActivitiesToAdd() {
        List<Activity> activitiesToAdd = new ArrayList<>();
        activitiesToAdd.add(ModActivities.FACTION_PATROL.get());
        activitiesToAdd.add(ModActivities.FACTION_RAIDER_PREP.get());
        activitiesToAdd.add(ModActivities.FACTION_RAIDER_VILLAGE.get());
        if(activityRequirements.keySet().contains(ModActivities.DIG.get())){
            activitiesToAdd.add(ModActivities.DIG.get());
        }
        return activitiesToAdd;
    }
}
