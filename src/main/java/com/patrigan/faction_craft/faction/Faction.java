package com.patrigan.faction_craft.faction;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Faction {
    public static final Faction DEFAULT = new Faction(new ResourceLocation("faction/default"), false, new CompoundNBT(), FactionRaidConfig.DEFAULT, Collections.emptyList());

    public static final Codec<Faction> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    ResourceLocation.CODEC.fieldOf("name").forGetter(Faction::getName),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(data -> data.replace),
                    CompoundNBT.CODEC.fieldOf("banner").forGetter(Faction::getBanner),
                    FactionRaidConfig.CODEC.optionalFieldOf("raid_config", FactionRaidConfig.DEFAULT).forGetter(Faction::getRaidConfig),
                    FactionEntity.CODEC.listOf().fieldOf("entities").forGetter(Faction::getEntities)
            ).apply(builder, Faction::new));

    private final ResourceLocation name;
    private final boolean replace;
    private final CompoundNBT banner;
    private final FactionRaidConfig raidConfig;
    private final List<FactionEntity> entities;

    public Faction(ResourceLocation name, boolean replace, CompoundNBT banner, FactionRaidConfig raidConfig, List<FactionEntity> entities) {
        this.name = name;
        this.replace = replace;
        this.banner = banner;
        this.raidConfig = raidConfig;
        this.entities = entities;
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

    public List<FactionEntity> getEntities() {
        return entities;
    }

    public List<Pair<FactionEntity, Integer>> getWeightMap(){
        return entities.stream().map(factionEntity -> new Pair<>(factionEntity, factionEntity.getWeight())).collect(Collectors.toList());
    }
    public List<Pair<FactionEntity, Integer>> getWeightMapForWave(int wave){
        return entities.stream().filter(factionEntity -> factionEntity.getMinimumWave() <= wave).map(factionEntity -> new Pair<>(factionEntity, factionEntity.getWeight())).collect(Collectors.toList());
    }

    public ItemStack getBannerInstance() {
        ItemStack itemstack = ItemStack.of(banner);
        return itemstack;
    }
}
