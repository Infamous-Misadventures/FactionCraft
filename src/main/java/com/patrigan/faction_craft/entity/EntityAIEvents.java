package com.patrigan.faction_craft.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.patrigan.faction_craft.entity.ai.brain.ModActivities;
import com.patrigan.faction_craft.entity.ai.brain.task.BeginRaidTask;
import com.patrigan.faction_craft.entity.ai.brain.task.CelebrateRaidVictoryTask;
import com.patrigan.faction_craft.entity.ai.brain.task.FindHidingPlaceDuringRaidTask;
import com.patrigan.faction_craft.entity.ai.brain.task.FindWalkTargetAfterRaidVictoryTask;
import com.patrigan.faction_craft.entity.ai.brain.task.ForgetRaidTask;
import com.patrigan.faction_craft.entity.ai.brain.task.GoOutsideAfterRaidTask;
import com.patrigan.faction_craft.util.BrainHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class EntityAIEvents {

//    @SubscribeEvent
//    public static void onLivingEntityUpdate(LivingEvent.LivingUpdateEvent event){
//
//    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event){
        if(event.getEntity() instanceof VillagerEntity){
            addVillagerTasks((VillagerEntity) event.getEntity());
        }
    }

    public static void addVillagerTasks(VillagerEntity villagerEntity) {
        Brain<VillagerEntity> brain = villagerEntity.getBrain();
        ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> prioritizedCoreTasks= ImmutableList.of(Pair.of(0, new BeginRaidTask()));
        BrainHelper.addPrioritizedBehaviors(Activity.CORE, prioritizedCoreTasks, brain);
        BrainHelper.addPrioritizedBehaviors(ModActivities.PRE_FACTION_RAID.get(), getPreRaidPackage(villagerEntity.getVillagerData().getProfession(), 0.5F), brain);
        BrainHelper.addPrioritizedBehaviors(ModActivities.FACTION_RAID.get(), getRaidPackage(villagerEntity.getVillagerData().getProfession(), 0.5F), brain);
    }

    private static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getPreRaidPackage(VillagerProfession pProfession, float p_220642_1_) {
        return ImmutableList.of(Pair.of(0, new RingBellTask()), Pair.of(0, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new StayNearPointTask(MemoryModuleType.MEETING_POINT, p_220642_1_ * 1.5F, 2, 150, 200), 6), Pair.of(new FindWalkTargetTask(p_220642_1_ * 1.5F), 2)))), getMinimalLookBehavior(), Pair.of(99, new ForgetRaidTask()));
    }

    private static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getRaidPackage(VillagerProfession pProfession, float p_220640_1_) {
        return ImmutableList.of(Pair.of(0, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new GoOutsideAfterRaidTask(p_220640_1_), 5), Pair.of(new FindWalkTargetAfterRaidVictoryTask(p_220640_1_ * 1.1F), 2)))), Pair.of(0, new CelebrateRaidVictoryTask(600, 600)), Pair.of(2, new FindHidingPlaceDuringRaidTask(24, p_220640_1_ * 1.4F)), getMinimalLookBehavior(), Pair.of(99, new ForgetRaidTask()));
    }

    private static Pair<Integer, Task<LivingEntity>> getMinimalLookBehavior() {
        return Pair.of(5, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.VILLAGER, 8.0F), 2), Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new DummyTask(30, 60), 8))));
    }
}
