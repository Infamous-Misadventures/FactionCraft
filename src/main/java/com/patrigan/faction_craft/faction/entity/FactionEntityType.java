package com.patrigan.faction_craft.faction.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.data.ResourceSet;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.registry.FactionEntityTypes;
import com.patrigan.faction_craft.util.IntRange;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES;


public class FactionEntityType {
    public static final FactionEntityType DEFAULT = new FactionEntityType(new ResourceLocation("minecraft:pig"), new CompoundTag(), false, true, 1, 1, List.of(FactionEntityRank.SOLDIER), EntityBoostConfig.DEFAULT, new IntRange(0, 10000), new IntRange(0, 10000), Integer.MAX_VALUE, new IntRange(0, 10000), new IntRange(-64, 320), ResourceSet.getEmpty(Registry.BIOME_REGISTRY), ResourceSet.getEmpty(Registry.BIOME_REGISTRY));
    public static final Codec<FactionEntityType> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("entity_type").forGetter(data -> data.entityType),
                    CompoundTag.CODEC.optionalFieldOf("tag", new CompoundTag()).forGetter(data -> data.tag),
                    Codec.BOOL.optionalFieldOf("tag_first", false).forGetter(data -> data.tagFirst),
                    Codec.BOOL.optionalFieldOf("should_finalize_spawn", true).forGetter(data -> data.shouldFinalizeSpawn),
                    Codec.INT.fieldOf("weight").forGetter(data -> data.weight),
                    Codec.INT.fieldOf("strength").forGetter(data -> data.strength),
                    FactionEntityRank.CODEC.listOf().fieldOf("ranks").forGetter(data -> data.ranks),
                    EntityBoostConfig.CODEC.optionalFieldOf("boosts", EntityBoostConfig.DEFAULT).forGetter(data -> data.entityBoostConfig),
                    IntRange.getCodec(0, 10000).optionalFieldOf("wave_range", new IntRange(1, 10000)).forGetter(data -> data.waveRange),
                    IntRange.getCodec(0, 10000).optionalFieldOf("spawned_range", new IntRange(0, 10000)).forGetter(data -> data.spawnedRange),
                    Codec.INT.optionalFieldOf("max_spawned_per_x", Integer.MAX_VALUE).forGetter(data -> data.maxSpawnedPerX),
                    IntRange.getCodec(0, 10000).optionalFieldOf("omen_range", new IntRange(0, 10000)).forGetter(data -> data.omenRange),
                    IntRange.getCodec(-10000, 10000).optionalFieldOf("y_range", new IntRange(-64, 320)).forGetter(data -> data.yRange),
                    ResourceSet.getCodec(Registry.BIOME_REGISTRY).optionalFieldOf("biome_whitelist", ResourceSet.getEmpty(Registry.BIOME_REGISTRY)).forGetter(data -> data.biomeWhitelist),
                    ResourceSet.getCodec(Registry.BIOME_REGISTRY).optionalFieldOf("biome_blacklist", ResourceSet.getEmpty(Registry.BIOME_REGISTRY)).forGetter(data -> data.biomeBlacklist)
            ).apply(builder, FactionEntityType::new));

    public static final Codec<FactionEntityType> CODEC_OLD = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("entity_type").forGetter(data -> data.entityType),
                    CompoundTag.CODEC.optionalFieldOf("tag", new CompoundTag()).forGetter(data -> data.tag),
                    Codec.INT.fieldOf("weight").forGetter(data -> data.weight),
                    Codec.INT.fieldOf("strength").forGetter(data -> data.strength),
                    FactionEntityRank.CODEC.fieldOf("rank").forGetter(data -> data.ranks.get(0)),
                    FactionEntityRank.CODEC.fieldOf("maximum_rank").forGetter(data -> data.ranks.get(data.ranks.size()-1)),
                    EntityBoostConfig.CODEC.optionalFieldOf("boosts", EntityBoostConfig.DEFAULT).forGetter(data -> data.entityBoostConfig),
                    Codec.INT.fieldOf("minimum_wave").forGetter(data -> data.waveRange.getMin()),
                    Codec.INT.optionalFieldOf("maximum_wave", 10000).forGetter(data -> data.waveRange.getMax()),
                    Codec.INT.optionalFieldOf("minimum_spawned", 0).forGetter(data -> data.spawnedRange.getMin()),
                    Codec.INT.optionalFieldOf("maximum_spawned", 10000).forGetter(data -> data.spawnedRange.getMax()),
                    Codec.INT.optionalFieldOf("minimum_omen", 0).forGetter(data -> data.omenRange.getMin()),
                    Codec.INT.optionalFieldOf("maximum_omen", 10000).forGetter(data -> data.omenRange.getMax())
            ).apply(builder, FactionEntityType::new));

    private final ResourceLocation entityType;
    private final CompoundTag tag;
    private final boolean tagFirst;
    private final boolean shouldFinalizeSpawn;
    private final int weight;
    private final int strength;
    private final List<FactionEntityRank> ranks;
    private final EntityBoostConfig entityBoostConfig;
    private final IntRange waveRange;
    private final IntRange spawnedRange;
    private final int maxSpawnedPerX;
    private final IntRange omenRange;
    private final IntRange yRange;
    private final ResourceSet<Biome> biomeWhitelist;
    private final ResourceSet<Biome> biomeBlacklist;

    public FactionEntityType(ResourceLocation entityType, CompoundTag tag, boolean tagFirst, boolean shouldFinalizeSpawn, int weight, int strength, List<FactionEntityRank> ranks, EntityBoostConfig entityBoostConfig, IntRange waveRange, IntRange spawnedRange, int maxSpawnedPerX, IntRange omenRange, IntRange yRange, ResourceSet<Biome> biomeWhitelist, ResourceSet<Biome> biomeBlacklist) {
        this.entityType = entityType;
        this.tag = tag;
        this.tagFirst = tagFirst;
        this.shouldFinalizeSpawn = shouldFinalizeSpawn;
        this.weight = weight;
        this.strength = strength;
        this.ranks = ranks;
        this.entityBoostConfig = entityBoostConfig;
        this.waveRange = waveRange;
        this.spawnedRange = spawnedRange;
        this.maxSpawnedPerX = maxSpawnedPerX <= 0 ? Integer.MAX_VALUE : maxSpawnedPerX;
        this.omenRange = omenRange;
        this.yRange = yRange;
        this.biomeWhitelist = biomeWhitelist;
        this.biomeBlacklist = biomeBlacklist;
    }

    public FactionEntityType(ResourceLocation entityType, CompoundTag tag, int weight, int strength, FactionEntityRank rank, FactionEntityRank maximumRank, EntityBoostConfig entityBoostConfig, int minimumWave, int maximumWave, int minimumSpawned, int maximumSpawned, int minimumOmen, int maximumOmen) {
        this.entityType = entityType;
        this.tag = tag;
        this.tagFirst = false;
        this.shouldFinalizeSpawn = true;
        this.weight = weight;
        this.strength = strength;
        this.ranks = new ArrayList<>();
        FactionEntityRank currentRank = rank;
        while (currentRank != null) {
            ranks.add(currentRank);
            if (currentRank.equals(maximumRank)) {
                break;
            }
            currentRank = currentRank.promote();
        }
        this.entityBoostConfig = entityBoostConfig;
        this.waveRange = new IntRange(minimumWave, maximumWave);
        this.spawnedRange = new IntRange(minimumSpawned, maximumSpawned);
        this.maxSpawnedPerX = Integer.MAX_VALUE;
        this.omenRange = new IntRange(minimumOmen, maximumOmen);
        this.yRange = new IntRange(-64, 320);
        this.biomeWhitelist = ResourceSet.getEmpty(Registry.BIOME_REGISTRY);
        this.biomeBlacklist = ResourceSet.getEmpty(Registry.BIOME_REGISTRY);
    }

    public static FactionEntityType load(CompoundTag compoundNbt) {
        if (compoundNbt.contains("factionEntityType")) {
            ResourceLocation factionEntityType = new ResourceLocation(compoundNbt.getString("factionEntityType"));
            if (FactionEntityTypes.getFactionEntityType(factionEntityType) != null) {
                return FactionEntityTypes.getFactionEntityType(factionEntityType);
            } else {
                return FactionEntityType.DEFAULT;
            }
        } else {

            FactionEntityRank rank = FactionEntityRank.byName(compoundNbt.getString("rank"), FactionEntityRank.SOLDIER);
            FactionEntityRank maximumRank = FactionEntityRank.byName(compoundNbt.getString("maximumRank"), null);
            List<FactionEntityRank> ranks = new ArrayList<>();
            FactionEntityRank currentRank = rank;
            while (currentRank != null) {
                ranks.add(currentRank);
                if (currentRank.equals(maximumRank)) {
                    break;
                }
                currentRank = currentRank.promote();
            }
            return new FactionEntityType(
                    new ResourceLocation(compoundNbt.getString("entityType")),
                    compoundNbt.getCompound("tag"),
                    false,
                    true,
                    compoundNbt.getInt("weight"),
                    compoundNbt.getInt("strength"),
                    ranks,
                    EntityBoostConfig.load(compoundNbt.getCompound("entityBoostConfig")),
                    new IntRange(compoundNbt.getInt("minimumWave"),
                            compoundNbt.getInt("maximumWave")),
                    new IntRange(compoundNbt.getInt("minimumSpawned"),
                            compoundNbt.getInt("maximumSpawned")),
                    Integer.MAX_VALUE,
                    new IntRange(compoundNbt.getInt("minimumOmen"),
                            compoundNbt.getInt("maximumOmen")),
                    new IntRange(-64, 320),
                    ResourceSet.getEmpty(Registry.BIOME_REGISTRY),
                    ResourceSet.getEmpty(Registry.BIOME_REGISTRY)
            );
        }
    }

    public ResourceLocation getEntityType() {
        return entityType;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public int getWeight() {
        return weight;
    }

    public int getStrength() {
        return strength;
    }

    public EntityBoostConfig getBoostConfig() {
        return entityBoostConfig;
    }

    public IntRange getWaveRange() {
        return waveRange;
    }

    public IntRange getSpawnedRange() {
        return spawnedRange;
    }

    public int getMaxSpawnedPerX() {
        return maxSpawnedPerX;
    }

    public IntRange getOmenRange() {
        return omenRange;
    }

    public IntRange getYRange() {
        return yRange;
    }

    public ResourceSet<Biome> getBiomeWhitelist() {
        return biomeWhitelist;
    }

    public ResourceSet<Biome> getBiomeBlacklist() {
        return biomeBlacklist;
    }

    public boolean canSpawnInWave(int wave) {
        return getWaveRange().isBetweenInclusive(wave);
    }

    public boolean canSpawnForOmen(int omen) {
        return getOmenRange().isBetweenInclusive(omen);
    }

    public boolean canSpawnForYPos(BlockPos blockPos) {
        return blockPos == null || getYRange().isBetweenInclusive(blockPos.getY());
    }

    public boolean canSpawnForBiome(Biome biome) {
        if (biome == null) {
            return this.getBiomeWhitelist().isEmpty();
        } else {
            if (getBiomeBlacklist().contains(biome)) {
                return false;
            } else {
                return this.getBiomeWhitelist().isEmpty() || this.getBiomeWhitelist().contains(biome);
            }
        }
    }

    public List<FactionEntityRank> getRanks() {
        return ranks;
    }

    public boolean canBeBannerHolder() {
        List<FactionEntityRank> possibleCaptains = Arrays.asList(FactionEntityRank.CAPTAIN, FactionEntityRank.GENERAL, FactionEntityRank.LEADER);
        for (FactionEntityRank possibleCaptain : possibleCaptains) {
            if(ranks.contains(possibleCaptain)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRank(FactionEntityRank requiredRank) {
        return getRanks().contains(requiredRank);
    }

    public boolean hasRanks(List<FactionEntityRank> requiredRanks) {
        List<FactionEntityRank> possibleRanks = getRanks();
        return requiredRanks.stream().anyMatch(possibleRanks::contains);
    }
    public int getMaxSpawned(int totalSpawned) {
        return getSpawnedRange().getMax() * Mth.ceil(totalSpawned / (float) getMaxSpawnedPerX());
    }

    public Entity createEntity(ServerLevel level, Faction faction, BlockPos spawnBlockPos, boolean bannerHolder, MobSpawnType spawnReason) {
        EntityType<?> entityType = ENTITY_TYPES.getValue(this.getEntityType());
        Entity entity;
        entity = entityType.create(level);
        if (entity == null) {
            return null;
        }
        entity.moveTo(spawnBlockPos.getX() + 0.5D, spawnBlockPos.getY() + 1.0D, spawnBlockPos.getZ() + 0.5D, entity.getYRot(), entity.getXRot());
        if (entity instanceof Mob mobEntity) {
            if (bannerHolder) {
                faction.makeBannerHolder(mobEntity);
            }
            if (net.minecraftforge.common.ForgeHooks.canEntitySpawn(mobEntity, level, spawnBlockPos.getX(), spawnBlockPos.getY(), spawnBlockPos.getZ(), null, spawnReason) == -1)
                return null;
            if(tagFirst) mergeTag(entity, this.getTag());
            if(shouldFinalizeSpawn)  mobEntity.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnBlockPos), MobSpawnType.EVENT, null, null);
            if(!tagFirst) mergeTag(entity, this.getTag());
            if(mobEntity.getNavigation() instanceof GroundPathNavigation groundPathNavigation) {
                groundPathNavigation.setCanOpenDoors(true);
            }
            mobEntity.setOnGround(true);
        }
        entity.getRootVehicle().getSelfAndPassengers()
                .forEach(stackedEntity -> {
                    if (stackedEntity instanceof Mob mob) {
                        FactionEntity cap = FactionEntityHelper.getFactionEntityCapability(mob);
                        cap.setFaction(faction);
                        cap.setFactionEntityType(this);
                        cap.getFaction().getBoostConfig().getMandatoryBoosts().forEach(boost -> boost.apply(mob));
                        cap.getFactionEntityType().getBoostConfig().getMandatoryBoosts().forEach(boost -> boost.apply(mob));
                    }
                });

        level.addFreshEntityWithPassengers(entity.getRootVehicle());
        return entity;
    }

    private void mergeTag(Entity entity, CompoundTag tag) {
        if(tag.isEmpty()) return;
        CompoundTag toLoad = new CompoundTag();
        entity.save(toLoad);
        toLoad = toLoad.merge(tag);
        entity.load(toLoad);
    }

    public CompoundTag save(CompoundTag compoundNbt) {
        ResourceLocation factionEntityType = FactionEntityTypes.getFactionEntityTypeKey(this);
        if (factionEntityType != null) {
            compoundNbt.putString("factionEntityType", factionEntityType.toString());
        } else {
            compoundNbt.putString("entityType", this.entityType.toString());
            compoundNbt.put("tag", tag);
            compoundNbt.putInt("weight", weight);
            compoundNbt.putInt("strength", strength);
            compoundNbt.putString("rank", ranks.get(0).getName());
            compoundNbt.putString("maximumRank", ranks.get(ranks.size() - 1).getName());
            CompoundTag boostConfigNbt = new CompoundTag();
            compoundNbt.put("entityBoostConfig", entityBoostConfig.save(boostConfigNbt));
            compoundNbt.putInt("minimumWave", waveRange.min());
            compoundNbt.putInt("maximumWave", waveRange.max());
            compoundNbt.putInt("minimumSpawned", spawnedRange.min());
            compoundNbt.putInt("maximumSpawned", spawnedRange.max());
            compoundNbt.putInt("minimumOmen", omenRange.min());
            compoundNbt.putInt("maximumOmen", omenRange.max());
        }
        return compoundNbt;
    }

}
