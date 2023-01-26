package com.patrigan.faction_craft.platform;

public interface FCPlatform {

    /**
     * Base method for all main platform classes to call
     */
    void setupFC();

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Helper method used across each modloader platform to load registries appropriately
     */
    void setupRegistries();

    /**
     * Helper method used across each modloader platform to appropriately register events
     */
    void setupEvents();
}
