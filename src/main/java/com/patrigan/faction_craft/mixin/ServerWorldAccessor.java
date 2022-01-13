package com.patrigan.faction_craft.mixin;

import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(ServerWorld.class)
public interface ServerWorldAccessor {

    @Accessor
    List<ISpecialSpawner> getCustomSpawners();

    @Mutable
    @Accessor
    void setCustomSpawners(List<ISpecialSpawner> spawners);
}
