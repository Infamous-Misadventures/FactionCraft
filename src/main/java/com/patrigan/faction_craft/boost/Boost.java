package com.patrigan.faction_craft.boost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.patrigan.faction_craft.FactionCraft;
import com.patrigan.faction_craft.capabilities.appliedboosts.AppliedBoostsHelper;
import com.patrigan.faction_craft.util.RegistryDispatcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.IExtensibleEnum;

import java.util.Map;
import java.util.function.Supplier;

public abstract class Boost extends RegistryDispatcher.Dispatchable<Boost.Serializer<?>> {
    public static final Codec<Boost> CODEC = FactionCraft.BOOST_DISPATCHER.getDispatchedCodec();

    public Boost(Supplier<? extends Serializer<?>> dispatcherGetter) {
        super(dispatcherGetter);
    }

    public abstract BoostType getType();
    public abstract Rarity getRarity();

    public int apply(LivingEntity livingEntity){
        AppliedBoostsHelper.getAppliedBoostsCapabilityLazy(livingEntity).ifPresent(cap -> cap.addAppliedBoost(this));
        return 0;
    }

    public abstract boolean canApply(LivingEntity livingEntity);

    public void updateAIOnJoin(Mob mobEntity){
        // noop
    }

    public CompoundTag save(CompoundTag compoundNBT) {
        ResourceLocation resourceLocation = Boosts.BOOSTS.data.entrySet().stream().filter(entry -> entry.getValue().equals(this)).findFirst().map(Map.Entry::getKey).orElse(new ResourceLocation("empty"));
        compoundNBT.putString("name", resourceLocation.toString());
        return compoundNBT;
    }

    public static Boost load(CompoundTag compoundNBT) {
        ResourceLocation name = new ResourceLocation(compoundNBT.getString("name"));
        return Boosts.getBoost(name);
    }

    public static class Serializer<P extends Boost> extends RegistryDispatcher.Dispatcher<Serializer<?>, P>
    {
        public Serializer(Codec<P> subCodec)
        {
            super(subCodec);
        }
    }

    public enum BoostType implements IExtensibleEnum {
        SPECIAL("special", 999),
        ATTRIBUTE("attribute",999),
        ARMOR("armor", 1),
        MOUNT("mount", 1),
        MAINHAND("mainhand", 1),
        OFFHAND("offhand", 1);
        public static final Codec<BoostType> CODEC = Codec.STRING.flatComapMap(s -> BoostType.byName(s, null), d -> DataResult.success(d.getName()));

        private final String name;
        private final int max;

        BoostType(String name, int max) {
            this.name = name;
            this.max = max;
        }

        public int getMax() {
            return max;
        }

        public String getName() {
            return name;
        }

        public static BoostType byName(String key, BoostType fallBack) {
            for(BoostType boostType : values()) {
                if (boostType.name.equals(key)) {
                    return boostType;
                }
            }

            return fallBack;
        }

        public static BoostType create(String id, String name, int max)
        {
            throw new IllegalStateException("Enum not extended");
        }
    }

    public enum Rarity {
        SUPER_COMMON("super_common", 40),
        COMMON("common", 10),
        UNCOMMON("uncommon", 5),
        RARE("rare", 2),
        VERY_RARE("very_rare", 1),
        NONE("none", 0);
        public static final Codec<Rarity> CODEC = Codec.STRING.flatComapMap(s -> Rarity.byName(s, null), d -> DataResult.success(d.getName()));

        private final String name;
        private final int weight;

        Rarity(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }

        /**
         * Retrieves the weight of Rarity.
         */
        public int getWeight() {
            return this.weight;
        }

        public String getName() {
            return name;
        }

        public static Rarity byName(String key, Rarity fallBack) {
            for(Rarity rarity : values()) {
                if (rarity.name.equals(key)) {
                    return rarity;
                }
            }

            return fallBack;
        }
    }
}
