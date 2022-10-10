package com.patrigan.faction_craft.boost;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.IFactionEntity;
import com.patrigan.faction_craft.capabilities.raider.IRaider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.util.GeneralUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.boost.BoostProviders.FACTION_MOUNT;
import static com.patrigan.faction_craft.boost.BoostProviders.MOUNT;
import static net.minecraftforge.registries.ForgeRegistries.ENTITIES;

public class FactionMountBoost extends Boost {
    public static final Codec<FactionMountBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("mount").forGetter(FactionMountBoost::getEntityTypeLocation),
            Rarity.CODEC.fieldOf("rarity").forGetter(FactionMountBoost::getRarity)
    ).apply(instance, FactionMountBoost::new));

    private final ResourceLocation entityTypeLocation;
    private final Rarity rarity;

    public FactionMountBoost(ResourceLocation entityTypeLocation, Rarity rarity) {
        super(FACTION_MOUNT);
        this.entityTypeLocation = entityTypeLocation;
        this.rarity = rarity;
    }

    public ResourceLocation getEntityTypeLocation() {
        return entityTypeLocation;
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
        if(livingEntity.level instanceof ServerWorld && livingEntity instanceof MobEntity) {
            ServerWorld level = (ServerWorld) livingEntity.level;
            MobEntity mob = (MobEntity) livingEntity;
            IFactionEntity cap = FactionEntityHelper.getFactionEntityCapability(mob);
            if (cap.getFaction() == null) {
                return 0;
            } else {
                List<Pair<FactionEntityType, Integer>> weightMap = cap.getFaction().getWeightMapForRank(FactionEntityType.FactionRank.MOUNT).stream().filter(pair -> pair.getFirst().getEntityType().equals(entityTypeLocation)).collect(Collectors.toList());
                IRaider raiderCap = RaiderHelper.getRaiderCapability(mob);
                if (raiderCap != null && raiderCap.hasActiveRaid()) {
                    weightMap = weightMap.stream()
                            .filter(pair -> pair.getFirst().canSpawnInWave(raiderCap.getWave()))
                            .filter(pair -> pair.getFirst().getMaximumSpawned() > raiderCap.getRaid().getRaidersInWave(raiderCap.getWave()).stream()
                                    .filter(entity -> FactionEntityHelper.getFactionEntityCapability(entity).getFactionEntityType() != null && FactionEntityHelper.getFactionEntityCapability(entity).getFactionEntityType().equals(pair.getFirst()))
                                    .count())
                            .collect(Collectors.toList());
                }
                if(weightMap.isEmpty()){
                    return 0;
                }
                FactionEntityType randomEntry = GeneralUtils.getRandomEntry(weightMap, mob.getRandom());
                Entity entity = randomEntry.createEntity(level, cap.getFaction(), livingEntity.blockPosition(), false, SpawnReason.JOCKEY);
                if(raiderCap != null && raiderCap.hasActiveRaid() && entity instanceof MobEntity){
                    raiderCap.getRaid().addWaveMob(raiderCap.getWave(), (MobEntity) entity, true);
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
        if(livingEntity instanceof MobEntity) {
            MobEntity mob = (MobEntity) livingEntity;
            IFactionEntity cap = FactionEntityHelper.getFactionEntityCapability(mob);
            if(cap.getFaction() == null){
                return false;
            }
            List<Pair<FactionEntityType, Integer>> weightMap = cap.getFaction().getWeightMapForRank(FactionEntityType.FactionRank.MOUNT).stream().filter(pair -> pair.getFirst().getEntityType().equals(entityTypeLocation)).collect(Collectors.toList());
            IRaider raiderCap = RaiderHelper.getRaiderCapability(mob);
            if (raiderCap != null && raiderCap.hasActiveRaid()) {
                weightMap = weightMap.stream()
                        .filter(pair -> pair.getFirst().canSpawnInWave(raiderCap.getWave()))
                        .filter(pair -> pair.getFirst().getMaximumSpawned() > raiderCap.getRaid().getRaidersInWave(raiderCap.getWave()).stream()
                                .filter(entity -> FactionEntityHelper.getFactionEntityCapability(entity).getFactionEntityType() != null && FactionEntityHelper.getFactionEntityCapability(entity).getFactionEntityType().equals(pair.getFirst()))
                                .count())
                        .collect(Collectors.toList());
            }
            return !weightMap.isEmpty();
        }
        return false;
    }
}
