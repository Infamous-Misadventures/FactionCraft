package com.patrigan.faction_craft;

import com.patrigan.faction_craft.platform.FCConstants;
import net.minecraftforge.fml.common.Mod;

@Mod(FCConstants.MODID)
public class FactionCraft {
    
    public FactionCraft() {
        FCConstants.LOGGER.info("Setting FC Forge up!");
        FCConstants.PLATFORM.setupFC();
    }

}