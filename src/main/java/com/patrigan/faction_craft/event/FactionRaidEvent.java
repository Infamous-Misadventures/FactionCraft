package com.patrigan.faction_craft.event;

import com.patrigan.faction_craft.raid.Raid;
import net.minecraftforge.eventbus.api.Event;

public abstract class FactionRaidEvent extends Event {

        private final Raid raid;

    public FactionRaidEvent(Raid raid) {
        this.raid = raid;
    }

    public Raid getRaid() {
        return raid;
    }

    public static class Wave extends FactionRaidEvent{
        public Wave(Raid raid) {
            super(raid);
        }
    }

    public static class Victory extends FactionRaidEvent{
        public Victory(Raid raid) {
            super(raid);
        }
    }

    public static class Defeat extends FactionRaidEvent{
        public Defeat(Raid raid) {
            super(raid);
        }
    }
}
