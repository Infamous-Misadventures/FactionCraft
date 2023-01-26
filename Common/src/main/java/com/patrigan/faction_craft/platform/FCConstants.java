package com.patrigan.faction_craft.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public class FCConstants {

	public static final String MODID = "faction_craft";
	public static final String MOD_NAME = "Faction Craft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
	public static final FCPlatform PLATFORM = load(FCPlatform.class);

	public static <T> T load(Class<T> clazz) {
		final T loadedService = ServiceLoader.load(clazz)
				.findFirst()
				.orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
		FCConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
		return loadedService;
	}

}