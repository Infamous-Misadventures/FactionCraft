package com.patrigan.faction_craft.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.patrigan.faction_craft.entity.ai.brain.ModActivities;
import com.patrigan.faction_craft.entity.ai.brain.task.raider.*;
import com.patrigan.faction_craft.entity.ai.brain.task.villager.*;
import com.patrigan.faction_craft.entity.ai.target.FactionAllyHurtTargetGoal;
import com.patrigan.faction_craft.entity.ai.target.NearestFactionEnemyTargetGoal;
import com.patrigan.faction_craft.registry.ModMemoryModuleTypes;
import com.patrigan.faction_craft.registry.ModSensorTypes;
import com.patrigan.faction_craft.util.BrainHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;
import java.util.Set;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static com.patrigan.faction_craft.registry.ModMemoryModuleTypes.RAID_WALK_TARGET;

@Mod.EventBusSubscriber(modid = MODID)
public class EntityAIEvents {

//    @SubscribeEvent
//    public static void onLivingEntityUpdate(LivingEvent.LivingUpdateEvent event){
//
//    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityJoinLevel(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof Villager){
            addVillagerTasks((Villager) event.getEntity());
        }else if(event.getEntity() instanceof Mob mob){
            if(!hasBrain(mob)){
                mob.targetSelector.addGoal(2, new NearestFactionEnemyTargetGoal(mob, 10, true, false));
                mob.targetSelector.addGoal(2, new FactionAllyHurtTargetGoal(mob, 10, true, false));
            }else {
                // Add Brain faction targets
                BrainHelper.addMemory(mob.getBrain(), ModMemoryModuleTypes.NEAREST_VISIBLE_FACTION_ENEMY.get());
                BrainHelper.addMemory(mob.getBrain(), ModMemoryModuleTypes.NEAREST_VISIBLE_FACTION_ALLY.get());
                BrainHelper.addMemory(mob.getBrain(), ModMemoryModuleTypes.NEAREST_VISIBLE_DAMAGED_FACTION_ALLY.get());
                BrainHelper.addSensor(mob.getBrain(), ModSensorTypes.FACTION_SENSOR.get());
                addRaiderTasks(mob);
            }
        }
    }

    private static boolean hasBrain(Mob mob) {
        return mob.getBrain().isActive(Activity.CORE);
    }

    public static <E extends LivingEntity> void addRaiderTasks(E mob) {
        Brain<E> brain = (Brain<E>)mob.getBrain();
        BrainHelper.addMemory(brain, RAID_WALK_TARGET.get());
        BrainHelper.addMemory(brain, ModMemoryModuleTypes.RAIDED_VILLAGE_POI.get());
        BrainHelper.addMemory(brain, ModMemoryModuleTypes.RAID.get());
        brain.addActivityWithConditions(ModActivities.FACTION_RAIDER_PREP.get(), getRaiderPackage(0.5F), Set.of(Pair.of(ModMemoryModuleTypes.RAID.get(), MemoryStatus.VALUE_PRESENT)));
        BrainHelper.addPrioritizedBehaviors(ModActivities.FACTION_RAIDER_PREP.get(), getRaiderPackage(0.5F), brain);
        brain.addActivity(ModActivities.FACTION_RAIDER_VILLAGE.get(), getVillageRaiderPackage(0.5F));
    }

    public static void addVillagerTasks(Villager villagerEntity) {
        Brain<Villager> brain = villagerEntity.getBrain();
        ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> prioritizedCoreTasks= ImmutableList.of(Pair.of(0, new BeginVillagerRaidTask()));
        BrainHelper.addPrioritizedBehaviors(Activity.CORE, prioritizedCoreTasks, brain);
        BrainHelper.addPrioritizedBehaviors(ModActivities.PRE_FACTION_RAID.get(), getPreRaidPackage(villagerEntity.getVillagerData().getProfession(), 0.5F), brain);
        BrainHelper.addPrioritizedBehaviors(ModActivities.FACTION_RAID.get(), getRaidPackage(villagerEntity.getVillagerData().getProfession(), 0.5F), brain);
    }

    private static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getPreRaidPackage(VillagerProfession pProfession, float p_220642_1_) {
        return ImmutableList.of(Pair.of(0, new RingBell()), Pair.of(0, new RunOne<>(ImmutableList.of(Pair.of(new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, p_220642_1_ * 1.5F, 2, 150, 200), 6), Pair.of(new VillageBoundRandomStroll(p_220642_1_ * 1.5F), 2)))), getMinimalLookBehavior(), Pair.of(99, new ForgetRaidTask()));
    }

    private static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getRaidPackage(VillagerProfession pProfession, float p_220640_1_) {
        return ImmutableList.of(Pair.of(0, new RunOne<>(ImmutableList.of(Pair.of(new GoOutsideAfterRaidTask(p_220640_1_), 5), Pair.of(new FindWalkTargetAfterRaidVictoryTask(p_220640_1_ * 1.1F), 2)))), Pair.of(0, new CelebrateRaidVictoryTask(600, 600)), Pair.of(2, new FindHidingPlaceDuringRaidTask(24, p_220640_1_ * 1.4F)), getMinimalLookBehavior(), Pair.of(99, new ForgetRaidTask()));
    }

    private static  <E extends LivingEntity>  ImmutableList<Pair<Integer, ? extends Behavior<? super E>>> getRaiderPackage(float p_220640_1_) {
        return ImmutableList.of(Pair.of(0, new AcquireRaidTargetPosition<>(RAID_WALK_TARGET.get())), Pair.of(1, new BeginRaiderRaidVillageTask()), Pair.of(2, new RaiderSetWalkTargetFromBlockMemory<>(RAID_WALK_TARGET.get(), p_220640_1_ * 1.5F, 2, 150, 200)));
    }

    private static <E extends LivingEntity>  ImmutableList<Pair<Integer, ? extends Behavior<? super E>>> getVillageRaiderPackage(float p_220640_1_) {
        return ImmutableList.of(Pair.of(0, new AcquireVillageRaidTarget<>(RAID_WALK_TARGET.get(), 1)), Pair.of(1, new RaiderSetWalkTargetFromBlockMemory<>(RAID_WALK_TARGET.get(), p_220640_1_ * 1.5F, 2, 150, 200)));
    }

    private static Pair<Integer, Behavior<LivingEntity>> getMinimalLookBehavior() {
        return Pair.of(5, new RunOne<>(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.VILLAGER, 8.0F), 2), Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), 2), Pair.of(new DoNothing(30, 60), 8))));
    }
}
