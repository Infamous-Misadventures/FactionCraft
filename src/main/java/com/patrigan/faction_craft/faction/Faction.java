package com.patrigan.faction_craft.faction;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.data.ResourceSet;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class Faction {
    public static final Faction DEFAULT = new Faction(new ResourceLocation("faction/default"), false, new CompoundTag(), FactionRaidConfig.DEFAULT, FactionBoostConfig.DEFAULT, FactionRelations.DEFAULT, Collections.emptyList(), new ResourceLocation(MODID, "default"), ResourceSet.getEmpty(Registry.ENTITY_TYPE_REGISTRY));

    public static final Codec<Faction> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("name").forGetter(Faction::getName),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(data -> data.replace),
                    CompoundTag.CODEC.fieldOf("banner").forGetter(Faction::getBanner),
                    FactionRaidConfig.CODEC.optionalFieldOf("raid_config", FactionRaidConfig.DEFAULT).forGetter(Faction::getRaidConfig),
                    FactionBoostConfig.CODEC.optionalFieldOf("boosts", FactionBoostConfig.DEFAULT).forGetter(Faction::getBoostConfig),
                    FactionRelations.CODEC.optionalFieldOf("relations", FactionRelations.DEFAULT).forGetter(Faction::getRelations),
                    FactionEntityType.CODEC_OLD.listOf().optionalFieldOf("entities", new ArrayList<>()).forGetter(Faction::getEntityTypes),
                    ResourceLocation.CODEC.optionalFieldOf("activation_advancement", new ResourceLocation(MODID, "activation_advancement")).forGetter(Faction::getActivationAdvancement),
                    ResourceSet.getCodec(Registry.ENTITY_TYPE_REGISTRY).optionalFieldOf("default_entities", ResourceSet.getEmpty(Registry.ENTITY_TYPE_REGISTRY)).forGetter(data -> data.defaultEntities)
            ).apply(builder, Faction::new));

    private final ResourceLocation name;
    private final boolean replace;
    private final CompoundTag banner;
    private final FactionRaidConfig raidConfig;
    private final FactionBoostConfig boostConfig;
    private final FactionRelations relations;
    private List<FactionEntityType> entityTypes;
    private final ResourceLocation activationAdvancement;
    private final ResourceSet<EntityType<?>> defaultEntities;

    public Faction(ResourceLocation name, boolean replace, CompoundTag banner, FactionRaidConfig raidConfig, FactionBoostConfig boostConfig, FactionRelations relations, List<FactionEntityType> entityTypes, ResourceLocation activationAdvancement, ResourceSet<EntityType<?>> defaultEntities) {
        this.name = name;
        this.replace = replace;
        this.banner = banner;
        this.raidConfig = raidConfig;
        this.boostConfig = boostConfig;
        this.relations = relations;
        this.entityTypes = entityTypes;
        this.activationAdvancement = activationAdvancement;
        this.defaultEntities = defaultEntities;
    }

    public ResourceLocation getName() {
        return name;
    }

    public boolean isReplace() {
        return replace;
    }

    public CompoundTag getBanner() {
        return banner;
    }

    public FactionRaidConfig getRaidConfig() {
        return raidConfig;
    }

    public FactionBoostConfig getBoostConfig() {
        return boostConfig;
    }

    public FactionRelations getRelations() {
        return relations;
    }

    public List<FactionEntityType> getEntityTypes() {
        return entityTypes;
    }

    public ResourceLocation getActivationAdvancement() {
        return activationAdvancement;
    }

    public ResourceSet<EntityType<?>> getDefaultEntities() {
        return defaultEntities;
    }

    public List<Pair<FactionEntityType, Integer>> getWeightMap(){
        return entityTypes.stream().map(factionEntityType -> new Pair<>(factionEntityType, factionEntityType.getWeight())).toList();
    }

    public List<Pair<FactionEntityType, Integer>> getWeightMapForRank(FactionEntityType.FactionRank rank){
        return entityTypes.stream().filter(factionEntityType -> factionEntityType.hasRank(rank)).map(factionEntityType -> new Pair<>(factionEntityType, factionEntityType.getWeight())).toList();
    }

    public List<Pair<FactionEntityType, Integer>> getWeightMap(EntityWeightMapProperties properties){
        return entityTypes.stream().filter(
                factionEntityType -> factionEntityType.canSpawnInWave(properties.getWave())
                && factionEntityType.hasRanks(properties.getAllowedRanks())
                && factionEntityType.canSpawnForOmen(properties.getOmen())
                && factionEntityType.canSpawnForBiome(properties.getBiome())
                && factionEntityType.canSpawnForYPos(properties.getBlockPos()))
                .map(factionEntityType -> new Pair<>(factionEntityType, factionEntityType.getWeight())).toList();
    }

    public ItemStack getBannerInstance() {
        ItemStack itemstack = ItemStack.of(banner);
        return itemstack;
    }

    public void makeBannerHolder(Mob mobEntity) {
        mobEntity.setItemSlot(EquipmentSlot.HEAD, getBannerInstance());
        mobEntity.setDropChance(EquipmentSlot.HEAD, 2.0F);
    }

    public void addEntityTypes(Collection<FactionEntityType> factionEntityTypes) {
        entityTypes = new ArrayList<>(entityTypes);
        entityTypes.addAll(factionEntityTypes);
    }
}
