package com.patrigan.faction_craft.level.spawner;

import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.registry.Factions;
import com.patrigan.faction_craft.raid.target.FactionBattleRaidTarget;
import com.patrigan.faction_craft.util.GeneralUtils;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BattleSpawner implements CustomSpawner {
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
            this.nextTick += FactionCraftConfig.BATTLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get() + random.nextInt(FactionCraftConfig.BATTLE_VARIABLE_TICK_DELAY_BETWEEN_SPAWN_ATTEMPTS.get());
            if (pLevel.getDayTime() >= FactionCraftConfig.BATTLE_DAYTIME_BEFORE_SPAWNING.get() && pLevel.isDay()) {
               if (random.nextFloat() <= FactionCraftConfig.BATTLE_SPAWN_CHANCE_ON_SPAWN_ATTEMPT.get()) {
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

   public static int spawnFactionBattle(ServerLevel pLevel, RandomSource random, BlockPos blockpos) {
      Faction faction1 = Factions.getRandomFactionWithEnemies(pLevel, random, faction -> faction.getRaidConfig().isEnabled());
      if(faction1 == null) {
         return 0;
      }
      List<Faction> enemies = faction1.getRelations().getEnemies().stream().filter(resourceLocation -> !FactionCraftConfig.DISABLED_FACTIONS.get().contains(resourceLocation.toString())).map(Factions::getFaction).filter(faction -> faction.getRelations().getEnemies().contains(faction1.getName())).collect(Collectors.toList());
      if(enemies.isEmpty()) {
         return 0;
      }else{
         Faction faction2 = GeneralUtils.getRandomItem(enemies, random);
         RaidManager cap = RaidManagerHelper.getRaidManagerCapability(pLevel);
         FactionBattleRaidTarget factionBattleRaidTarget = new FactionBattleRaidTarget(blockpos, faction1, faction2, pLevel);
         if(cap == null){
            return 0;
         }
         cap.createRaid(Arrays.asList(faction1, faction2), factionBattleRaidTarget);
         return 1;
      }
   }

}
