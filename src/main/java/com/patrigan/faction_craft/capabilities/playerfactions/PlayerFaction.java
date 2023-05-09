package com.patrigan.faction_craft.capabilities.playerfactions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.capabilities.savedfactiondata.FactionData;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.relations.FactionRelations;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class PlayerFaction {

    public static final Codec<PlayerFaction> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.STRING.fieldOf("player").forGetter(PlayerFaction::getPlayerUUIDString),
                    Faction.CODEC.fieldOf("faction").forGetter(PlayerFaction::getFaction)
            ).apply(builder, PlayerFaction::new));

    private UUID player;
    private Faction faction;

    public PlayerFaction(String player, Faction faction) {
        this.player = UUID.fromString(player);
        this.faction = faction;
    }
    public PlayerFaction(UUID player, Faction faction) {
        this.player = player;
        this.faction = faction;
    }

    public String getPlayerUUIDString() {
        return player.toString();
    }

    public UUID getPlayer() {
        return player;
    }

    public Faction getFaction() {
        return faction;
    }
}
