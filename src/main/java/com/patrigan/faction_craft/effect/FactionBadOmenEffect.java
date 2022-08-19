package com.patrigan.faction_craft.effect;

import com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteractionHelper;
import com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteraction;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.target.VillageRaidTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.Difficulty;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class FactionBadOmenEffect extends MobEffect {
    public FactionBadOmenEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @SubscribeEvent
    public static void onPotionRemoveEvent(MobEffectEvent.Remove event) {
        if (event.getEffect() instanceof FactionBadOmenEffect && !event.getEntity().level.isClientSide()
                && event.getEntity() instanceof Player player) {
            FactionInteraction cap = FactionInteractionHelper.getFactionInteractionCapability(player);
            if (cap != null) {
                cap.clearBadOmenFactions();
            }
        }
    }

    /**
     * checks if Potion effect is ready to be applied this tick.
     */
    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity instanceof ServerPlayer player && !pLivingEntity.isSpectator()) {
            ServerLevel serverlevel = player.getLevel();
            if (serverlevel.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }

            if (serverlevel.isVillage(pLivingEntity.blockPosition())) {
                RaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(serverlevel);
                raidManagerCapability.createBadOmenRaid(new VillageRaidTarget(player.blockPosition(), serverlevel), player);
            }
        }
    }
}
