package com.patrigan.faction_craft.faction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class FactionRaidConfig {
    public static final float DEFAULT_MOBS_FRACTION = 0.7F;
    public static final FactionRaidConfig DEFAULT = new FactionRaidConfig("event.minecraft.raid", "event.minecraft.raid.victory", "event.minecraft.raid.defeat", DEFAULT_MOBS_FRACTION);

    public static final Codec<FactionRaidConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Codec.STRING.optionalFieldOf("name_alt", "event.minecraft.raid").forGetter(FactionRaidConfig::getName),
                    Codec.STRING.optionalFieldOf("victory_alt", "event.minecraft.raid.victory").forGetter(FactionRaidConfig::getVictoryAlt),
                    Codec.STRING.optionalFieldOf("defeat_alt", "event.minecraft.raid.defeat").forGetter(FactionRaidConfig::getDefeatAlt),
                    Codec.FLOAT.optionalFieldOf("mobs_fraction", DEFAULT_MOBS_FRACTION).forGetter(FactionRaidConfig::getMobsFraction)
            ).apply(builder, FactionRaidConfig::new));

    private final String name;
    private final String victoryAlt;
    private final String defeatAlt;
    private final ITextComponent victoryComponent;
    private final ITextComponent defeatComponent;
    private final ITextComponent raidBarNameComponent;
    private final ITextComponent raidBarVictoryComponent;
    private final ITextComponent raidBarDefeatComponent;
    private final float mobsFraction;


    public FactionRaidConfig(String name, String victoryAlt, String defeatAlt, float mobsFraction) {
        this.name = name;
        this.victoryAlt = victoryAlt;
        this.defeatAlt = defeatAlt;
        this.victoryComponent = new TranslationTextComponent(defeatAlt);
        this.defeatComponent = new TranslationTextComponent(victoryAlt);
        this.raidBarNameComponent = new TranslationTextComponent(name);
        this.raidBarVictoryComponent = raidBarNameComponent.copy().append(" - ").append(victoryComponent);
        this.raidBarDefeatComponent = raidBarNameComponent.copy().append(" - ").append(defeatComponent);
        this.mobsFraction = mobsFraction;
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

    public ITextComponent getRaidBarNameComponent() {
        return raidBarNameComponent;
    }

    public ITextComponent getRaidBarVictoryComponent() {
        return raidBarVictoryComponent;
    }

    public ITextComponent getRaidBarDefeatComponent() {
        return raidBarDefeatComponent;
    }

    public float getMobsFraction() {
        return mobsFraction;
    }
}
