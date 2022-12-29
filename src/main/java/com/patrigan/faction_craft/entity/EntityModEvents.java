package com.patrigan.faction_craft.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.stream.Collectors;

import static com.patrigan.faction_craft.FactionCraft.MODID;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_KNOCKBACK;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MODID)
public class EntityModEvents {

    @SubscribeEvent
    public static void onEntityAttributeModificationEvent(EntityAttributeModificationEvent event){
        List<EntityType<? extends LivingEntity>> entitiesWithoutAttack = event.getTypes().stream().filter(entityType -> !event.has(entityType, ATTACK_DAMAGE)).collect(Collectors.toList());
        entitiesWithoutAttack.forEach(entityType -> event.add(entityType, ATTACK_DAMAGE, 0));
        List<EntityType<? extends LivingEntity>> entitiesWithoutKnockback = event.getTypes().stream().filter(entityType -> !event.has(entityType, ATTACK_KNOCKBACK)).collect(Collectors.toList());
        entitiesWithoutKnockback.forEach(entityType -> event.add(entityType, ATTACK_KNOCKBACK, 0));
    }
}
