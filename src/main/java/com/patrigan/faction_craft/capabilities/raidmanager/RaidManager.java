package com.patrigan.faction_craft.capabilities.raidmanager;


import com.google.common.collect.Maps;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.target.RaidTarget;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.world.server.ServerWorld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class RaidManager implements IRaidManager {
    private final Map<Integer, Raid> raidMap = Maps.newHashMap();
    private final ServerWorld level;
    private int nextAvailableID = 1;
    private int tick;

    public RaidManager() {
        level = null;
    }

    public RaidManager(ServerWorld level) {
        this.level = level;
    }

    public void tick() {
        ++this.tick;
        Iterator<Raid> iterator = this.raidMap.values().iterator();

        while(iterator.hasNext()) {
            Raid raid = iterator.next();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
                raid.stop();
            }

            if (raid.isStopped()) {
                iterator.remove();
            } else {
                raid.tick();
            }
        }

//        DebugPacketSender.sendRaids(this.level, this.raidMap.values());
    }

    @Override
    public Map<Integer, Raid> getRaids() {
        return raidMap;
    }

    private int getUniqueId() {
        return ++this.nextAvailableID;
    }

    public Raid getRaidAt(BlockPos blockPos) {
        return this.getNearbyRaid(blockPos, 9216);
    }

    public Raid getNearbyRaid(BlockPos blockPos, int distance) {
        Raid raid = null;
        double d0 = distance;

        for(Raid raid1 : this.raidMap.values()) {
            double d1 = raid1.getCenter().distSqr(blockPos);
            if (raid1.isActive() && d1 < d0) {
                raid = raid1;
                d0 = d1;
            }
        }

        return raid;
    }

    public Raid createRaid(Faction faction, RaidTarget raidTarget) {
        return createRaid(Arrays.asList(faction), raidTarget);
    }

    @Override
    public Raid createRaid(Collection<Faction> factions, RaidTarget raidTarget) {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            return null;
        } else {
            Raid raid = this.getRaidAt(raidTarget.getTargetBlockPos());
            if(raid == null) {
                raid = new Raid(this.getUniqueId(), factions, this.level, raidTarget);
            }
            boolean flag = false;
            if (!raid.isStarted()) {
                if (!this.raidMap.containsKey(raid.getId())) {
                    this.raidMap.put(raid.getId(), raid);
                }
                flag = true;
//            } else if (raid.getBadOmenLevel() < raid.getMaxBadOmenLevel()) {
//                flag = true;
//            } else {
//                pPlayer.removeEffect(Effects.BAD_OMEN);
//                pPlayer.connection.send(new SEntityStatusPacket(pPlayer, (byte)43));
            }

//            if (flag) {
//                raid.absorbBadOmen(pPlayer);
//                pPlayer.connection.send(new SEntityStatusPacket(pPlayer, (byte)43));
//                if (!raid.hasFirstWaveSpawned()) {
//                    pPlayer.awardStat(Stats.RAID_TRIGGER);
//                    CriteriaTriggers.BAD_OMEN.trigger(pPlayer);
//                }
//            }

            return raid;
        }
    }


    public void load(CompoundNBT tag) {
        this.nextAvailableID = tag.getInt("NextAvailableID");
        this.tick = tag.getInt("Tick");
        ListNBT listnbt = tag.getList("Raids", 10);

        for(int i = 0; i < listnbt.size(); ++i) {
            CompoundNBT compoundnbt = listnbt.getCompound(i);
            Raid raid = new Raid(this.level, compoundnbt);
            this.raidMap.put(raid.getId(), raid);
        }

    }

    public CompoundNBT save(CompoundNBT pCompound) {
        pCompound.putInt("NextAvailableID", this.nextAvailableID);
        pCompound.putInt("Tick", this.tick);
        ListNBT listnbt = new ListNBT();

        for(Raid raid : this.raidMap.values()) {
            CompoundNBT compoundnbt = new CompoundNBT();
            raid.save(compoundnbt);
            listnbt.add(compoundnbt);
        }

        pCompound.put("Raids", listnbt);
        return pCompound;
    }

}
