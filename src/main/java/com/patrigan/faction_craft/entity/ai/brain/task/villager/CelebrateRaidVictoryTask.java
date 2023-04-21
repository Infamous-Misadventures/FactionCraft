package com.patrigan.faction_craft.entity.ai.brain.task.villager;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.behavior.MoveToSkySeeingSpot;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CelebrateRaidVictoryTask extends Behavior<Villager> {
   @Nullable
   private Raid currentRaid;

   public CelebrateRaidVictoryTask(int p_i50370_1_, int p_i50370_2_) {
      super(ImmutableMap.of(), p_i50370_1_, p_i50370_2_);
   }

   protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
      BlockPos blockpos = pOwner.blockPosition();
      RaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(pLevel);
      this.currentRaid = raidManagerCapability.getRaidAt(blockpos);
      return this.currentRaid != null && this.currentRaid.isVictory() && MoveToSkySeeingSpot.hasNoBlocksAbove(pLevel, pOwner, blockpos);
   }

   protected boolean canStillUse(ServerLevel pLevel, Villager pEntity, long pGameTime) {
      return this.currentRaid != null && !this.currentRaid.isStopped();
   }

   protected void stop(ServerLevel pLevel, Villager pEntity, long pGameTime) {
      this.currentRaid = null;
      pEntity.getBrain().updateActivityFromSchedule(pLevel.getDayTime(), pLevel.getGameTime());
   }

   protected void tick(ServerLevel pLevel, Villager pOwner, long pGameTime) {
      RandomSource random = pOwner.getRandom();
      if (random.nextInt(100) == 0) {
         pOwner.playCelebrateSound();
      }

      if (random.nextInt(200) == 0 && MoveToSkySeeingSpot.hasNoBlocksAbove(pLevel, pOwner, pOwner.blockPosition())) {
         DyeColor dyecolor = Util.getRandom(DyeColor.values(), random);
         int i = random.nextInt(3);
         ItemStack itemstack = this.getFirework(dyecolor, i);
         FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(pOwner.level, pOwner, pOwner.getX(), pOwner.getEyeY(), pOwner.getZ(), itemstack);
         pOwner.level.addFreshEntity(fireworkrocketentity);
      }

   }

   private ItemStack getFirework(DyeColor pColor, int pFlightTime) {
      ItemStack itemstack = new ItemStack(Items.FIREWORK_ROCKET, 1);
      ItemStack itemstack1 = new ItemStack(Items.FIREWORK_STAR);
      CompoundTag compoundnbt = itemstack1.getOrCreateTagElement("Explosion");
      List<Integer> list = Lists.newArrayList();
      list.add(pColor.getFireworkColor());
      compoundnbt.putIntArray("Colors", list);
      compoundnbt.putByte("Type", (byte)FireworkRocketItem.Shape.BURST.getId());
      CompoundTag compoundnbt1 = itemstack.getOrCreateTagElement("Fireworks");
      ListTag listnbt = new ListTag();
      CompoundTag compoundnbt2 = itemstack1.getTagElement("Explosion");
      if (compoundnbt2 != null) {
         listnbt.add(compoundnbt2);
      }

      compoundnbt1.putByte("Flight", (byte)pFlightTime);
      if (!listnbt.isEmpty()) {
         compoundnbt1.put("Explosions", listnbt);
      }

      return itemstack;
   }
}