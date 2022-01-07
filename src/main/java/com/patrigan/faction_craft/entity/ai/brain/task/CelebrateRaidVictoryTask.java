package com.patrigan.faction_craft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.entity.ai.brain.task.MoveToSkylightTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CelebrateRaidVictoryTask extends Task<VillagerEntity> {
   @Nullable
   private Raid currentRaid;

   public CelebrateRaidVictoryTask(int p_i50370_1_, int p_i50370_2_) {
      super(ImmutableMap.of(), p_i50370_1_, p_i50370_2_);
   }

   protected boolean checkExtraStartConditions(ServerWorld pLevel, VillagerEntity pOwner) {
      BlockPos blockpos = pOwner.blockPosition();
      IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(pLevel);
      this.currentRaid = raidManagerCapability.getRaidAt(blockpos);
      return this.currentRaid != null && this.currentRaid.isVictory() && MoveToSkylightTask.hasNoBlocksAbove(pLevel, pOwner, blockpos);
   }

   protected boolean canStillUse(ServerWorld pLevel, VillagerEntity pEntity, long pGameTime) {
      return this.currentRaid != null && !this.currentRaid.isStopped();
   }

   protected void stop(ServerWorld pLevel, VillagerEntity pEntity, long pGameTime) {
      this.currentRaid = null;
      pEntity.getBrain().updateActivityFromSchedule(pLevel.getDayTime(), pLevel.getGameTime());
   }

   protected void tick(ServerWorld pLevel, VillagerEntity pOwner, long pGameTime) {
      Random random = pOwner.getRandom();
      if (random.nextInt(100) == 0) {
         pOwner.playCelebrateSound();
      }

      if (random.nextInt(200) == 0 && MoveToSkylightTask.hasNoBlocksAbove(pLevel, pOwner, pOwner.blockPosition())) {
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
      CompoundNBT compoundnbt = itemstack1.getOrCreateTagElement("Explosion");
      List<Integer> list = Lists.newArrayList();
      list.add(pColor.getFireworkColor());
      compoundnbt.putIntArray("Colors", list);
      compoundnbt.putByte("Type", (byte)FireworkRocketItem.Shape.BURST.getId());
      CompoundNBT compoundnbt1 = itemstack.getOrCreateTagElement("Fireworks");
      ListNBT listnbt = new ListNBT();
      CompoundNBT compoundnbt2 = itemstack1.getTagElement("Explosion");
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