package com.patrigan.faction_craft.capabilities.raider;

import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.nbt.CompoundNBT;

public interface IRaider {

    Raid getRaid();

    void setRaid(Raid raid);

    boolean hasActiveRaid();

    int getWave();

    void setWave(int pWave);

    void setCanJoinRaid(boolean b);

    int getTicksOutsideRaid();

    void setTicksOutsideRaid(int i);

    boolean isWaveLeader();

    void setWaveLeader(boolean waveLeader);

    CompoundNBT save(CompoundNBT tag);

    void load(CompoundNBT tag);
}
