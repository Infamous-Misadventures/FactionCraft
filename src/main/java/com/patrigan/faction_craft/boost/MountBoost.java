package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.IFactionEntity;
import com.patrigan.faction_craft.capabilities.raider.IRaider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.Set;

import static com.patrigan.faction_craft.boost.BoostProviders.MOUNT;
import static net.minecraftforge.registries.ForgeRegistries.ENTITIES;

public class MountBoost extends Boost {
    public static final Codec<MountBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("mount").forGetter(MountBoost::getEntityTypeLocation),
            Codec.BOOL.optionalFieldOf("taken_from_raid", false).forGetter(MountBoost::isTakenFromRaid),
            Codec.BOOL.optionalFieldOf("share_faction", false).forGetter(MountBoost::isShareFaction),
            Codec.INT.optionalFieldOf("strength_adjustment", 1).forGetter(MountBoost::getStrengthAdjustment),
            Rarity.CODEC.fieldOf("rarity").forGetter(MountBoost::getRarity)
    ).apply(instance, MountBoost::new));

    private final ResourceLocation entityTypeLocation;
    private final boolean takenFromRaid;
    private final boolean shareFaction;
    private final int strengthAdjustment;
    private final Rarity rarity;

    public MountBoost(ResourceLocation entityTypeLocation, boolean takenFromRaid, boolean shareFaction, int strengthAdjustment, Rarity rarity) {
        super(MOUNT);
        this.entityTypeLocation = entityTypeLocation;
        this.takenFromRaid = takenFromRaid;
        this.shareFaction = shareFaction;
        this.strengthAdjustment = strengthAdjustment;
        this.rarity = rarity;
    }

    public ResourceLocation getEntityTypeLocation() {
        return entityTypeLocation;
    }

    public boolean isTakenFromRaid() {
        return takenFromRaid;
    }

    public boolean isShareFaction() {
        return shareFaction;
    }

    public int getStrengthAdjustment() {
        return strengthAdjustment;
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
        if(livingEntity instanceof MobEntity){
            MobEntity mob = (MobEntity) livingEntity;
            IRaider raiderCap = RaiderHelper.getRaiderCapability(mob);
            if(takenFromRaid && raiderCap.hasActiveRaid()){
                Set<MobEntity> raidersInWave = raiderCap.getRaid().getRaidersInWave(raiderCap.getWave());
                Optional<MobEntity> mount = raidersInWave.stream().filter(mobEntity -> mobEntity.getType().getRegistryName().equals(entityTypeLocation) && mobEntity.getPassengers().isEmpty()).findFirst();
                if(mount.isPresent()){
                    mob.startRiding(mount.get());
                    super.apply(livingEntity);
                    return strengthAdjustment;
                }
            }
        }else if(livingEntity.level instanceof ServerWorld) {
            ServerWorld level = (ServerWorld) livingEntity.level;
            Entity mount = ENTITIES.getValue(entityTypeLocation).create(level);
            if (mount != null) {
                Vector3d pos = livingEntity.position();
                mount.setPos(pos.x, pos.y, pos.z);
                if (mount instanceof MobEntity) {
                    MobEntity mobMount = (MobEntity) mount;
                    if (net.minecraftforge.common.ForgeHooks.canEntitySpawn(mobMount, level, mount.blockPosition().getX(), mount.blockPosition().getY(), mount.blockPosition().getZ(), null, SpawnReason.EVENT) == -1)
                        return 0;
                    mobMount.finalizeSpawn(level, level.getCurrentDifficultyAt(mount.blockPosition()), SpawnReason.EVENT, null, null);
                }
                if(shareFaction){
                    copyFaction(livingEntity, mount);
                }
                super.apply(livingEntity);
                level.addFreshEntity(mount);
                livingEntity.startRiding(mount);
                return strengthAdjustment;
            }
        }
        return 0;
    }

    private void copyFaction(LivingEntity livingEntity, Entity mount) {
        if(livingEntity instanceof MobEntity && mount instanceof MobEntity) {
            IFactionEntity entityCapability = FactionEntityHelper.getFactionEntityCapability((MobEntity) livingEntity);
            IFactionEntity mountCapability = FactionEntityHelper.getFactionEntityCapability((MobEntity) mount);
            mountCapability.setFaction(entityCapability.getFaction());
        }
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return !livingEntity.isPassenger() && (!takenFromRaid || canTakeFromRaid(livingEntity));
    }

    private boolean canTakeFromRaid(LivingEntity livingEntity) {
        if(livingEntity instanceof MobEntity) {
            MobEntity mob = (MobEntity) livingEntity;
            IRaider raiderCap = RaiderHelper.getRaiderCapability(mob);
            if(!raiderCap.hasActiveRaid()){
                return true;
            }
            Set<MobEntity> raidersInWave = raiderCap.getRaid().getRaidersInWave(raiderCap.getWave());
            return raidersInWave.stream().anyMatch(mobEntity -> mobEntity.getType().getRegistryName().equals(entityTypeLocation) && mobEntity.getPassengers().isEmpty());//TODO: change to canAddPassenger()
        }
        return true;
    }
}
