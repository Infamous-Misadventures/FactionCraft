package com.patrigan.faction_craft.faction.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.faction.Faction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;

import java.util.Arrays;
import java.util.List;

import static net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES;


public class FactionEntityType {
    public static final Codec<FactionEntityType> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("entity_type").forGetter(data -> data.entityType),
                    CompoundTag.CODEC.optionalFieldOf("tag", new CompoundTag()).forGetter(data -> data.tag),
                    Codec.INT.fieldOf("weight").forGetter(data -> data.weight),
                    Codec.INT.fieldOf("strength").forGetter(data -> data.strength),
                    FactionRank.CODEC.fieldOf("rank").forGetter(data -> data.rank),
                    FactionRank.CODEC.fieldOf("maximum_rank").forGetter(data -> data.maximumRank),
                    EntityBoostConfig.CODEC.optionalFieldOf("boosts", EntityBoostConfig.DEFAULT).forGetter(data -> data.entityBoostConfig),
                    Codec.INT.fieldOf("minimum_wave").forGetter(data -> data.minimumWave),
                    Codec.INT.optionalFieldOf("maximum_wave", 10000).forGetter(data -> data.maximumWave),
                    Codec.INT.optionalFieldOf("minimum_spawned", 0).forGetter(data -> data.minimumSpawned),
                    Codec.INT.optionalFieldOf("maximum_spawned", 10000).forGetter(data -> data.maximumSpawned)
            ).apply(builder, FactionEntityType::new));

    private final ResourceLocation entityType;
    private final CompoundTag tag;
    private final int weight;
    private final int strength;
    private final FactionRank rank;
    private final FactionRank maximumRank;
    private final EntityBoostConfig entityBoostConfig;
    private final int minimumWave;
    private final int maximumWave;
    private final int minimumSpawned;
    private final int maximumSpawned;

    public FactionEntityType(ResourceLocation entityType, CompoundTag tag, int weight, int strength, FactionRank rank, FactionRank maximumRank, EntityBoostConfig entityBoostConfig, int minimumWave, int maximumWave, int minimumSpawned, int maximumSpawned) {
        this.entityType = entityType;
        this.tag = tag;
        this.weight = weight;
        this.strength = strength;
        this.rank = rank;
        this.maximumRank = maximumRank;
        this.entityBoostConfig = entityBoostConfig;
        this.minimumWave = minimumWave;
        this.maximumWave = maximumWave;
        this.minimumSpawned = minimumSpawned;
        this.maximumSpawned = maximumSpawned;
    }

    public static FactionEntityType load(CompoundTag compoundNbt) {
        return new FactionEntityType(
                new ResourceLocation(compoundNbt.getString("entityType")),
                compoundNbt.getCompound("tag"),
                compoundNbt.getInt("weight"),
                compoundNbt.getInt("strength"),
                FactionRank.byName(compoundNbt.getString("rank"), FactionRank.SOLDIER),
                FactionRank.byName(compoundNbt.getString("maximumRank"), null),
                EntityBoostConfig.load(compoundNbt.getCompound("entityBoostConfig")),
                compoundNbt.getInt("minimumWave"),
                compoundNbt.getInt("maximumWave"),
                compoundNbt.getInt("minimumSpawned"),
                compoundNbt.getInt("maximumSpawned"));
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

    public int getMinimumWave() {
        return minimumWave;
    }

    public int getMaximumWave() {
        return maximumWave;
    }

    public int getMinimumSpawned() {
        return minimumSpawned;
    }

    public int getMaximumSpawned() {
        return maximumSpawned;
    }

    public boolean canSpawnInWave(int wave){
        return wave >= minimumWave && wave <= maximumWave;
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


    //See and use SummonCommand approach for tag and add to the level
    public Entity createEntity(ServerLevel level, Faction faction, BlockPos spawnBlockPos, boolean bannerHolder, MobSpawnType spawnReason) {
        EntityType<?> entityType = ENTITY_TYPES.getValue(this.getEntityType());
        Entity entity = null;
        if (!this.getTag().isEmpty()) {
            CompoundTag compoundnbt = this.getTag().copy();
            compoundnbt.putString("id", this.getEntityType().toString());
            entity = EntityType.loadEntityRecursive(compoundnbt, level, createdEntity -> {
                createdEntity.moveTo(spawnBlockPos.getX() + 0.5D, spawnBlockPos.getY() + 1.0D, spawnBlockPos.getZ() + 0.5D, createdEntity.getYRot(), createdEntity.getXRot());
                return createdEntity;
            });
        } else {
            entity = entityType.create(level);
            entity.setPos(spawnBlockPos.getX() + 0.5D, spawnBlockPos.getY() + 1.0D, spawnBlockPos.getZ() + 0.5D);
        }
        if (entity == null) {
            return null;
        }
        if (entity instanceof Mob) {
            Mob mobEntity = (Mob) entity;
            if (bannerHolder) {
                faction.makeBannerHolder(mobEntity);
            }
            if (net.minecraftforge.common.ForgeHooks.canEntitySpawn(mobEntity, level, spawnBlockPos.getX(), spawnBlockPos.getY(), spawnBlockPos.getZ(), null, spawnReason) == -1)
                return null;
            if (this.tag.isEmpty()) {
                mobEntity.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnBlockPos), MobSpawnType.EVENT, null, null);
            }
            mobEntity.setOnGround(true);
        }
        entity.getRootVehicle().getSelfAndPassengers()
            .forEach(stackedEntity -> {
                if (stackedEntity instanceof Mob)
                    FactionEntityHelper.getFactionEntityCapabilityLazy((Mob) stackedEntity)
                        .ifPresent(cap -> {
                            cap.setFaction(faction);
                            cap.setFactionEntityType(this);
                        });
            });

        level.addFreshEntityWithPassengers(entity.getRootVehicle());
        return entity;
    }

    public CompoundTag save(CompoundTag compoundNbt) {
        compoundNbt.putString("entityType", this.entityType.toString());
        compoundNbt.put("tag", tag);
        compoundNbt.putInt("weight", weight);
        compoundNbt.putInt("strength", strength);
        compoundNbt.putString("rank", rank.getName());
        compoundNbt.putString("maximumRank", maximumRank.getName());
        CompoundTag boostConfigNbt = new CompoundTag();
        compoundNbt.put("entityBoostConfig", entityBoostConfig.save(boostConfigNbt));
        compoundNbt.putInt("minimumWave", minimumWave);
        compoundNbt.putInt("maximumWave", maximumWave);
        compoundNbt.putInt("minimumSpawned", minimumSpawned);
        compoundNbt.putInt("maximumSpawned", maximumSpawned);
        return compoundNbt;
    }

    public enum FactionRank {
        LEADER("leader", null),
        SUPPORT("support", null),
        MOUNT("mount", null),
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
