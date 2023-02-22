package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.patrigan.faction_craft.boost.ai.DiggerBoost;
import com.patrigan.faction_craft.boost.ai.MeleeAttackBoost;
import com.patrigan.faction_craft.util.RegistryDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid=MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class BoostProviders {
    public static RegistryDispatcher<Boost> BOOST_DISPATCHER = RegistryDispatcher.makeDispatchForgeRegistry(
            FMLJavaModLoadingContext.get().getModEventBus(),
            new ResourceLocation(MODID, "boost"),
            Boost::getCodec,
            builder -> builder
                    .disableSaving()
                    .disableSync()
    );

    public static final RegistryObject<Codec<NoBoost>> NO_BOOST = BOOST_DISPATCHER.registry().register("no_boost", () -> NoBoost.CODEC);
    public static final RegistryObject<Codec<AttributeBoost>> ATTRIBUTE = BOOST_DISPATCHER.registry().register("attribute_boost", () -> AttributeBoost.CODEC);
    public static final RegistryObject<Codec<WearArmorBoost>> WEAR_ARMOR = BOOST_DISPATCHER.registry().register("wear_armor_boost", () -> WearArmorBoost.CODEC);
    public static final RegistryObject<Codec<WearHandsBoost>> WEAR_HANDS = BOOST_DISPATCHER.registry().register("wear_hands_boost", () -> WearHandsBoost.CODEC);
    public static final RegistryObject<Codec<DaylightProtectionBoost>> DAYLIGHT_PROTECTION = BOOST_DISPATCHER.registry().register("daylight_protection_boost", () -> DaylightProtectionBoost.CODEC);
    public static final RegistryObject<Codec<MountBoost>> MOUNT = BOOST_DISPATCHER.registry().register("mount_boost", () -> MountBoost.CODEC);
    public static final RegistryObject<Codec<FactionMountBoost>> FACTION_MOUNT = BOOST_DISPATCHER.registry().register("faction_mount_boost", () -> FactionMountBoost.CODEC);
    //AI
    public static final RegistryObject<Codec<MeleeAttackBoost>> MELEE_ATTACK = BOOST_DISPATCHER.registry().register("melee_attack_boost", () -> MeleeAttackBoost.CODEC);
    public static final RegistryObject<Codec<DiggerBoost>> Digger = BOOST_DISPATCHER.registry().register("digger_boost", () -> DiggerBoost.CODEC);
}
