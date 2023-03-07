package com.patrigan.faction_craft.faction.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.registry.FactionEntityTypes;
import com.patrigan.faction_craft.util.IntRange;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.world.level.biome.Biome.DIRECT_CODEC;
import static net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES;


public class FactionEntityType {
    public static final FactionEntityType DEFAULT = new FactionEntityType(new ResourceLocation("minecraft:pig"), new CompoundTag(), 1, 1, FactionRank.SOLDIER, FactionRank.SOLDIER, EntityBoostConfig.DEFAULT, new IntRange(0, 10000), new IntRange(0, 10000), new IntRange(0, 10000), new IntRange(-64, 320), HolderSet.direct(), HolderSet.direct());
    public static final Codec<FactionEntityType> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("entity_type").forGetter(data -> data.entityType),
                    CompoundTag.CODEC.optionalFieldOf("tag", new CompoundTag()).forGetter(data -> data.tag),
                    Codec.INT.fieldOf("weight").forGetter(data -> data.weight),
                    Codec.INT.fieldOf("strength").forGetter(data -> data.strength),
                    FactionRank.CODEC.fieldOf("rank").forGetter(data -> data.rank),
                    FactionRank.CODEC.fieldOf("maximum_rank").forGetter(data -> data.maximumRank),
                    EntityBoostConfig.CODEC.optionalFieldOf("boosts", EntityBoostConfig.DEFAULT).forGetter(data -> data.entityBoostConfig),
                    IntRange.getCodec(0, 10000).optionalFieldOf("wave_range", new IntRange(1, 10000)).forGetter(data -> data.waveRange),
                    IntRange.getCodec(0, 10000).optionalFieldOf("spawned_range", new IntRange(0, 10000)).forGetter(data -> data.spawnedRange),
                    IntRange.getCodec(0, 10000).optionalFieldOf("omen_range", new IntRange(0, 10000)).forGetter(data -> data.omenRange),
                    IntRange.getCodec(-10000, 10000).optionalFieldOf("y_range", new IntRange(-64, 320)).forGetter(data -> data.yRange),
                    RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY, DIRECT_CODEC).optionalFieldOf("biome_whitelist", HolderSet.direct()).forGetter(data -> data.biomeWhitelist),
                    RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY, DIRECT_CODEC).optionalFieldOf("biome_blacklist", HolderSet.direct()).forGetter(data -> data.biomeBlacklist)
            ).apply(builder, FactionEntityType::new));

    public static final Codec<FactionEntityType> CODEC_OLD = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("entity_type").forGetter(data -> data.entityType),
                    CompoundTag.CODEC.optionalFieldOf("tag", new CompoundTag()).forGetter(data -> data.tag),
                    Codec.INT.fieldOf("weight").forGetter(data -> data.weight),
                    Codec.INT.fieldOf("strength").forGetter(data -> data.strength),
                    FactionRank.CODEC.fieldOf("rank").forGetter(data -> data.rank),
                    FactionRank.CODEC.fieldOf("maximum_rank").forGetter(data -> data.maximumRank),
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
    private final int weight;
    private final int strength;
    private final FactionRank rank;
    private final FactionRank maximumRank;
    private final EntityBoostConfig entityBoostConfig;
    private final IntRange waveRange;
    private final IntRange spawnedRange;
    private final IntRange omenRange;
    private final IntRange yRange;
    private final HolderSet<Biome> biomeWhitelist;
    private final HolderSet<Biome> biomeBlacklist;

    public FactionEntityType(ResourceLocation entityType, CompoundTag tag, int weight, int strength, FactionRank rank, FactionRank maximumRank, EntityBoostConfig entityBoostConfig, IntRange waveRange, IntRange spawnedRange, IntRange omenRange, IntRange yRange, HolderSet<Biome> biomeWhitelist, HolderSet<Biome> biomeBlacklist) {
        this.entityType = entityType;
        this.tag = tag;
        this.weight = weight;
        this.strength = strength;
        this.rank = rank;
        this.maximumRank = maximumRank;
        this.entityBoostConfig = entityBoostConfig;
        this.waveRange = waveRange;
        this.spawnedRange = spawnedRange;
        this.omenRange = omenRange;
        this.yRange = yRange;
        this.biomeWhitelist = biomeWhitelist;
        this.biomeBlacklist = biomeBlacklist;
    }

    public FactionEntityType(ResourceLocation entityType, CompoundTag tag, int weight, int strength, FactionRank rank, FactionRank maximumRank, EntityBoostConfig entityBoostConfig, int minimumWave, int maximumWave, int minimumSpawned, int maximumSpawned, int minimumOmen, int maximumOmen) {
        this.entityType = entityType;
        this.tag = tag;
        this.weight = weight;
        this.strength = strength;
        this.rank = rank;
        this.maximumRank = maximumRank;
        this.entityBoostConfig = entityBoostConfig;
        this.waveRange = new IntRange(minimumWave, maximumWave);
        this.spawnedRange = new IntRange(minimumSpawned, maximumSpawned);
        this.omenRange = new IntRange(minimumOmen, maximumOmen);
        this.yRange = new IntRange(-64, 320);
        this.biomeWhitelist = HolderSet.direct();
        this.biomeBlacklist = HolderSet.direct();
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
            return new FactionEntityType(
                    new ResourceLocation(compoundNbt.getString("entityType")),
                    compoundNbt.getCompound("tag"),
                    compoundNbt.getInt("weight"),
                    compoundNbt.getInt("strength"),
                    FactionRank.byName(compoundNbt.getString("rank"), FactionRank.SOLDIER),
                    FactionRank.byName(compoundNbt.getString("maximumRank"), null),
                    EntityBoostConfig.load(compoundNbt.getCompound("entityBoostConfig")),
                    new IntRange(compoundNbt.getInt("minimumWave"),
                            compoundNbt.getInt("maximumWave")),
                    new IntRange(compoundNbt.getInt("minimumSpawned"),
                            compoundNbt.getInt("maximumSpawned")),
                    new IntRange(compoundNbt.getInt("minimumOmen"),
                            compoundNbt.getInt("maximumOmen")),
                    new IntRange(-64, 320),
                    HolderSet.direct(),
                    HolderSet.direct()
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

    public FactionRank getRank() {
        return rank;
    }

    public FactionRank getMaximumRank() {
        return maximumRank;
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

    public IntRange getOmenRange() {
        return omenRange;
    }

    public IntRange getYRange() {
        return yRange;
    }

    public HolderSet<Biome> getBiomeWhitelist() {
        return biomeWhitelist;
    }

    public HolderSet<Biome> getBiomeBlacklist() {
        return biomeBlacklist;
    }

    public boolean canSpawnInWave(int wave) {
        return getWaveRange().isBetween(wave);
    }

    public boolean canSpawnForOmen(int omen) {
        return getOmenRange().isBetween(omen);
    }

    public boolean canSpawnForYPos(BlockPos blockPos) {
        return blockPos != null && getYRange().isBetween(blockPos.getY());
    }

    public boolean canSpawnForBiome(Biome biome) {
        if (biome == null) {
            return this.getBiomeWhitelist().size() == 0;
        } else if (getBiomeBlacklist().contains(Holder.direct(biome))) {
            return false;
        } else {
            return this.getBiomeWhitelist().size() == 0 || this.getBiomeWhitelist().contains(Holder.direct(biome));
        }
    }

    public List<FactionRank> getRanks() {
        List<FactionRank> ranks = new ArrayList<>();
        FactionRank currentRank = rank;
        while (currentRank != null) {
            ranks.add(currentRank);
            if (currentRank.equals(getMaximumRank())) {
                break;
            }
            currentRank = currentRank.promote();
        }
        return ranks;
    }

    public boolean canBeBannerHolder() {
        FactionRank currentRank = this.getRank();
        List<FactionRank> possibleCaptains = Arrays.asList(FactionRank.CAPTAIN, FactionRank.GENERAL, FactionRank.LEADER);
        while (currentRank != null) {
            if (possibleCaptains.contains(currentRank)) {
                return true;
            } else {
                if (currentRank.equals(getMaximumRank())) {
                    return false;
                }
            }
            currentRank = currentRank.promote();
        }
        return false;
    }

    public boolean hasRank(FactionRank requiredRank) {
        FactionRank currentRank = rank;
        while (currentRank != null) {
            if (currentRank.equals(requiredRank)) {
                return true;
            }
            currentRank = currentRank.promote();
        }
        return false;
    }

    public boolean hasRanks(List<FactionRank> requiredRanks) {
        List<FactionRank> possibleRanks = getRanks();
        return requiredRanks.stream().anyMatch(possibleRanks::contains);
    }

    public Entity createEntity(ServerLevel level, Faction faction, BlockPos spawnBlockPos, boolean bannerHolder, MobSpawnType spawnReason) {
        EntityType<?> entityType = ENTITY_TYPES.getValue(this.getEntityType());
        Entity entity;
        if (!this.getTag().isEmpty()) {
            CompoundTag compoundnbt = this.getTag().copy();
            compoundnbt.putString("id", this.getEntityType().toString());
            entity = EntityType.loadEntityRecursive(compoundnbt, level, createdEntity -> createdEntity);
        } else {
            entity = entityType.create(level);
        }
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
            if (this.getTag().isEmpty()) {
                mobEntity.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnBlockPos), MobSpawnType.EVENT, null, null);
            }
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

    public CompoundTag save(CompoundTag compoundNbt) {
        ResourceLocation factionEntityType = FactionEntityTypes.getFactionEntityTypeKey(this);
        if (factionEntityType != null) {
            compoundNbt.putString("factionEntityType", factionEntityType.toString());
        } else {
            compoundNbt.putString("entityType", this.entityType.toString());
            compoundNbt.put("tag", tag);
            compoundNbt.putInt("weight", weight);
            compoundNbt.putInt("strength", strength);
            compoundNbt.putString("rank", rank.getName());
            compoundNbt.putString("maximumRank", maximumRank.getName());
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

    public enum FactionRank {
        LEADER("leader", null),
        SUPPORT("support", null),
        MOUNT("mount", null),
        DIGGER("digger", null),
        GENERAL("general", LEADER),
        CAPTAIN("captain", GENERAL),
        SOLDIER("soldier", CAPTAIN);

        public static final Codec<FactionRank> CODEC = Codec.STRING.flatComapMap(s -> FactionRank.byName(s, null), d -> DataResult.success(d.getName()));

        private final String name;
        private final FactionRank promotion;

        FactionRank(String name, FactionRank promotion) {
            this.name = name;
            this.promotion = promotion;
        }

        public static FactionRank byName(String name, FactionRank defaultRank) {
            for (FactionRank factionRank : values()) {
                if (factionRank.name.equals(name)) {
                    return factionRank;
                }
            }

            return defaultRank;
        }

        public FactionRank promote() {
            return promotion;
        }

        public String getName() {
            return name;
        }
    }
}
