package com.patrigan.faction_craft.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ServerLevel.class)
public interface ServerLevelAccessor {

    @Accessor
    List<CustomSpawner> getCustomSpawners();

    @Mutable
    @Accessor
    void setCustomSpawners(List<CustomSpawner> spawners);
}
