package com.patrigan.faction_craft.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class FactionCraftConfig {
    public static ForgeConfigSpec.ConfigValue<List<String>> DISABLED_FACTIONS;
    public static ForgeConfigSpec.ConfigValue<Integer> NUMBER_WAVES_EASY;
    public static ForgeConfigSpec.ConfigValue<Integer> NUMBER_WAVES_NORMAL;
    public static ForgeConfigSpec.ConfigValue<Integer> NUMBER_WAVES_HARD;
    public static ForgeConfigSpec.ConfigValue<Float> STARTING_WAVE_MULTIPLIER;
    public static ForgeConfigSpec.ConfigValue<Float> MULTIPLIER_INCREASE_PER_WAVE;
    public static ForgeConfigSpec.ConfigValue<Float> WAVE_TARGET_STRENGTH_SPREAD;
    public static ForgeConfigSpec.ConfigValue<Float> TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_EASY;
    public static ForgeConfigSpec.ConfigValue<Float> TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_NORMAL;
    public static ForgeConfigSpec.ConfigValue<Float> TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_HARD;

    public static ForgeConfigSpec.ConfigValue<Float> VILLAGE_RAID_TARGET_STRENGTH_MULTIPLIER;
    public static ForgeConfigSpec.ConfigValue<Float> VILLAGE_RAID_ADDITIONAL_WAVE_CHANCE;
    public static ForgeConfigSpec.ConfigValue<Integer> VILLAGE_RAID_VILLAGER_WEIGHT;
    public static ForgeConfigSpec.ConfigValue<Integer> VILLAGE_RAID_IRON_GOLEM_WEIGHT;

    public static ForgeConfigSpec.ConfigValue<Boolean> DISABLE_FACTION_PATROLS;
    public static ForgeConfigSpec.ConfigValue<Boolean> DISABLE_VANILLA_PATROLS;
    public static ForgeConfigSpec.ConfigValue<Long> DAYTIME_BEFORE_SPAWNING;
    public static ForgeConfigSpec.ConfigValue<Integer> TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS;
    public static ForgeConfigSpec.ConfigValue<Integer> VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS;
    public static ForgeConfigSpec.ConfigValue<Float> SPAWN_CHANCE_ON_SPAWN_ATTEMPT;

    public static class Common {

        public Common(ForgeConfigSpec.Builder builder){

            builder.comment("Factions").push("factions");
            DISABLED_FACTIONS = builder
                    .comment("A list of disabled factions. \n" +
                            "Default: The internal skeleton test faction. ")
                    .define("disabledFactions", Arrays.asList(MODID+":undead", MODID+":skeleton_test"));
            builder.pop();

            builder.comment("Standard Raid Calculations").push("standard_raid_calculations");
            NUMBER_WAVES_EASY = builder
                    .comment("The number of waves for Easy difficulty. \n" +
                            "Default 2")
                    .define("numberWavesEasy", 2);
            NUMBER_WAVES_NORMAL = builder
                    .comment("The number of waves for Normal difficulty. \n" +
                            "Default 4")
                    .define("numberWavesNormal", 4);
            NUMBER_WAVES_HARD = builder
                    .comment("The number of waves for Hard difficulty. \n" +
                            "Default 6")
                    .define("numberWavesHard", 6);
            STARTING_WAVE_MULTIPLIER = builder
                    .comment("The multiplier for the target strength for the first wave. \n" +
                            "1.0 disables Starting wave multiplier. Default 0.5")
                    .define("startingWaveMultiplier", 0.5F);
            MULTIPLIER_INCREASE_PER_WAVE = builder
                    .comment("The amount the multiplier for the target strength increases per wave. \n" +
                            "0.0 removes increase per wave. Default 0.15")
                    .define("multiplierIncreasePerWave", 0.15F);
            WAVE_TARGET_STRENGTH_SPREAD = builder
                    .comment("The amount the the target strength can fluctuate per wave. \n" +
                            "Default 0.1")
                    .define("waveTargetStrengthSpread", 0.1F);
            TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_EASY = builder
                    .comment("The multiplier applied to target strength for Easy difficulty. \n" +
                            "1.0 disables difficulty based multipliers. Default 0.9")
                    .define("targetStrengthDifficultyMultiplierEasy", 0.9F);
            TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_NORMAL = builder
                    .comment("The multiplier applied to target strength for Normal difficulty. \n" +
                            "1.0 disables difficulty based multipliers. Default 1.0")
                    .define("targetStrengthDifficultyMultiplierNormal", 1.0F);
            TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_HARD = builder
                    .comment("The multiplier applied to target strength for Hard difficulty. \n" +
                            "1.0 disables difficulty based multipliers. Default 1.1")
                    .define("targetStrengthDifficultyMultiplierHard", 1.1F);

            builder.pop();

            builder.comment("Village Raid Target Calculations").push("village_raid_target_calculations");
            VILLAGE_RAID_TARGET_STRENGTH_MULTIPLIER = builder
                    .comment("Applied to the target strength of the village \n" +
                            "Multiplies the target strength and rounds down. \n" +
                            "Vanilla villages have a target strength between 10 and 20. Default 1.0F")
                    .define("villageRaidTargetStrengthMultiplier", 1.0F);
            VILLAGE_RAID_ADDITIONAL_WAVE_CHANCE = builder
                    .comment("Applied to the target strength of the village \n" +
                            "Additional waves is calulcated by applying this value to target strength. \n" +
                            "Vanilla villages have a target strength between 10 and 20. Default 0.08F")
                    .define("villageRaidAdditionalWaveChance", 0.08F);
            VILLAGE_RAID_VILLAGER_WEIGHT = builder
                    .comment("The amount that 1 villager adds to the strength of a village. \n" +
                            "Default 1")
                    .define("villageRaidVillagerWeight", 1);
            VILLAGE_RAID_IRON_GOLEM_WEIGHT = builder
                    .comment("The amount that 1 Iron Golem adds to the strength of a village. \n" +
                            "Default 2")
                    .define("villageRaidIronGolemWeight", 2);
            builder.pop();

            builder.comment("Patroller Config").push("patroller_config");
            DISABLE_FACTION_PATROLS = builder
                    .comment("Disables faction patrols \n" +
                            "Default false")
                    .define("disableFactionPatrols", false);
            DISABLE_VANILLA_PATROLS = builder
                    .comment("Disables vanilla patrols (and such, vanilla raids) \n" +
                            "Default true")
                    .define("disableVanillaPatrols", true);
            DAYTIME_BEFORE_SPAWNING = builder
                    .comment("ingame daytime before patrols start spawning. \n" +
                            "Vanilla is 24000L equivalent of 5days. Default 24000L")
                    .define("daytimeBeforeSpawning", 24000L);
            TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS = builder
                    .comment("This value plus the variable version together make up the time between two spawn attempts. \n" +
                            "Vanilla default 12000, Default 6000")
                    .define("tickDelayBetweenSpawnAttempts", 2);
            VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS = builder
                    .comment("A random value between 0 and this value is added to the static delay to determine total delay. \n" +
                            "Vanilla default is 1200. Default 1200")
                    .define("variableTickDelayBetweenSpawnAttempts", 2);
            SPAWN_CHANCE_ON_SPAWN_ATTEMPT = builder
                    .comment("The chance a patrol spawns on a spawn attempt. \n" +
                            "Vanilla default is 0.2 Default 0.4")
                    .define("spawnChanceOnSpawnAttempt", 0.4F);
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