package com.patrigan.faction_craft.capabilities.raidmanager;

import com.google.common.collect.Maps;
import com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteraction;
import com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteractionHelper;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.effect.ModMobEffects;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.Raid;
import com.patrigan.faction_craft.raid.target.RaidTarget;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.RAID_MANAGER_CAPABILITY;
import static com.patrigan.faction_craft.config.FactionCraftConfig.RAID_MAX_FACTIONS;

public class RaidManager implements INBTSerializable<CompoundTag> {
    private final Map<Integer, Raid> raidMap = Maps.newHashMap();
    private final ServerLevel level;
    private int nextAvailableID = 1;
    private int tick;

    public RaidManager(ServerLevel level) {
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

    public static boolean canJoinRaid(Mob pRaider, Raid pRaid) {
        if (pRaider != null && pRaid != null && pRaid.getLevel() != null) {
            return pRaider.isAlive() && pRaider.getNoActionTime() <= 2400 && pRaider.level.dimensionType() == pRaid.getLevel().dimensionType();
        } else {
            return false;
        }
    }

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

    public Raid createRaid(List<Faction> factions, RaidTarget raidTarget) {
        if (FactionCraftConfig.DISABLE_FACTION_RAIDS.get()) {
            return null;
        } else {
            Raid raid = this.getRaidAt(raidTarget.getTargetBlockPos());
            if(raid == null) {
                raid = new Raid(this.getUniqueId(), factions, this.level, raidTarget);
                if (!this.raidMap.containsKey(raid.getId())) {
                    this.raidMap.put(raid.getId(), raid);
                }
            }
            return raid;
        }
    }

    public Raid createBadOmenRaid(RaidTarget raidTarget, ServerPlayer player) {
        if (FactionCraftConfig.DISABLE_FACTION_RAIDS.get()) {
            return null;
        } else {
            Raid raid = this.getRaidAt(raidTarget.getTargetBlockPos());
            FactionInteraction cap = FactionInteractionHelper.getFactionInteractionCapability(player);
            List<Faction> badOmenFactions = cap.getBadOmenFactions();
            if(raid == null) {
                raid = createRaid(new ArrayList<>(badOmenFactions), raidTarget);
                clearBadOmen(cap, player, raid, true);
            }else if (raid.getFactions().size() <= RAID_MAX_FACTIONS.get()) {
                if(raid.getFactions().size() + badOmenFactions.size() <= RAID_MAX_FACTIONS.get()){
                    raid.addFactions(badOmenFactions);
                }else{
                    for (Faction badOmenFaction : badOmenFactions) {
                        if (raid.getFactions().size() < RAID_MAX_FACTIONS.get()){
                            raid.addFaction(badOmenFaction);
                        }
                    }
                }
                clearBadOmen(cap, player, raid, true);
            }else{
                clearBadOmen(cap, player, raid, false);
            }
            return raid;
        }
    }

    private void clearBadOmen(FactionInteraction cap, ServerPlayer player, Raid raid, boolean contributed) {
        cap.clearBadOmenFactions();
        player.removeEffect(ModMobEffects.FACTION_BAD_OMEN.get());
        player.connection.send(new ClientboundEntityEventPacket(player, (byte)43));
        if (contributed && !raid.hasFirstWaveSpawned()) {
            player.awardStat(Stats.RAID_TRIGGER);
            CriteriaTriggers.BAD_OMEN.trigger(player);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        if (RAID_MANAGER_CAPABILITY == null) {
            return new CompoundTag();
        } else {
            CompoundTag tag = new CompoundTag();
            tag.putInt("NextAvailableID", this.nextAvailableID);
            tag.putInt("Tick", this.tick);
            ListTag listnbt = new ListTag();

            for(Raid raid : this.raidMap.values()) {
                CompoundTag compoundnbt = new CompoundTag();
                raid.save(compoundnbt);
                listnbt.add(compoundnbt);
            }

            tag.put("Raids", listnbt);
            return tag;
        }
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.nextAvailableID = tag.getInt("NextAvailableID");
        this.tick = tag.getInt("Tick");
        ListTag listnbt = tag.getList("Raids", 10);

        for(int i = 0; i < listnbt.size(); ++i) {
            CompoundTag compoundnbt = listnbt.getCompound(i);
            Raid raid = new Raid(this.level, compoundnbt);
            this.raidMap.put(raid.getId(), raid);
        }
    }
}
