package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;

import static com.patrigan.faction_craft.boost.Boost.BoostType.SPECIAL;

public class NoBoost extends Boost {

    public static final NoBoost INSTANCE = new NoBoost();
    public static final Codec<NoBoost> CODEC = Codec.unit(INSTANCE);

    public NoBoost()
    {
        super();
    }

    @Override
    public Codec<? extends Boost> getCodec() {
        return CODEC;
    }

    @Override
    public BoostType getType() {
        return SPECIAL;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.NONE;
    }

    @Override
    public int apply(LivingEntity livingEntity) {
        return 0;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return true;
    }
}
