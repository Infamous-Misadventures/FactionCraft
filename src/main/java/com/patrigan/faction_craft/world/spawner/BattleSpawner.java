package com.patrigan.faction_craft.world.spawner;

import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.Factions;
import com.patrigan.faction_craft.raid.target.FactionBattleRaidTarget;
import com.patrigan.faction_craft.util.GeneralUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class BattleSpawner implements ISpecialSpawner {
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
            this.nextTick += FactionCraftConfig.BATTLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get() + random.nextInt(FactionCraftConfig.BATTLE_VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get());
            if (pLevel.getDayTime() >= FactionCraftConfig.BATTLE_DAYTIME_BEFORE_SPAWNING.get() && pLevel.isDay()) {
               if (random.nextFloat() <= FactionCraftConfig.BATTLE_SPAWN_CHANCE_ON_SPAWN_ATTEMPT.get()) {
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
                              return spawnFactionBattle(pLevel, random, blockpos$mutable);
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

   public static int spawnFactionBattle(ServerWorld pLevel, Random random, BlockPos blockpos) {
      Faction faction1 = Factions.getRandomFactionWithEnemies(pLevel, random);
      List<Faction> enemies = faction1.getRelations().getEnemies().stream().filter(resourceLocation -> !FactionCraftConfig.DISABLED_FACTIONS.get().contains(resourceLocation.toString())).map(Factions::getFaction).filter(faction -> faction.getRelations().getEnemies().contains(faction1.getName())).collect(Collectors.toList());
      if(enemies.isEmpty()) {
         return 0;
      }else{
         Faction faction2 = GeneralUtils.getRandomItem(enemies, random);
         IRaidManager cap = RaidManagerHelper.getRaidManagerCapability(pLevel);
         FactionBattleRaidTarget factionBattleRaidTarget = new FactionBattleRaidTarget(blockpos, faction1, faction2, pLevel);
         if(cap == null){
            return 0;
         }
         cap.createRaid(Arrays.asList(faction1, faction2), factionBattleRaidTarget);
         return 1;
      }
   }

}
