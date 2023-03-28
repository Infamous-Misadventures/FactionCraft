package com.patrigan.faction_craft.tags;

import com.patrigan.faction_craft.FactionCraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class EntityTags {
    public static final TagKey<EntityType<?>> CAN_USE_MELEE_WEAPON = tag("can_use_melee_weapon");
    public static final TagKey<EntityType<?>> CAN_USE_BOW = tag("can_use_bow");
    public static final TagKey<EntityType<?>> CAN_USE_CROSSBOW = tag("can_use_crossbow");
    public static final TagKey<EntityType<?>> CAN_USE_TRIDENT = tag("can_use_trident");
    public static final TagKey<EntityType<?>> CAN_USE_SHIELD = tag("can_use_shield");
    public static final TagKey<EntityType<?>> CAN_WEAR_ARMOR = tag("can_wear_armor");


    private static TagKey<EntityType<?>> tag(String name) {
        return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation( FactionCraft.MODID, name));
    }

    public static void register() {
        // NOOP
    }
}
