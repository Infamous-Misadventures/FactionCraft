package com.patrigan.faction_craft.faction;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static com.patrigan.faction_craft.faction.entity.FactionEntityType.FactionRank.MOUNT;

public class Faction {
    public static final Faction DEFAULT = new Faction(new ResourceLocation("faction/default"), false, new CompoundTag(), FactionRaidConfig.DEFAULT, FactionBoostConfig.DEFAULT, FactionRelations.DEFAULT, Collections.emptyList(), new ResourceLocation(MODID, "default"));

    public static final Codec<Faction> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("name").forGetter(Faction::getName),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(data -> data.replace),
                    CompoundTag.CODEC.fieldOf("banner").forGetter(Faction::getBanner),
                    FactionRaidConfig.CODEC.optionalFieldOf("raid_config", FactionRaidConfig.DEFAULT).forGetter(Faction::getRaidConfig),
                    FactionBoostConfig.CODEC.optionalFieldOf("boosts", FactionBoostConfig.DEFAULT).forGetter(Faction::getBoostConfig),
                    FactionRelations.CODEC.optionalFieldOf("relations", FactionRelations.DEFAULT).forGetter(Faction::getRelations),
                    FactionEntityType.CODEC.listOf().fieldOf("entities").forGetter(Faction::getEntityTypes),
                    ResourceLocation.CODEC.optionalFieldOf("activation_advancement", new ResourceLocation(MODID, "default")).forGetter(Faction::getActivationAdvancement)
            ).apply(builder, Faction::new));

    private final ResourceLocation name;
    private final boolean replace;
    private final CompoundTag banner;
    private final FactionRaidConfig raidConfig;
    private final FactionBoostConfig boostConfig;
    private final FactionRelations relations;
    private final List<FactionEntityType> entityTypes;
    private final ResourceLocation activationAdvancement;

    public Faction(ResourceLocation name, boolean replace, CompoundTag banner, FactionRaidConfig raidConfig, FactionBoostConfig boostConfig, FactionRelations relations, List<FactionEntityType> entityTypes, ResourceLocation activationAdvancement) {
        this.name = name;
        this.replace = replace;
        this.banner = banner;
        this.raidConfig = raidConfig;
        this.boostConfig = boostConfig;
        this.relations = relations;
        this.entityTypes = entityTypes;
        this.activationAdvancement = activationAdvancement;
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

    public List<Pair<FactionEntityType, Integer>> getWeightMap(){
        return entityTypes.stream().map(factionEntityType -> new Pair<>(factionEntityType, factionEntityType.getWeight())).toList();
    }
    public List<Pair<FactionEntityType, Integer>> getWeightMapForWave(int wave){
        return entityTypes.stream().filter(factionEntityType -> factionEntityType.getMinimumWave() <= wave && factionEntityType.getMaximumWave() >= wave && !factionEntityType.hasRank(MOUNT)).map(factionEntityType -> new Pair<>(factionEntityType, factionEntityType.getWeight())).collect(Collectors.toList());
    }
    public List<Pair<FactionEntityType, Integer>> getWeightMapForRank(FactionEntityType.FactionRank rank){
        return entityTypes.stream().filter(factionEntityType -> factionEntityType.hasRank(rank)).map(factionEntityType -> new Pair<>(factionEntityType, factionEntityType.getWeight())).toList();
    }

    public ItemStack getBannerInstance() {
        ItemStack itemstack = ItemStack.of(banner);
        return itemstack;
    }

    public void makeBannerHolder(Mob mobEntity) {
        mobEntity.setItemSlot(EquipmentSlot.HEAD, getBannerInstance());
        mobEntity.setDropChance(EquipmentSlot.HEAD, 2.0F);
    }
}
