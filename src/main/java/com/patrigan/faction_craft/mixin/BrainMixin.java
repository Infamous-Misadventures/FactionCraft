package com.patrigan.faction_craft.mixin;

import com.patrigan.faction_craft.entity.ai.brain.ModActivities;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.schedule.Activity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Brain.class)
public class BrainMixin {

    @ModifyVariable(method = "setActiveActivityToFirstValid(Ljava/util/List;)V",
            at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    public List<Activity> factioncraft_addRaidActivities(List<Activity> pActivities) {
        List<Activity> result = new ArrayList<>(pActivities);
        int index = result.indexOf(Activity.FIGHT);
        if (index == -1) {
            result.add(0, ModActivities.FACTION_PATROL.get());
            result.add(0, ModActivities.FACTION_RAIDER_PREP.get());
            result.add(0, ModActivities.FACTION_RAIDER_VILLAGE.get());
        } else {
            result.add(index + 1, ModActivities.FACTION_PATROL.get());
            result.add(index + 1, ModActivities.FACTION_RAIDER_PREP.get());
            result.add(index + 1, ModActivities.FACTION_RAIDER_VILLAGE.get());
        }
        return result;
    }
}
