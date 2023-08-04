package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

import static com.patrigan.faction_craft.boost.BoostProviders.MOUNT;
import static net.minecraftforge.registries.ForgeRegistries.ENTITIES;

public class MountBoost extends Boost {
    public static final Codec<MountBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("mount").forGetter(MountBoost::getEntityTypeLocation),
            Codec.INT.optionalFieldOf("strength_adjustment", 1).forGetter(MountBoost::getStrengthAdjustment),
            Rarity.CODEC.fieldOf("rarity").forGetter(MountBoost::getRarity)
    ).apply(instance, MountBoost::new));

    private final ResourceLocation entityTypeLocation;
    private final int strengthAdjustment;
    private final Rarity rarity;

    public MountBoost(ResourceLocation entityTypeLocation, int strengthAdjustment, Rarity rarity) {
        super(MOUNT);
        this.entityTypeLocation = entityTypeLocation;
        this.strengthAdjustment = strengthAdjustment;
        this.rarity = rarity;
    }

    public ResourceLocation getEntityTypeLocation() {
        return entityTypeLocation;
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
        if(livingEntity.isPassenger()){
            return 0;
        }
        if(livingEntity.level instanceof ServerLevel) {
            ServerLevel level = (ServerLevel) livingEntity.level;
            Entity mount = ENTITIES.getValue(entityTypeLocation).create(level);
            if (mount != null) {
                Vec3 pos = livingEntity.position();
                mount.setPos(pos.x, pos.y, pos.z);
                if (mount instanceof Mob) {
                    Mob mobMount = (Mob) mount;
                    if (net.minecraftforge.common.ForgeHooks.canEntitySpawn(mobMount, level, mount.blockPosition().getX(), mount.blockPosition().getY(), mount.blockPosition().getZ(), null, MobSpawnType.EVENT) == -1)
                        return 0;
                    mobMount.finalizeSpawn(level, level.getCurrentDifficultyAt(mount.blockPosition()), MobSpawnType.EVENT, null, null);
                }
                super.apply(livingEntity);
                level.addFreshEntity(mount);
                if(mount instanceof AbstractHorse horse) {
                    horse.setTamed(true);
                    horse.setOwnerUUID(livingEntity.getUUID());
                }
                livingEntity.startRiding(mount);
                return strengthAdjustment;
            }
        }
        return 0;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return !livingEntity.isPassenger();
    }

}
