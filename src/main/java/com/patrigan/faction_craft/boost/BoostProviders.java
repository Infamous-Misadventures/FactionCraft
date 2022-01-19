package com.patrigan.faction_craft.boost;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import static com.patrigan.faction_craft.FactionCraft.BOOST_DISPATCHER;
import static com.patrigan.faction_craft.FactionCraft.MODID;

public class BoostProviders {
    public static final DeferredRegister<Boost.Serializer<?>> BOOST_PROVIDERS = BOOST_DISPATCHER.makeDeferredRegister(MODID);

    public static final RegistryObject<Boost.Serializer<NoBoost>> NO_BOOST = BOOST_PROVIDERS.register("no_boost", () -> new Boost.Serializer<>(NoBoost.CODEC));
    public static final RegistryObject<Boost.Serializer<AttributeBoost>> ATTRIBUTE = BOOST_PROVIDERS.register("attribute_boost", () -> new Boost.Serializer<>(AttributeBoost.CODEC));
    public static final RegistryObject<Boost.Serializer<WearArmorBoost>> WEAR_ARMOR = BOOST_PROVIDERS.register("wear_armor_boost", () -> new Boost.Serializer<>(WearArmorBoost.CODEC));
    public static final RegistryObject<Boost.Serializer<WearHandsBoost>> WEAR_HANDS = BOOST_PROVIDERS.register("wear_hands_boost", () -> new Boost.Serializer<>(WearHandsBoost.CODEC));
    public static final RegistryObject<Boost.Serializer<DaylightProtectionBoost>> DAYLIGHT_PROTECTION = BOOST_PROVIDERS.register("daylight_protection_boost", () -> new Boost.Serializer<>(DaylightProtectionBoost.CODEC));
    public static final RegistryObject<Boost.Serializer<MountBoost>> MOUNT = BOOST_PROVIDERS.register("mount_boost", () -> new Boost.Serializer<>(MountBoost.CODEC));
}
