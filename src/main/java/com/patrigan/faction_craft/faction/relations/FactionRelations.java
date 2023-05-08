package com.patrigan.faction_craft.faction.relations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.faction.Faction;
import net.minecraft.resources.ResourceLocation;

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

    public static final Codec<FactionRelations> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    FactionRelation.CODEC.listOf().fieldOf("retlaions").forGetter(data -> new ArrayList<>(data.getRelations().values()))
            ).apply(builder, FactionRelations::new));

    private final Map<ResourceLocation, FactionRelation> originalRelations = new HashMap<>();
    private final Map<ResourceLocation, FactionRelation> actualRelations = new HashMap<>();

    public FactionRelations(List<ResourceLocation> allies, List<ResourceLocation> enemies) {
        allies.forEach(ally -> originalRelations.put(ally, new FactionRelation(ally, FactionRelation.ALLY_MAX)));
        enemies.forEach(enemy -> originalRelations.put(enemy, new FactionRelation(enemy, FactionRelation.ENEMY_MAX)));
    }

    public FactionRelations(List<FactionRelation> factionRelations) {
        factionRelations.forEach(relation -> originalRelations.put(relation.getFaction(), new FactionRelation(relation.getFaction(), relation.getRelation())));
    }

    public Map<ResourceLocation, FactionRelation> getRelations() {
        return getRelations(true);
    }

    public Map<ResourceLocation, FactionRelation> getRelations(boolean actual) {
        return actual ? actualRelations : originalRelations;
    }

    public List<ResourceLocation> getAllies() {
        return originalRelations.values().stream().filter(FactionRelation::isAlly).map(FactionRelation::getFaction).toList();
    }

    public List<ResourceLocation> getEnemies() {
        return originalRelations.values().stream().filter(FactionRelation::isEnemy).map(FactionRelation::getFaction).toList();
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
        return actualRelations.computeIfAbsent(otherFaction.getName(), faction -> new FactionRelation(faction, FactionRelation.NEUTRAL));
    }

    public void setInitialRelation(Faction faction, int value) {
        originalRelations.putIfAbsent(faction.getName(), new FactionRelation(faction.getName(), value));
    }

    public void initiateActualRelations(Map<ResourceLocation, FactionRelation> targetRelations) {
        targetRelations.forEach((faction, relation) -> actualRelations.put(faction, new FactionRelation(faction, relation.getRelation())));
        originalRelations.forEach((faction, relation) -> actualRelations.putIfAbsent(faction, new FactionRelation(faction, relation.getRelation())));
    }
}
