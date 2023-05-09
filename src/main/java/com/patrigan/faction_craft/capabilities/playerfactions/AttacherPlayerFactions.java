package com.patrigan.faction_craft.capabilities.playerfactions;

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

public class AttacherPlayerFactions {

    private static class PlayerFactionsProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static final ResourceLocation IDENTIFIER = new ResourceLocation(MODID, "player_factions");
        private final PlayerFactions backend;
        private final LazyOptional<PlayerFactions> optionalData;

        public PlayerFactionsProvider() {
            backend = new PlayerFactions();
            optionalData = LazyOptional.of(() -> backend);
        }


        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            return ModCapabilities.PLAYER_FACTIONS_CAPABILITY.orEmpty(cap, this.optionalData);
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
            final PlayerFactionsProvider provider = new PlayerFactionsProvider();
            event.addCapability(PlayerFactionsProvider.IDENTIFIER, provider);
        }
    }
}
