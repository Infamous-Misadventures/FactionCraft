package com.patrigan.faction_craft.faction;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Faction {
    public static final Faction DEFAULT = new Faction(new ResourceLocation("faction/default"), false, new CompoundNBT(), FactionRaidConfig.DEFAULT, FactionBoostConfig.DEFAULT, Collections.emptyList());

    public static final Codec<Faction> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("name").forGetter(Faction::getName),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(data -> data.replace),
                    CompoundNBT.CODEC.fieldOf("banner").forGetter(Faction::getBanner),
                    FactionRaidConfig.CODEC.optionalFieldOf("raid_config", FactionRaidConfig.DEFAULT).forGetter(Faction::getRaidConfig),
                    FactionBoostConfig.CODEC.optionalFieldOf("boosts", FactionBoostConfig.DEFAULT).forGetter(Faction::getBoostConfig),
                    FactionEntityType.CODEC.listOf().fieldOf("entities").forGetter(Faction::getEntityTypes)
            ).apply(builder, Faction::new));

    private final ResourceLocation name;
    private final boolean replace;
    private final CompoundNBT banner;
    private final FactionRaidConfig raidConfig;
    private final FactionBoostConfig boostConfig;
    private final List<FactionEntityType> entityTypes;

    public Faction(ResourceLocation name, boolean replace, CompoundNBT banner, FactionRaidConfig raidConfig, FactionBoostConfig boostConfig, List<FactionEntityType> entityTypes) {
        this.name = name;
        this.replace = replace;
        this.banner = banner;
        this.raidConfig = raidConfig;
        this.boostConfig = boostConfig;
        this.entityTypes = entityTypes;
    }

    public ResourceLocation getName() {
        return name;
    }

    public boolean isReplace() {
        return replace;
    }

    public CompoundNBT getBanner() {
        return banner;
    }

    public FactionRaidConfig getRaidConfig() {
        return raidConfig;
    }

    public FactionBoostConfig getBoostConfig() {
        return boostConfig;
    }

    public List<FactionEntityType> getEntityTypes() {
        return entityTypes;
    }

    public List<Pair<FactionEntityType, Integer>> getWeightMap(){
        return entityTypes.stream().map(factionEntityType -> new Pair<>(factionEntityType, factionEntityType.getWeight())).collect(Collectors.toList());
    }
    public List<Pair<FactionEntityType, Integer>> getWeightMapForWave(int wave){
        return entityTypes.stream().filter(factionEntityType -> factionEntityType.getMinimumWave() <= wave).map(factionEntityType -> new Pair<>(factionEntityType, factionEntityType.getWeight())).collect(Collectors.toList());
    }

    public ItemStack getBannerInstance() {
        ItemStack itemstack = ItemStack.of(banner);
        return itemstack;
    }

    public void makeBannerHolder(MobEntity mobEntity) {
        mobEntity.setItemSlot(EquipmentSlotType.HEAD, getBannerInstance());
        mobEntity.setDropChance(EquipmentSlotType.HEAD, 2.0F);
    }
}
