package com.patrigan.faction_craft.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class Effects {
    public static MobEffect FACTION_BAD_OMEN;

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onEffectsRegistry(final RegistryEvent.Register<MobEffect> effectRegistryEvent) {
            FACTION_BAD_OMEN = new FactionBadOmenEffect(MobEffectCategory.BENEFICIAL, 10044730).setRegistryName(modLoc("faction_bad_omen"));
            effectRegistryEvent.getRegistry().registerAll(
                    FACTION_BAD_OMEN
            );
        }

        private static ResourceLocation modLoc(String name) {
            return new ResourceLocation(MODID, name);
        }
    }
}
