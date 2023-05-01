package com.patrigan.faction_craft.boost;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.util.GeneralUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

import java.util.List;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.boost.BoostProviders.FACTION_MOUNT;

public class FactionMountBoost extends Boost {
    public static final Codec<FactionMountBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("mount").forGetter(FactionMountBoost::getEntityTypeLocation),
            Rarity.CODEC.fieldOf("rarity").forGetter(FactionMountBoost::getRarity)
    ).apply(instance, FactionMountBoost::new));

    private final ResourceLocation entityTypeLocation;
    private final Rarity rarity;

    public FactionMountBoost(ResourceLocation entityTypeLocation, Rarity rarity) {
        super();
        this.entityTypeLocation = entityTypeLocation;
        this.rarity = rarity;
    }

    public ResourceLocation getEntityTypeLocation() {
        return entityTypeLocation;
    }

    @Override
    public Codec<? extends Boost> getCodec() {
        return CODEC;
    }

    @Override
    public BoostType getType() {
        return BoostType.MOUNT;
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public int apply(LivingEntity livingEntity) {
        if(livingEntity.isPassenger()){
            return 0;
        }
        if(livingEntity.level instanceof ServerLevel level && livingEntity instanceof Mob mob) {
            FactionEntity cap = FactionEntityHelper.getFactionEntityCapability(mob);
            if (cap.getFaction() == null) {
                return 0;
            } else {
                List<Pair<FactionEntityType, Integer>> weightMap = cap.getFaction().getWeightMapForRank(FactionEntityType.FactionRank.MOUNT).stream().filter(pair -> pair.getFirst().getEntityType().equals(entityTypeLocation)).collect(Collectors.toList());
                Raider raiderCap = RaiderHelper.getRaiderCapability(mob);
                if (raiderCap.hasActiveRaid()) {
                    weightMap = weightMap.stream()
                            .filter(pair -> pair.getFirst().canSpawnInWave(raiderCap.getWave()))
                            .filter(pair -> pair.getFirst().getMaxSpawned(raiderCap.getRaid().getRaidersInWave(raiderCap.getWave()).size()) > raiderCap.getRaid().getRaidersInWave(raiderCap.getWave()).stream()
                                    .filter(entity -> FactionEntityHelper.getFactionEntityCapability(entity).getFactionEntityType() != null && FactionEntityHelper.getFactionEntityCapability(entity).getFactionEntityType().equals(pair.getFirst()))
                                    .count())
                            .collect(Collectors.toList());
                }
                if(weightMap.isEmpty()){
                    return 0;
                }
                FactionEntityType randomEntry = GeneralUtils.getRandomEntry(weightMap, mob.getRandom());
                Entity entity = randomEntry.createEntity(level, cap.getFaction(), livingEntity.blockPosition(), false, MobSpawnType.JOCKEY);
                if(raiderCap.hasActiveRaid() && entity instanceof Mob mountMob){
                    raiderCap.getRaid().addWaveMob(raiderCap.getWave(), mountMob, true);
                }
                if(entity instanceof AbstractHorse horse) {
                    horse.setTamed(true);
                    horse.setOwnerUUID(mob.getUUID());
                }
                mob.startRiding(entity);
                return randomEntry.getStrength();
            }
        }
        return 0;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return !livingEntity.isPassenger() && factionHasMount(livingEntity);
    }

    private boolean factionHasMount(LivingEntity livingEntity) {
        if(livingEntity instanceof Mob) {
            Mob mob = (Mob) livingEntity;
            FactionEntity cap = FactionEntityHelper.getFactionEntityCapability(mob);
            if(cap.getFaction() == null){
                return false;
            }
            List<Pair<FactionEntityType, Integer>> weightMap = cap.getFaction().getWeightMapForRank(FactionEntityType.FactionRank.MOUNT).stream().filter(pair -> pair.getFirst().getEntityType().equals(entityTypeLocation)).collect(Collectors.toList());
            Raider raiderCap = RaiderHelper.getRaiderCapability(mob);
            if (raiderCap.hasActiveRaid()) {
                weightMap = weightMap.stream()
                        .filter(pair -> pair.getFirst().canSpawnInWave(raiderCap.getWave()))
                        .filter(pair -> pair.getFirst().getMaxSpawned(raiderCap.getRaid().getRaidersInWave(raiderCap.getWave()).size()) > raiderCap.getRaid().getRaidersInWave(raiderCap.getWave()).stream()
                                .filter(entity -> FactionEntityHelper.getFactionEntityCapability(entity).getFactionEntityType() != null && FactionEntityHelper.getFactionEntityCapability(entity).getFactionEntityType().equals(pair.getFirst()))
                                .count())
                        .collect(Collectors.toList());
            }
            return !weightMap.isEmpty();
        }
        return false;
    }
}
