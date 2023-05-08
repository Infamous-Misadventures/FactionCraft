package com.patrigan.faction_craft.capabilities.savedfactiondata;

import com.patrigan.faction_craft.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;

import static com.patrigan.faction_craft.FactionCraft.MODID;

public class AttacherSavedFactionData {

    private static class SavedFactionDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static final ResourceLocation IDENTIFIER = new ResourceLocation(MODID, "faction_data");
        private final SavedFactionData backend;
        private final LazyOptional<SavedFactionData> optionalData;

        public SavedFactionDataProvider() {
            backend = new SavedFactionData();
            optionalData = LazyOptional.of(() -> backend);
        }


        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            return ModCapabilities.SAVED_FACTION_DATA_CAPABILITY.orEmpty(cap, this.optionalData);
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

    public static void attach(final AttachCapabilitiesEvent<Level> event) {
        Level level = event.getObject();
        if (level instanceof ServerLevel && level.dimension().equals(Level.OVERWORLD)) {
            final SavedFactionDataProvider provider = new SavedFactionDataProvider();
            event.addCapability(SavedFactionDataProvider.IDENTIFIER, provider);
        }
    }
}
