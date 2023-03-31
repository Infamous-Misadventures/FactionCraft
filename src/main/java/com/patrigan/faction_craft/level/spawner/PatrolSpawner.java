package com.patrigan.faction_craft.level.spawner;

import com.mojang.datafixers.util.Pair;
import com.patrigan.faction_craft.capabilities.patroller.Patroller;
import com.patrigan.faction_craft.capabilities.patroller.PatrollerHelper;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.faction.EntityWeightMapProperties;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.registry.Factions;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.util.GeneralUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

import static net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES;

public class PatrolSpawner implements CustomSpawner {
   private int nextTick;

   public int tick(ServerLevel pLevel, boolean pSpawnHostiles, boolean pSpawnPassives) {
      if (!pSpawnHostiles) {
         return 0;
      } else if (FactionCraftConfig.DISABLE_FACTION_PATROLS.get()) {
         return 0;
      } else {
         RandomSource random = pLevel.random;
         --this.nextTick;
         if (this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick += FactionCraftConfig.PATROL_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get() + random.nextInt(FactionCraftConfig.PATROL_VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get());
            if (pLevel.getDayTime() >= FactionCraftConfig.PATROL_DAYTIME_BEFORE_SPAWNING.get() && pLevel.isDay()) {
               if (random.nextFloat() <= FactionCraftConfig.PATROL_SPAWN_CHANCE_ON_SPAWN_ATTEMPT.get()) {
                  return 0;
               } else {
                  int j = pLevel.players().size();
                  if (j < 1) {
                     return 0;
                  } else {
                     Player playerentity = pLevel.players().get(random.nextInt(j));
                     if (playerentity.isSpectator()) {
                        return 0;
                     } else if (pLevel.isCloseToVillage(playerentity.blockPosition(), 2)) {
                        return 0;
                     } else {
                        int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        BlockPos.MutableBlockPos blockpos$mutable = playerentity.blockPosition().mutable().move(k, 0, l);
                        if (!pLevel.hasChunksAt(blockpos$mutable.getX() - 10, blockpos$mutable.getY() - 10, blockpos$mutable.getZ() - 10, blockpos$mutable.getX() + 10, blockpos$mutable.getY() + 10, blockpos$mutable.getZ() + 10)) {
                           return 0;
                        } else {
                           Holder<Biome> holder = pLevel.getBiome(blockpos$mutable);
                           if (holder.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                              return 0;
                           } else {
                              Faction faction = Factions.getRandomFaction(pLevel, random);
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

   public static int spawnPatrol(ServerLevel pLevel, RandomSource random, Faction faction, BlockPos blockpos) {
      BlockPos.MutableBlockPos mutableBlockPos = blockpos.mutable();
      int i1 = 0;
      int j1 = (int)Math.ceil(pLevel.getCurrentDifficultyAt(mutableBlockPos).getEffectiveDifficulty()) + 1;

      for(int k1 = 0; k1 < j1; ++k1) {
         ++i1;
         mutableBlockPos.setY(pLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutableBlockPos).getY());
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

   private static boolean spawnPatrolMember(ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, boolean pLeader, Faction faction) {
      BlockState blockstate = pLevel.getBlockState(pPos);
      EntityWeightMapProperties entityWeightMapProperties = new EntityWeightMapProperties();
      if(pLeader) {
         entityWeightMapProperties.addAllowedRank(FactionEntityType.FactionRank.CAPTAIN);
      } else {
         entityWeightMapProperties.addAllowedRank(FactionEntityType.FactionRank.SOLDIER);
      }
      List<Pair<FactionEntityType, Integer>> weightMap = faction.getWeightMap(entityWeightMapProperties);
      if(weightMap.isEmpty()) {
         return false;
      }
      FactionEntityType factionEntityType = GeneralUtils.getRandomEntry(weightMap, pRandom);
      EntityType<? extends Mob> entityType = (EntityType<? extends Mob>) ENTITY_TYPES.getValue(factionEntityType.getEntityType());
      if (!NaturalSpawner.isValidEmptySpawnBlock(pLevel, pPos, blockstate, blockstate.getFluidState(), entityType)) {
         return false;
      } else if (!(pLevel.getBrightness(LightLayer.BLOCK, pPos) <= 8 && pLevel.getDifficulty() != Difficulty.PEACEFUL && Mob.checkMobSpawnRules(entityType, pLevel, MobSpawnType.PATROL, pPos, pRandom))) {
         return false;
      } else {
         Mob entity = (Mob) factionEntityType.createEntity(pLevel, faction, pPos, pLeader, MobSpawnType.PATROL);
         if (entity != null) {
            Patroller patrollerCap = PatrollerHelper.getPatrollerCapability(entity);
            if (pLeader) {
               patrollerCap.setPatrolLeader(true);
               patrollerCap.findPatrolTarget();
            }
            patrollerCap.setPatrolling(true);
            return true;
         } else {
            return false;
         }
      }
   }
}
