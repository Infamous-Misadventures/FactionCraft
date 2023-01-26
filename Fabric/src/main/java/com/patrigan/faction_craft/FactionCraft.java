package com.patrigan.faction_craft;

import com.patrigan.faction_craft.platform.FCConstants;
import net.fabricmc.api.ModInitializer;

public class FactionCraft implements ModInitializer {
    
    @Override
    public void onInitialize() {
        FCConstants.LOGGER.info("Setting FC Fabric up!");
        FCConstants.PLATFORM.setupFC();
    }
}
