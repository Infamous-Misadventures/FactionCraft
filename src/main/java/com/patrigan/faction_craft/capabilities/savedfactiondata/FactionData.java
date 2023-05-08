package com.patrigan.faction_craft.capabilities.savedfactiondata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.faction.relations.FactionRelations;
import net.minecraft.resources.ResourceLocation;

public class FactionData {

    public static final Codec<FactionData> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("faction").forGetter(FactionData::getFaction),
                    FactionRelations.CODEC.fieldOf("relations").forGetter(FactionData::getFactionRelations)
            ).apply(builder, FactionData::new));

    private final ResourceLocation faction;
    private final FactionRelations factionRelations;

    public FactionData(ResourceLocation faction, FactionRelations factionRelations) {
        this.faction = faction;
        this.factionRelations = factionRelations;
    }

    public ResourceLocation getFaction() {
        return faction;
    }

    public FactionRelations getFactionRelations() {
        return factionRelations;
    }
}
