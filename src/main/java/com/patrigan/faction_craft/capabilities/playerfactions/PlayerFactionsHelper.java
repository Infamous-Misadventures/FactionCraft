package com.patrigan.faction_craft.capabilities.playerfactions;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.PLAYER_FACTIONS_CAPABILITY;
import static com.patrigan.faction_craft.capabilities.ModCapabilities.SAVED_FACTION_DATA_CAPABILITY;


public class PlayerFactionsHelper {

    public static PlayerFactions getCapability(Level level)
    {
        return level.getCapability(PLAYER_FACTIONS_CAPABILITY).orElse(new PlayerFactions());
    }

    public static PlayerFactions getPlayerFactions() {
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        if(currentServer == null) return new PlayerFactions();
        return getCapability(currentServer.overworld());
    }

    public static PlayerFactions getPlayerFactions(ServerLevel level) {
        return getCapability(level);
    }
}
