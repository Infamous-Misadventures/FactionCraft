package com.patrigan.faction_craft.platform;

import net.fabricmc.loader.api.FabricLoader;

public class FactionCraftFabric implements FCPlatform {

    @Override
    public void setupFC() {

    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) { return FabricLoader.getInstance().isModLoaded(modId); }

    @Override
    public boolean isDevelopmentEnvironment() { return FabricLoader.getInstance().isDevelopmentEnvironment(); }

    @Override
    public void setupRegistries() {

    }

    @Override
    public void setupEvents() {

    }
}
