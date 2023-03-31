package com.patrigan.faction_craft.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class FactionCraftConfig {
    public static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_EXPERIMENTAL_FEATURES;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> DISABLED_FACTIONS;
    public static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_DEFAULT_FACTION;

    public static ForgeConfigSpec.ConfigValue<Boolean> DISABLE_FACTION_RAIDS;
    public static ForgeConfigSpec.ConfigValue<Integer> RAID_MAX_FACTIONS;
    public static ForgeConfigSpec.ConfigValue<Integer> NUMBER_WAVES_EASY;
    public static ForgeConfigSpec.ConfigValue<Integer> NUMBER_WAVES_NORMAL;
    public static ForgeConfigSpec.ConfigValue<Integer> NUMBER_WAVES_HARD;
    public static ForgeConfigSpec.ConfigValue<Integer> MAX_NUMBER_WAVES;
    public static ForgeConfigSpec.ConfigValue<Double> BASE_WAVE_MULTIPLIER;
    public static ForgeConfigSpec.ConfigValue<Double> MULTIPLIER_INCREASE_PER_WAVE;
    public static ForgeConfigSpec.ConfigValue<Double> MULTIPLIER_INCREASE_PER_BAD_OMEN;
    public static ForgeConfigSpec.ConfigValue<Double> WAVE_TARGET_STRENGTH_SPREAD;
    public static ForgeConfigSpec.ConfigValue<Double> TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_EASY;
    public static ForgeConfigSpec.ConfigValue<Double> TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_NORMAL;
    public static ForgeConfigSpec.ConfigValue<Double> TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_HARD;

    public static ForgeConfigSpec.ConfigValue<Double> VILLAGE_RAID_TARGET_STRENGTH_MULTIPLIER;
    public static ForgeConfigSpec.ConfigValue<Double> VILLAGE_RAID_ADDITIONAL_WAVE_CHANCE;
    public static ForgeConfigSpec.ConfigValue<Integer> VILLAGE_RAID_VILLAGER_WEIGHT;
    public static ForgeConfigSpec.ConfigValue<Integer> VILLAGE_RAID_IRON_GOLEM_WEIGHT;
    public static ForgeConfigSpec.ConfigValue<Integer> VILLAGE_RAID_GUARD_VILLAGER_WEIGHT;

    public static ForgeConfigSpec.ConfigValue<Integer> PLAYER_RAID_TARGET_BASE_STRENGTH;
    public static ForgeConfigSpec.ConfigValue<Double> PLAYER_RAID_TARGET_STRENGTH_MULTIPLIER;

    public static ForgeConfigSpec.ConfigValue<Integer> FACTION_BATTLE_RAID_TARGET_BASE_STRENGTH;
    public static ForgeConfigSpec.ConfigValue<Double> FACTION_BATTLE_RAID_TARGET_STRENGTH_MULTIPLIER;

    public static ForgeConfigSpec.ConfigValue<Boolean> DISABLE_FACTION_PATROLS;
    public static ForgeConfigSpec.ConfigValue<Boolean> DISABLE_VANILLA_PATROLS;
    public static ForgeConfigSpec.ConfigValue<Long> PATROL_DAYTIME_BEFORE_SPAWNING;
    public static ForgeConfigSpec.ConfigValue<Integer> PATROL_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS;
    public static ForgeConfigSpec.ConfigValue<Integer> PATROL_VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS;
    public static ForgeConfigSpec.ConfigValue<Double> PATROL_SPAWN_CHANCE_ON_SPAWN_ATTEMPT;

    public static ForgeConfigSpec.ConfigValue<Boolean> DISABLE_FACTION_BATTLES;
    public static ForgeConfigSpec.ConfigValue<Long> BATTLE_DAYTIME_BEFORE_SPAWNING;
    public static ForgeConfigSpec.ConfigValue<Integer> BATTLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS;
    public static ForgeConfigSpec.ConfigValue<Integer> BATTLE_VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS;
    public static ForgeConfigSpec.ConfigValue<Double> BATTLE_SPAWN_CHANCE_ON_SPAWN_ATTEMPT;
    public static ForgeConfigSpec.ConfigValue<Integer> BATTLE_STARTING_WAVE_MIN;
    public static ForgeConfigSpec.ConfigValue<Integer> BATTLE_STARTING_WAVE_MAX;

    public static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_RECONSTRUCT_BLOCKS;
    public static ForgeConfigSpec.ConfigValue<Boolean> RECONSTRUCT_ON_LOSS;
    public static ForgeConfigSpec.ConfigValue<Integer> RECONSTRUCT_TICK_DELAY;
    public static ForgeConfigSpec.ConfigValue<Integer> RECONSTRUCT_VARIABLE_TICK_DELAY;

    public static ForgeConfigSpec.ConfigValue<Boolean> ENABLE_DIGGER_AI;


    public static class Common {

        public Common(ForgeConfigSpec.Builder builder){
            modConfig(builder);
            factionConfig(builder);
            standardRaidConfig(builder);
            raidTargetConfig(builder);
            patrollerConfig(builder);
            factionBattleConfig(builder);
            reconstructBlockConfig(builder);
            AIConfig(builder);
        }

        private void modConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Faction Craft Mod Config").push("mod_config");
            ENABLE_EXPERIMENTAL_FEATURES = builder
                    .comment("Enables experimental features \n" +
                            "Default false")
                    .define("enableExperimentalFeatures", false);
            builder.pop();
        }

        private void factionConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Factions").push("factions");
            DISABLED_FACTIONS = builder
                    .comment("A list of disabled factions. \n" +
                            "Default: The internal skeleton test faction. ")
                    .defineList("disabledFactions", Arrays.asList(MODID+":skeleton_test", MODID+":slime_test"), o -> o instanceof String);
            ENABLE_DEFAULT_FACTION = builder
                    .comment("Enables default faction for entities \n" +
                            "Default false")
                    .define("enableDefaultFaction", false);
            builder.pop();
        }

        private void standardRaidConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Standard Raid Calculations").push("standard_raid_calculations");
            DISABLE_FACTION_RAIDS = builder
                    .comment("Disables faction raids \n" +
                            "Default false")
                    .define("disableFactionRaids", false);
            RAID_MAX_FACTIONS = builder
                    .comment("The max number of factions that can participate. \n" +
                            "Roughly corresponds to the max bad omen level in vanilla. Default 5")
                    .defineInRange("numberWavesEasy", 5, 0, 100);
            NUMBER_WAVES_EASY = builder
                    .comment("The number of waves for Easy difficulty. \n" +
                            "Default 2")
                    .defineInRange("numberWavesEasy", 2, 1, 9999);
            NUMBER_WAVES_NORMAL = builder
                    .comment("The number of waves for Normal difficulty. \n" +
                            "Default 4")
                    .defineInRange("numberWavesNormal", 4, 1, 9999);
            NUMBER_WAVES_HARD = builder
                    .comment("The number of waves for Hard difficulty. \n" +
                            "Default 6")
                    .defineInRange("numberWavesHard", 6, 1, 9999);
            MAX_NUMBER_WAVES = builder
                    .comment("The max number of waves. \n" +
                            "Default 10")
                    .defineInRange("maxNumberWaves", 10, 1, 9999);
            BASE_WAVE_MULTIPLIER = builder
                    .comment("The multiplier for the target strength for the first wave. \n" +
                            "1.0 disables Starting wave multiplier. Default 0.65")
                    .defineInRange("baseWaveMultiplier", 0.65, -100.0, 100.0);
            MULTIPLIER_INCREASE_PER_WAVE = builder
                    .comment("The amount the multiplier for the target strength increases per wave. \n" +
                            "0.0 removes target growth per wave. Default 0.15")
                    .defineInRange("multiplierIncreasePerWave", 0.15, -100.0, 100.0);
            MULTIPLIER_INCREASE_PER_BAD_OMEN = builder
                    .comment("The multiplier per bad omen level for the target strength. \n" +
                            "0.0 removes increase Bad Omen impact. Default 0.1")
                    .defineInRange("multiplierIncreasePerBadOmen", 0.1, -100.0, 100.0);
            WAVE_TARGET_STRENGTH_SPREAD = builder
                    .comment("The amount the the target strength can fluctuate per wave. \n" +
                            "Default 0.1")
                    .defineInRange("waveTargetStrengthSpread", 0.1, -100.0, 100.0);
            TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_EASY = builder
                    .comment("The multiplier applied to target strength for Easy difficulty. \n" +
                            "0.0 disables difficulty based multipliers. Default -0.1")
                    .defineInRange("targetStrengthDifficultyMultiplierEasy", -0.1, -100.0, 100.0);
            TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_NORMAL = builder
                    .comment("The multiplier applied to target strength for Normal difficulty. \n" +
                            "0.0 disables difficulty based multipliers. Default 0.0")
                    .defineInRange("targetStrengthDifficultyMultiplierNormal", 0.0, -100.0, 100.0);
            TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_HARD = builder
                    .comment("The multiplier applied to target strength for Hard difficulty. \n" +
                            "0.0 disables difficulty based multipliers. Default 1.1")
                    .defineInRange("targetStrengthDifficultyMultiplierHard", 0.1, -100.0, 100.0);

            builder.pop();
        }

        private void raidTargetConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Village Raid Target Calculations").push("village_raid_target_calculations");
            VILLAGE_RAID_TARGET_STRENGTH_MULTIPLIER = builder
                    .comment("Applied to the target strength of the village \n" +
                            "Multiplies the target strength and rounds down. \n" +
                            "Vanilla villages have a target strength between 10 and 20. Default 1.0F")
                    .defineInRange("villageRaidTargetStrengthMultiplier", 1.0, 0.0, 100.0);
            VILLAGE_RAID_ADDITIONAL_WAVE_CHANCE = builder
                    .comment("Applied to the target strength of the village \n" +
                            "Additional waves is calulcated by applying this value to target strength. \n" +
                            "Vanilla villages have a target strength between 50 and 100. Default 0.01F")
                    .defineInRange("villageRaidAdditionalWaveChance", 0.01, 0.0, 10);
            VILLAGE_RAID_VILLAGER_WEIGHT = builder
                    .comment("The amount that 1 villager adds to the strength of a village. \n" +
                            "Default 5")
                    .defineInRange("villageRaidVillagerWeight", 5, 0, 9999);
            VILLAGE_RAID_IRON_GOLEM_WEIGHT = builder
                    .comment("The amount that 1 Iron Golem adds to the strength of a village. \n" +
                            "Default 15")
                    .defineInRange("villageRaidIronGolemWeight", 15, 0, 9999);
            VILLAGE_RAID_GUARD_VILLAGER_WEIGHT = builder
                    .comment("The amount that 1 Guard Villager adds to the strength of a village. \n" +
                            "Default 7")
                    .defineInRange("villageRaidGuardVillagerWeight", 7, 0, 9999);
            builder.pop();

            builder.comment("Player Raid Target Calculations").push("player_raid_target_calculations");
            PLAYER_RAID_TARGET_BASE_STRENGTH = builder
                    .comment("Base target strength of the player \n" +
                            "Default 70")
                    .defineInRange("playerRaidTargetBaseStrength", 70, 0, 9999);
            PLAYER_RAID_TARGET_STRENGTH_MULTIPLIER = builder
                    .comment("Applied to the target strength of the player \n" +
                            "Multiplies the target strength and rounds down. Default 1.0F")
                    .defineInRange("playerRaidTargetStrengthMultiplier", 1.0, 0.0, 100.0);
            builder.pop();

            builder.comment("Faction Battle Raid Target Calculations").push("faction_battle_raid_target_calculations");
            FACTION_BATTLE_RAID_TARGET_BASE_STRENGTH = builder
                    .comment("Base target strength of a faction battle. \n" +
                            "Will be divided over both factions. Default 140")
                    .defineInRange("factionBattleRaidTargetBaseStrength", 140, 0, 9999);
            FACTION_BATTLE_RAID_TARGET_STRENGTH_MULTIPLIER = builder
                    .comment("Applied to the target strength of a faction battle. \n" +
                            "Multiplies the target strength and rounds down. Default 1.0F")
                    .defineInRange("factionBattleRaidTargetStrengthMultiplier", 1.0, 0.0, 100.0);
            builder.pop();
        }

        private void patrollerConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Patroller Config").push("patroller_config");
            DISABLE_FACTION_PATROLS = builder
                    .comment("Disables faction patrols \n" +
                            "Default false")
                    .define("disableFactionPatrols", false);
            DISABLE_VANILLA_PATROLS = builder
                    .comment("Disables vanilla patrols (and such, vanilla raids) \n" +
                            "Default true")
                    .define("disableVanillaPatrols", true);
            PATROL_DAYTIME_BEFORE_SPAWNING = builder
                    .comment("ingame daytime before patrols start spawning. \n" +
                            "Vanilla is 24000L equivalent of 5days. Default 24000L")
                    .defineInRange("patrolDaytimeBeforeSpawning", 24000L, 0L, 9999999999L);
            PATROL_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS = builder
                    .comment("This value plus the variable version together make up the time between two spawn attempts. \n" +
                            "Vanilla default 12000, Default 6000")
                    .defineInRange("patrolTickDelayBetweenSpawnAttempts", 9000, 0, 999999999);
            PATROL_VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS = builder
                    .comment("A random value between 0 and this value is added to the static delay to determine total delay. \n" +
                            "Vanilla default is 1200. Default 1200")
                    .defineInRange("patrolVariableTickDelayBetweenSpawnAttempts", 1200, 0, 999999999);
            PATROL_SPAWN_CHANCE_ON_SPAWN_ATTEMPT = builder
                    .comment("The chance a patrol spawns on a spawn attempt. \n" +
                            "Vanilla default is 0.2 Default 0.3")
                    .defineInRange("patrolSpawnChanceOnSpawnAttempt", 0.3, 0.0, 1.0);
            builder.pop();
        }

        private void factionBattleConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Faction Battle Config").push("faction_battle_config");
            DISABLE_FACTION_BATTLES = builder
                    .comment("Disables faction battles \n" +
                            "Default false")
                    .define("disableFactionBattles", false);
            BATTLE_DAYTIME_BEFORE_SPAWNING = builder
                    .comment("ingame daytime before battles start spawning. \n" +
                            "Vanilla is 4800L equivalent of 1days. Default 48000L")
                    .defineInRange("battleDaytimeBeforeSpawning", 48000L, 0L, 9999999999L);
            BATTLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS = builder
                    .comment("This value plus the variable version together make up the time between two spawn attempts. \n" +
                            "Default 18000")
                    .defineInRange("battleTickDelayBetweenSpawnAttempts", 18000, 0, 999999999);
            BATTLE_VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS = builder
                    .comment("A random value between 0 and this value is added to the static delay to determine total delay. \n" +
                            "Default 2400")
                    .defineInRange("battleVariableTickDelayBetweenSpawnAttempts", 2400, 0, 999999999);
            BATTLE_SPAWN_CHANCE_ON_SPAWN_ATTEMPT = builder
                    .comment("The chance a battle spawns on a spawn attempt. \n" +
                            "Default 0.15")
                    .defineInRange("battleSpawnChanceOnSpawnAttempt", 0.15, 0.0, 1.0);
            BATTLE_STARTING_WAVE_MIN = builder
                    .comment("Determines the minimum starting wave for a battle. \n" +
                            "Default 1")
                    .defineInRange("battleStartingWaveMin", 1, 0, 999999999);
            BATTLE_STARTING_WAVE_MAX = builder
                    .comment("Determines the maximum starting wave for a battle. \n" +
                            "Default 5")
                    .defineInRange("battleStartingWaveMax", 5, 0, 999999999);
            builder.pop();
        }

        private void reconstructBlockConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Reconstruct Block Config").push("reconstruct_block_config");
            ENABLE_RECONSTRUCT_BLOCKS = builder
                    .comment("Enables Reconstruct Blocks \n" +
                            "Default True")
                    .define("enableReconstructBlocks", true);
            RECONSTRUCT_ON_LOSS = builder
                    .comment("Reconstructs on loss \n" +
                            "Default false")
                    .define("reconstructOnLoss", false);
            RECONSTRUCT_TICK_DELAY = builder
                    .comment("This value plus the variable version together make up the time required before reconstructing. \n" +
                            "Default 100")
                    .defineInRange("reconstructTickDelay", 100, 0, 999999999);
            RECONSTRUCT_VARIABLE_TICK_DELAY = builder
                    .comment("A random value between 0 and this value is added to the static delay to determine total delay. \n" +
                            "Default 200")
                    .defineInRange("reconstructVariableTickDelay", 60, 0, 999999999);
            builder.pop();
        }

        private void AIConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("AI Config").push("ai_config");
            ENABLE_DIGGER_AI = builder
                    .comment("Enables Digger AI and the spawning of diggers \n" +
                            "Default true")
                    .define("enableDiggerAI", true);
            builder.pop();
        }
    }

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();
    }
}