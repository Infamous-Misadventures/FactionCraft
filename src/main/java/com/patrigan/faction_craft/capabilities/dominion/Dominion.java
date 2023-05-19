package com.patrigan.faction_craft.capabilities.dominion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.data.CodecHelper;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.relations.FactionRelation;
import com.patrigan.faction_craft.registry.Factions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Dominion implements INBTSerializable<CompoundTag> {

    public static final Codec<Dominion> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.unboundedMap(CodecHelper.CHUNKPOS_CODEC, ChunkDominion.CODEC).fieldOf("factions").forGetter(Dominion::getChunkDominions)
            ).apply(builder, Dominion::new));

    private Map<ChunkPos, ChunkDominion> chunkDominions = new HashMap<>();

    public Dominion() {
    }

    public Dominion(Map<ChunkPos, ChunkDominion> chunkDominions) {
        this.chunkDominions = chunkDominions;
    }

    public Map<ChunkPos, ChunkDominion> getChunkDominions() {
        return chunkDominions;
    }


    public Dominion setChunkDominions(Map<ChunkPos, ChunkDominion> chunkDominions) {
        this.chunkDominions = chunkDominions;
        return this;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        CODEC.encodeStart(NbtOps.INSTANCE, this).resultOrPartial(FactionCraft.LOGGER::error).ifPresent((p_216906_) -> {
            compoundTag.put("ChunkDominions", p_216906_);
        });
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag pCompound) {
        if (pCompound.contains("ChunkDominions", 10)) {
            DataResult<Dominion> dataresult = Dominion.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, pCompound.get("ChunkDominions")));
            dataresult.resultOrPartial(FactionCraft.LOGGER::error).ifPresent(dominion -> this.setChunkDominions(dominion.getChunkDominions()));
        }
    }
}
