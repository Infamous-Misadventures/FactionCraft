package com.patrigan.faction_craft.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FactionRelations {

    public static final FactionRelations DEFAULT = new FactionRelations(new ArrayList<>(), new ArrayList<>());

    public static final Codec<FactionRelations> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.listOf().optionalFieldOf("allies", new ArrayList<>()).forGetter(FactionRelations::getAllies),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("enemies", new ArrayList<>()).forGetter(FactionRelations::getEnemies)
            ).apply(builder, FactionRelations::new));

    private final List<ResourceLocation> allies;
    private final List<ResourceLocation> enemies;

    public FactionRelations(List<ResourceLocation> allies, List<ResourceLocation> enemies) {
        this.allies = allies;
        this.enemies = enemies;
    }

    public List<ResourceLocation> getAllies() {
        return allies;
    }

    public List<ResourceLocation> getEnemies() {
        return enemies;
    }

    public boolean isEnemyOf(Faction otherFaction) {
        return enemies.contains(otherFaction.getName());
    }
}
