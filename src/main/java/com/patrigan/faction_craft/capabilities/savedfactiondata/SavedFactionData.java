package com.patrigan.faction_craft.capabilities.savedfactiondata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.relations.FactionRelation;
import com.patrigan.faction_craft.registry.Factions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SavedFactionData implements INBTSerializable<CompoundTag> {

    public static final Codec<SavedFactionData> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    FactionData.CODEC.listOf().fieldOf("factions").forGetter(data -> new ArrayList<>(data.getCurrentFactionData().values()))
            ).apply(builder, SavedFactionData::new));

    private Map<ResourceLocation, FactionData> factions = new HashMap<>();

    public SavedFactionData() {
    }

    public SavedFactionData(List<FactionData> factionDataList) {
        factionDataList.forEach(factionData -> this.factions.put(factionData.getFaction(), factionData));
    }

    public Map<ResourceLocation, FactionData> getFactionData() {
        return factions;
    }

    public Map<ResourceLocation, FactionData> getCurrentFactionData(){
        return Factions.FACTION_DATA.getData().values().stream()
                .collect(Collectors.toMap(Faction::getName, Faction::toFactionData));
    }

    public SavedFactionData setFactions(Map<ResourceLocation, FactionData> factions) {
        this.factions = factions;
        return this;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        CODEC.encodeStart(NbtOps.INSTANCE, this).resultOrPartial(FactionCraft.LOGGER::error).ifPresent((p_216906_) -> {
            compoundTag.put("FactionDataList", p_216906_);
        });
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag pCompound) {
        if (pCompound.contains("FactionDataList", 10)) {
            DataResult<SavedFactionData> dataresult = SavedFactionData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, pCompound.get("FactionDataList")));
            dataresult.resultOrPartial(FactionCraft.LOGGER::error).ifPresent(savedFactionData -> this.setFactions(savedFactionData.getFactionData()));
        }
    }

    public Map<ResourceLocation, FactionRelation> getOriginalRelations(Faction faction) {
        FactionData factionData = this.factions.get(faction.getName());
        if (factionData != null) {
            return factionData.getFactionRelations().getRelations(false);
        }
        return new HashMap<>();
    }
}
