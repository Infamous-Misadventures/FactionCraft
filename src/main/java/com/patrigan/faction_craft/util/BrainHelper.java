package com.patrigan.faction_craft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.patrigan.faction_craft.entity.ai.brain.sensor.FactionSpecificSensor;
import com.patrigan.faction_craft.mixin.BrainAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.ai.behavior.Behavior;

import java.util.Collection;
import java.util.Optional;

public class BrainHelper {

    /*
    public static <E extends LivingEntity> void addActivityAndRemoveMemoryWhenStopped(Brain<E> brain, Activity activity, int priorityStart, ImmutableList<? extends Task<? super E>> tasks, MemoryModuleType<?> memoryToRemove) {
        Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>> memoryToStatusSet = ImmutableSet.of(Pair.of(memoryToRemove, MemoryModuleStatus.VALUE_PRESENT));
        Set<MemoryModuleType<?>> memorySet = ImmutableSet.of(memoryToRemove);
        addActivityAndRemoveMemoriesWhenStopped(brain, activity, createPriorityPairs(priorityStart, tasks), memoryToStatusSet, memorySet);
    }
    */

    public static <E extends LivingEntity> ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> createPriorityPairs(int priorityStart, ImmutableList<? extends Behavior<? super E>> tasks) {
        int priorityIndex = priorityStart;
        ImmutableList.Builder<Pair<Integer, ? extends Behavior<? super E>>> priorityPairs = ImmutableList.builder();

        for(Behavior<? super E> task : tasks) {
            priorityPairs.add(Pair.of(priorityIndex++, task));
        }

        return priorityPairs.build();
    }

    /*
    private static <E extends LivingEntity> void addActivityAndRemoveMemoriesWhenStopped(Brain<E> brain, Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> prioritizedTasks, Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>> memoryToStatusSet, Set<MemoryModuleType<?>> memorySet){
        BrainAccessor<E> brainAccessor = castToAccessor(brain);
        brainAccessor.getActivityRequirements().put(activity, memoryToStatusSet);
        if (!memorySet.isEmpty()) {
            brainAccessor.getActivityMemoriesToEraseWhenStopped().put(activity, memorySet);
        }

        addPrioritizedBehaviors(activity, prioritizedTasks, brainAccessor);
    }

    public static <E extends LivingEntity> void addPrioritizedBehaviors(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> prioritizedTasks, BrainAccessor<E> brainAccessor) {
        for(Pair<Integer, ? extends Task<? super E>> pair : prioritizedTasks) {
            brainAccessor.getAvailableBehaviorsByPriority()
                    .computeIfAbsent(pair.getFirst(), (p) -> Maps.newHashMap())
                    .computeIfAbsent(activity, (a) -> Sets.newLinkedHashSet())
                    .add(pair.getSecond());
        }
    }
     */

    public static <E extends LivingEntity> void addPrioritizedBehaviors(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> prioritizedTasks, Brain<E> brain) {
        BrainAccessor<E> brainAccessor = castToAccessor(brain);

        for(Pair<Integer, ? extends Behavior<? super E>> pair : prioritizedTasks) {
            brainAccessor.getAvailableBehaviorsByPriority()
                    .computeIfAbsent(pair.getFirst(), (p) -> Maps.newHashMap())
                    .computeIfAbsent(activity, (a) -> Sets.newLinkedHashSet())
                    .add(pair.getSecond());
        }
    }

    public static <E extends LivingEntity> void addMemory(Brain<E> brain, MemoryModuleType<?> memoryModuleType) {
        if(brain.getMemories().containsKey(memoryModuleType)) {
            return;
        }
        brain.getMemories().put(memoryModuleType, Optional.empty().map(ExpirableValue::of));
    }

    public static void addSensor(Brain<?> brain, SensorType<FactionSpecificSensor> factionSpecificSensorSensorType) {
        BrainAccessor<?> brainAccessor = castToAccessor(brain);
        brainAccessor.getSensors().put(factionSpecificSensorSensorType, factionSpecificSensorSensorType.create());
    }

    public static <E extends LivingEntity> BrainAccessor<E> castToAccessor(Brain<E> brain) {
        //noinspection unchecked
        return (BrainAccessor<E>)brain;
    }

    public static <E extends LivingEntity> Behavior<? super E> getAttackTask(Brain<E> brain) {
        BrainAccessor<E> brainAccessor = castToAccessor(brain);
        Optional<Behavior<? super E>> first = brainAccessor.getAvailableBehaviorsByPriority().values().stream()
                .flatMap(activitySetMap -> activitySetMap.values().stream())
                .flatMap(Collection::stream)
                .filter(behavior -> behavior instanceof StartAttacking)
                .findFirst();
        return first.orElse(null);
    }

    public static boolean hasBrain(Mob mob) {
        return mob.getBrain().isActive(Activity.CORE);
    }
}