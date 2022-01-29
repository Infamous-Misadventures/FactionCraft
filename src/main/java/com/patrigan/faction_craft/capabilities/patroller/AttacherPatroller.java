package com.patrigan.faction_craft.capabilities.patroller;

import com.patrigan.faction_craft.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class AttacherPatroller {

    private static class PatrollerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static final ResourceLocation IDENTIFIER = new ResourceLocation(MODID, "patroller");
        private final Patroller backend;
        private final LazyOptional<Patroller> optionalData;

        public PatrollerProvider(Mob entity) {
            backend = new Patroller(entity);
            optionalData = LazyOptional.of(() -> backend);
        }

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            return ModCapabilities.PATROLLER_CAPABILITY.orEmpty(cap, this.optionalData);
        }

        @Override
        public CompoundTag serializeNBT() {
            return this.backend.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.backend.deserializeNBT(nbt);
        }
    }

    // attach only to living entities
    public static void attach(final AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof Mob) {
            final AttacherPatroller.PatrollerProvider provider = new AttacherPatroller.PatrollerProvider((Mob) entity);
            event.addCapability(AttacherPatroller.PatrollerProvider.IDENTIFIER, provider);
        }
    }
}
