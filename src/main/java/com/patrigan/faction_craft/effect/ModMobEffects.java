package com.patrigan.faction_craft.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class ModMobEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    public static final RegistryObject<FactionBadOmenEffect> FACTION_BAD_OMEN = MOB_EFFECTS.register("faction_bad_omen", () -> new FactionBadOmenEffect(MobEffectCategory.BENEFICIAL, 10044730));
}
