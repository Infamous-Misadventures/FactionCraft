package com.patrigan.faction_craft.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

public class RegistryDispatcher<DTYPE extends IForgeRegistryEntry<DTYPE>, DISPATCHABLES>
{
    /**
     * Call in your mod constructor (or any time before the RegistryEvent.NewRegistry event fires, really)
     * @param modBus Your mod's mod bus from FMLJavaModLoadingContext.get().getModEventBus();
     * @param registryClass the .class object for this registry (deferred registers will use this class to find the correct registry)
     *		The type of this is genargified with an unchecked cast, so don't use the wrong class object here
     * @param registryID the id of this registry, e.g. "yourmod:cheeses"
     * @param extraSettings Access to the registry builder to apply extra settings like .disableSave() and .disableSync() if needed
     * 		This consumer is called after .setName() and .setType() are called but before .create()
     */
    public static <DTYPE extends Dispatcher<DTYPE, ? extends DISPATCHABLES>, DISPATCHABLES extends Dispatchable<DTYPE>> RegistryDispatcher<DTYPE, DISPATCHABLES> makeDispatchForgeRegistry(
            final IEventBus modBus,
            final Class<?> registryClass,
            final ResourceLocation registryID,
            final Consumer<RegistryBuilder<DTYPE>> extraSettings)
    {
        Class<DTYPE> genargifiedClass = (Class<DTYPE>) registryClass;
        RegistryWrapper<DTYPE> wrapper = new RegistryWrapper<>();
        Codec<DTYPE> dispatcherCodec = ResourceLocation.CODEC.xmap(
                id -> wrapper.get().getValue(id),
                DTYPE::getRegistryName);
        Codec<DISPATCHABLES> dispatchedCodec = dispatcherCodec.dispatch(dispatchable->dispatchable.getDispatcher(), dispatcher->dispatcher.getSubCodec());
        Consumer<RegistryEvent.NewRegistry> newRegistryListener = event ->
        {
            RegistryBuilder<DTYPE> builder = new RegistryBuilder<DTYPE>()
                    .setName(registryID)
                    .setType(genargifiedClass);
            extraSettings.accept(builder);
            IForgeRegistry<DTYPE> registry = builder.create();
            wrapper.setRegistry(registry);
        };

        modBus.addListener(newRegistryListener);

        return new RegistryDispatcher<>(wrapper, genargifiedClass, dispatcherCodec, dispatchedCodec);
    }

    private final Supplier<IForgeRegistry<DTYPE>> registryGetter;
    private final Class<DTYPE> registryClass;
    private final Codec<DTYPE> dispatcherCodec;
    private final Codec<DISPATCHABLES> dispatchedCodec;

    public RegistryDispatcher(Supplier<IForgeRegistry<DTYPE>> registryGetter, Class<DTYPE> registryClass, Codec<DTYPE> dispatcherCodec, Codec<DISPATCHABLES> dispatchedCodec)
    {
        this.registryGetter = registryGetter;
        this.registryClass = registryClass;
        this.dispatcherCodec = dispatcherCodec;
        this.dispatchedCodec = dispatchedCodec;
    }

    /**
     * Gets the forge registry for the dispatchers. The forge registry is created and initialized in the NewRegistry event;
     * if getForgeRegistry is called before this, it will return null (so it's not safe to use this to make DeferredRegisters, generally)
     * @return The forge registry for the dispatchers, or null if the forge registry hasn't been initialized in the NewRegistry event yet
     */
    public IForgeRegistry<DTYPE> getForgeRegistry()
    { return this.registryGetter.get(); }

    /**
     * Gets the class used by the forge registry
     * @return the class used by the forge registry
     */
    public Class<DTYPE> getRegistryClass()
    { return this.registryClass; }

    /**
     * Gets the codec for the DTYPE class
     * This could be used for serializing ids of dispatcher types but it's generally less useful than the dispatched codec
     * @return the DTYPE class's codec
     */
    public Codec<DTYPE> getDispatcherCodec()
    { return this.dispatcherCodec; }

    /**
     * Gets the codec for the DISPATCHABLES class
     * You can use this codec to read a json containing the dispatcher type + extra data
     * @return the DISPATCHABLES class's codec
     */
    public Codec<DISPATCHABLES> getDispatchedCodec()
    { return this.dispatchedCodec; }

    /**
     * Creates a Deferred Register for the forge registry for the dispatchers (does not subscribe it to the mod bus)
     * @param modid Your modid
     * @return An unsubscribed Deferred Register
     */
    public DeferredRegister<DTYPE> makeDeferredRegister(String modid)
    { return DeferredRegister.create(this.getRegistryClass(), modid); }

    /**
     * Class for the dispatchers/serializers
     * Extend this to make your class useable with makeDispatchRegistry
     */
    public static abstract class Dispatcher<DTYPE extends IForgeRegistryEntry<DTYPE>, P> extends ForgeRegistryEntry<DTYPE>
    {
        private final Codec<P> subCodec;
        public Codec<P> getSubCodec() { return this.subCodec; }

        public Dispatcher(Codec<P> subCodec)
        {
            this.subCodec = subCodec;
        }
    }

    /**
     * Base class for the dispatched objects
     * Instances of subclasses of this can be deserialized from jsons, etc
     */
    public static abstract class Dispatchable<DTYPE>
    {
        private final Supplier<? extends DTYPE> dispatcherGetter;
        public DTYPE getDispatcher() { return this.dispatcherGetter.get(); }

        public Dispatchable(Supplier<? extends DTYPE> dispatcherGetter)
        {
            this.dispatcherGetter = dispatcherGetter;
        }
    }

    /**
     * Registry wrapper for forge registries
     * The usual pattern is to wait until the registry event before creating them
     * This lets us static final init a field for the registry
     */
    private static class RegistryWrapper<T extends IForgeRegistryEntry<T>> implements Supplier<IForgeRegistry<T>>
    {
        private IForgeRegistry<T> registry = null;

        @Override
        public IForgeRegistry<T> get()
        {
            return this.registry;
        }

        public void setRegistry(IForgeRegistry<T> value)
        {
            this.registry = value;
        }

    }
}