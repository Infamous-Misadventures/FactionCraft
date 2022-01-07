package com.patrigan.faction_craft.capabilities.raidmanager;

import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.raid.Raid;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Map;


public class RaidManagerStorage implements Capability.IStorage<IRaidManager> {

    public static final String RAID_MANAGER_KEY = "RaidManager";

    @Override
    public INBT writeNBT(Capability<IRaidManager> capability, IRaidManager instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag = instance.save(tag);
        return tag;
    }

    @Override
    public void readNBT(Capability<IRaidManager> capability, IRaidManager instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.load(tag);
    }
}