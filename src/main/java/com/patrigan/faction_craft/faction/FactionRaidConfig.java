package com.patrigan.faction_craft.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.Optional;

public class FactionRaidConfig {
    public static final float DEFAULT_MOBS_FRACTION = 0.7F;
    public static final FactionRaidConfig DEFAULT = new FactionRaidConfig(false, "event.minecraft.raid", "event.minecraft.raid.victory", "event.minecraft.raid.defeat", DEFAULT_MOBS_FRACTION, Optional.of(SoundEvents.RAID_HORN), null, Optional.of(SoundEvents.RAID_HORN));

    public static final Codec<FactionRaidConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.BOOL.optionalFieldOf("enabled", true).forGetter(FactionRaidConfig::isEnabled),
                    Codec.STRING.optionalFieldOf("name_alt", "event.minecraft.raid").forGetter(FactionRaidConfig::getName),
                    Codec.STRING.optionalFieldOf("victory_alt", "event.minecraft.raid.victory").forGetter(FactionRaidConfig::getVictoryAlt),
                    Codec.STRING.optionalFieldOf("defeat_alt", "event.minecraft.raid.defeat").forGetter(FactionRaidConfig::getDefeatAlt),
                    Codec.FLOAT.optionalFieldOf("mobs_fraction", DEFAULT_MOBS_FRACTION).forGetter(FactionRaidConfig::getMobsFraction),
                    SoundEvent.CODEC.optionalFieldOf("wave_sound").forGetter(FactionRaidConfig::getWaveSoundEvent),
                    SoundEvent.CODEC.optionalFieldOf("victory_sound").forGetter(FactionRaidConfig::getVictorySoundEvent),
                    SoundEvent.CODEC.optionalFieldOf("defeat_sound").forGetter(FactionRaidConfig::getDefeatSoundEvent)
            ).apply(builder, FactionRaidConfig::new));

    private final boolean enabled;
    private final String name;
    private final String victoryAlt;
    private final String defeatAlt;
    private final Component victoryComponent;
    private final Component defeatComponent;
    private final Component raidBarNameComponent;
    private final Component raidBarVictoryComponent;
    private final Component raidBarDefeatComponent;
    private final float mobsFraction;
    private final Optional<SoundEvent> waveSoundEvent;
    private final Optional<SoundEvent> victorySoundEvent;
    private final Optional<SoundEvent> defeatSoundEvent;

    public FactionRaidConfig(boolean enabled, String name, String victoryAlt, String defeatAlt, float mobsFraction, Optional<SoundEvent> waveSoundEvent, Optional<SoundEvent> victorySoundEvent, Optional<SoundEvent> defeatSoundEvent) {
        this.enabled = enabled;
        this.name = name;
        this.victoryAlt = victoryAlt;
        this.defeatAlt = defeatAlt;
        this.victoryComponent = Component.translatable (victoryAlt);
        this.defeatComponent = Component.translatable (defeatAlt);
        this.raidBarNameComponent = Component.translatable (name);
        this.raidBarVictoryComponent = raidBarNameComponent.copy().append(" - ").append(victoryComponent);
        this.raidBarDefeatComponent = raidBarNameComponent.copy().append(" - ").append(defeatComponent);
        this.mobsFraction = mobsFraction;
        this.waveSoundEvent = waveSoundEvent;
        this.victorySoundEvent = victorySoundEvent;
        this.defeatSoundEvent = defeatSoundEvent;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public String getVictoryAlt() {
        return victoryAlt;
    }

    public String getDefeatAlt() {
        return defeatAlt;
    }

    public Component getRaidBarNameComponent() {
        return raidBarNameComponent;
    }

    public Component getRaidBarVictoryComponent() {
        return raidBarVictoryComponent;
    }

    public Component getRaidBarDefeatComponent() {
        return raidBarDefeatComponent;
    }

    public float getMobsFraction() {
        return mobsFraction;
    }

    public Optional<SoundEvent> getWaveSoundEvent() {
        return waveSoundEvent;
    }

    public Optional<SoundEvent> getVictorySoundEvent() {
        return victorySoundEvent;
    }

    public Optional<SoundEvent> getDefeatSoundEvent() {
        return defeatSoundEvent;
    }
}
