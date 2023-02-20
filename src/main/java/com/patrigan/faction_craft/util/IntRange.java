package com.patrigan.faction_craft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record IntRange(int min, int max) {
    public static Codec<IntRange> CODEC = getCodec(0, 1000);

    public static Codec<IntRange> getCodec(int minDefault, int maxDefault) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("min", minDefault).forGetter(IntRange::getMin),
                Codec.INT.optionalFieldOf("max", maxDefault).forGetter(IntRange::getMax)
        ).apply(instance, IntRange::new));
    }

    public IntRange {
        if (min > max) {
            throw new IllegalArgumentException("min must be less than or equal to max");
        }
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public boolean isBetween(int value) {
        return value >= min && value <= max;
    }
}
