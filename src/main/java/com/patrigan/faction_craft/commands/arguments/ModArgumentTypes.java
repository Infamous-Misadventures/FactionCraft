package com.patrigan.faction_craft.commands.arguments;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class ModArgumentTypes {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, MODID);


    public static final RegistryObject<SingletonArgumentInfo<FactionArgument>> FACTION = COMMAND_ARGUMENT_TYPES.register("faction",
            () -> ArgumentTypeInfos.registerByClass(FactionArgument.class, SingletonArgumentInfo.contextFree(FactionArgument::new)));

    public static final RegistryObject<SingletonArgumentInfo<FactionEntitySummonArgument>> FACTION_ENTITY_TYPE = COMMAND_ARGUMENT_TYPES.register("faction_entity_type",
            () -> ArgumentTypeInfos.registerByClass(FactionEntitySummonArgument.class, SingletonArgumentInfo.contextFree(FactionEntitySummonArgument::new)));
}
