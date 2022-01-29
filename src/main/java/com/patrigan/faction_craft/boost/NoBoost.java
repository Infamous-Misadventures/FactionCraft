package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

import static com.patrigan.faction_craft.boost.Boost.BoostType.SPECIAL;

public class NoBoost extends Boost {

    public static final NoBoost INSTANCE = new NoBoost(BoostProviders.NO_BOOST::get);
    public static final Codec<NoBoost> CODEC = Codec.unit(INSTANCE);

    public NoBoost(Supplier<? extends Serializer<?>> dispatcherGetter)
    {
        super(dispatcherGetter);
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
