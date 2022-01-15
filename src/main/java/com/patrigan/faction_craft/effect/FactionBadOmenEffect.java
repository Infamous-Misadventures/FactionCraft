package com.patrigan.faction_craft.effect;

import com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteractionHelper;
import com.patrigan.faction_craft.capabilities.factioninteraction.IFactionInteraction;
import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.raid.target.VillageRaidTarget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class FactionBadOmenEffect extends Effect {
    public FactionBadOmenEffect(EffectType pCategory, int pColor) {
        super(pCategory, pColor);
    }

    /**
     * checks if Potion effect is ready to be applied this tick.
     */
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity instanceof ServerPlayerEntity && !pLivingEntity.isSpectator()) {
            ServerPlayerEntity player = (ServerPlayerEntity)pLivingEntity;
            ServerWorld serverworld = player.getLevel();
            if (serverworld.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }

            if (serverworld.isVillage(pLivingEntity.blockPosition())) {
                IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(serverworld);
                IFactionInteraction factionInteractionCapability = FactionInteractionHelper.getFactionInteractionCapability(player);
                List<Faction> factions = factionInteractionCapability.getBadOmenFactions();
                raidManagerCapability.createRaid(factions, new VillageRaidTarget(player.blockPosition(), serverworld));
            }
        }

    }
}
