package com.patrigan.faction_craft.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactionRelations {

    public static final FactionRelations DEFAULT = new FactionRelations(new ArrayList<>(), new ArrayList<>());

    public static final Codec<FactionRelations> CODEC_OLD = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.listOf().optionalFieldOf("allies", new ArrayList<>()).forGetter(FactionRelations::getAllies),
                    ResourceLocation.CODEC.listOf().optionalFieldOf("enemies", new ArrayList<>()).forGetter(FactionRelations::getEnemies)
            ).apply(builder, FactionRelations::new));

    private final Map<ResourceLocation, FactionRelation> relations = new HashMap<>();

    public FactionRelations(List<ResourceLocation> allies, List<ResourceLocation> enemies) {
        allies.forEach(ally -> relations.put(ally, new FactionRelation(ally, FactionRelation.ALLY_MAX)));
        enemies.forEach(enemy -> relations.put(enemy, new FactionRelation(enemy, FactionRelation.ENEMY_MAX)));
    }

    public List<ResourceLocation> getAllies() {
        return relations.values().stream().filter(FactionRelation::isAlly).map(FactionRelation::getFaction).toList();
    }

    public List<ResourceLocation> getEnemies() {
        return relations.values().stream().filter(FactionRelation::isEnemy).map(FactionRelation::getFaction).toList();
    }

    public boolean isEnemyOf(Faction otherFaction) {
        return getFactionRelation(otherFaction).isEnemy();
    }

    public boolean isAllyOf(Faction otherFaction) {
        return getFactionRelation(otherFaction).isAlly();
    }

    public void adjustRelation(Faction otherFaction, int amount) {
        getFactionRelation(otherFaction).adjustRelation(amount);
    }

    private FactionRelation getFactionRelation(Faction otherFaction) {
        return relations.computeIfAbsent(otherFaction.getName(), faction -> new FactionRelation(faction, FactionRelation.NEUTRAL));
    }
}
