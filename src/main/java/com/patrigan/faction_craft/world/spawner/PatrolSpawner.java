package com.patrigan.faction_craft.world.spawner;

import com.mojang.datafixers.util.Pair;
import com.patrigan.faction_craft.capabilities.patroller.IPatroller;
import com.patrigan.faction_craft.capabilities.patroller.PatrollerHelper;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.faction.Factions;
import com.patrigan.faction_craft.util.GeneralUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.registries.ForgeRegistries.ENTITIES;

public class PatrolSpawner implements ISpecialSpawner {
   private int nextTick;

   public int tick(ServerWorld pLevel, boolean pSpawnHostiles, boolean pSpawnPassives) {
      if (!pSpawnHostiles) {
         return 0;
      } else if (FactionCraftConfig.DISABLE_FACTION_PATROLS.get()) {
         return 0;
      } else {
         Random random = pLevel.random;
         --this.nextTick;
         if (this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick += FactionCraftConfig.TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get() + random.nextInt(FactionCraftConfig.VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get());
            if (pLevel.getDayTime() >= FactionCraftConfig.DAYTIME_BEFORE_SPAWNING.get() && pLevel.isDay()) {
               if (random.nextFloat() <= FactionCraftConfig.SPAWN_CHANCE_ON_SPAWN_ATTEMPT.get()) {
                  return 0;
               } else {
                  int j = pLevel.players().size();
                  if (j < 1) {
                     return 0;
                  } else {
                     PlayerEntity playerentity = pLevel.players().get(random.nextInt(j));
                     if (playerentity.isSpectator()) {
                        return 0;
                     } else if (pLevel.isCloseToVillage(playerentity.blockPosition(), 2)) {
                        return 0;
                     } else {
                        int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        BlockPos.Mutable blockpos$mutable = playerentity.blockPosition().mutable().move(k, 0, l);
                        if (!pLevel.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)) {
                           return 0;
                        } else {
                           Biome biome = pLevel.getBiome(blockpos$mutable);
                           Biome.Category biome$category = biome.getBiomeCategory();
                           if (biome$category == Biome.Category.MUSHROOM) {
                              return 0;
                           } else {
                              Faction faction = Factions.getRandomFaction(random);
                              return spawnPatrol(pLevel, random, faction, blockpos$mutable);
                           }
                        }
                     }
                  }
               }
            } else {
               return 0;
            }
         }
      }
   }

   public static int spawnPatrol(ServerWorld pLevel, Random random, Faction faction, BlockPos blockpos) {
      BlockPos.Mutable mutableBlockPos = blockpos.mutable();
      int i1 = 0;
      int j1 = (int)Math.ceil(pLevel.getCurrentDifficultyAt(mutableBlockPos).getEffectiveDifficulty()) + 1;

      for(int k1 = 0; k1 < j1; ++k1) {
         ++i1;
         mutableBlockPos.setY(pLevel.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutableBlockPos).getY());
         if (k1 == 0) {
            if (!spawnPatrolMember(pLevel, mutableBlockPos, random, true, faction)) {
               break;
            }
         } else {
            spawnPatrolMember(pLevel, mutableBlockPos, random, false, faction);
         }

         mutableBlockPos.setX(mutableBlockPos.getX() + random.nextInt(5) - random.nextInt(5));
         mutableBlockPos.setZ(mutableBlockPos.getZ() + random.nextInt(5) - random.nextInt(5));
      }

      return i1;
   }

   private static boolean spawnPatrolMember(ServerWorld pLevel, BlockPos pPos, Random pRandom, boolean pLeader, Faction faction) {
      BlockState blockstate = pLevel.getBlockState(pPos);
      List<Pair<FactionEntityType, Integer>> weightMap = faction.getWeightMap();
      FactionEntityType factionEntityType = GeneralUtils.getRandomEntry(weightMap, pRandom);
      EntityType<? extends MobEntity> entityType = (EntityType<? extends MobEntity>) ENTITIES.getValue(factionEntityType.getEntityType());
      if (!WorldEntitySpawner.isValidEmptySpawnBlock(pLevel, pPos, blockstate, blockstate.getFluidState(), entityType)) {
         return false;
      } else if (!(pLevel.getBrightness(LightType.BLOCK, pPos) <= 8 && pLevel.getDifficulty() != Difficulty.PEACEFUL && MobEntity.checkMobSpawnRules(entityType, pLevel, SpawnReason.PATROL, pPos, pRandom))) {
         return false;
      } else {
         MobEntity entity = (MobEntity) factionEntityType.createEntity(pLevel, faction, pPos, pLeader);
         if (entity != null) {
            IPatroller patrollerCap = PatrollerHelper.getPatrollerCapability(entity);
            if (pLeader) {
               patrollerCap.setPatrolLeader(true);
               patrollerCap.findPatrolTarget();
            }
            patrollerCap.setPatrolling(true);
            if(net.minecraftforge.common.ForgeHooks.canEntitySpawn(entity, pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), null, SpawnReason.PATROL) == -1) return false;
            entity.finalizeSpawn(pLevel, pLevel.getCurrentDifficultyAt(pPos), SpawnReason.PATROL, null, null);
            pLevel.addFreshEntityWithPassengers(entity.getRootVehicle());
            return true;
         } else {
            return false;
         }
      }
   }
}
