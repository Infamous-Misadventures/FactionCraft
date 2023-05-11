package com.patrigan.faction_craft.capabilities.playerfactions;

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
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerFactions implements INBTSerializable<CompoundTag> {

    public static final Codec<PlayerFactions> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    PlayerFaction.CODEC.listOf().fieldOf("player_factions").forGetter(data -> new ArrayList<>(data.getPlayerFactions().values()))
            ).apply(builder, PlayerFactions::new));

    private Map<UUID, PlayerFaction> playerFactions = new HashMap<>();

    public PlayerFactions() {
    }

    public PlayerFactions(List<PlayerFaction> playerFactions) {
        playerFactions.forEach(playerFaction -> this.playerFactions.put(playerFaction.getPlayer(), playerFaction));
    }

    public Map<UUID, PlayerFaction> getPlayerFactions() {
        return playerFactions;
    }

    public PlayerFactions setPlayerFactions(Map<UUID, PlayerFaction> playerFactions) {
        this.playerFactions = playerFactions;
        return this;
    }

    public PlayerFactions setPlayerFactions(List<PlayerFaction> playerFactions) {
        playerFactions.forEach(playerFaction -> this.playerFactions.put(playerFaction.getPlayer(), playerFaction));
        return this;
    }

    public void addPlayerFaction(Player player, Faction faction) {
        this.playerFactions.put(player.getUUID(), new PlayerFaction(player.getUUID(), faction));
    }


    public boolean hasPlayerFaction(Player player){
        return this.playerFactions.containsKey(player.getUUID());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        CODEC.encodeStart(NbtOps.INSTANCE, this).resultOrPartial(FactionCraft.LOGGER::error).ifPresent((p_216906_) -> {
            compoundTag.put("PlayerFactions", p_216906_);
        });
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag pCompound) {
        if (pCompound.contains("PlayerFactions", 10)) {
            DataResult<PlayerFactions> dataresult = PlayerFactions.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, pCompound.get("PlayerFactions")));
            dataresult.resultOrPartial(FactionCraft.LOGGER::error).ifPresent(playerFactions -> this.setPlayerFactions(playerFactions.getPlayerFactions()));
        }
    }

    public Faction getPlayerFaction(Player player) {
        return this.playerFactions.get(player.getUUID()).getFaction();
    }
}
