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
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class FactionBadOmenEffect extends MobEffect {
    public FactionBadOmenEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    /**
     * checks if Potion effect is ready to be applied this tick.
     */
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity instanceof ServerPlayer && !pLivingEntity.isSpectator()) {
            ServerPlayer player = (ServerPlayer)pLivingEntity;
            ServerLevel serverworld = player.getLevel();
            if (serverworld.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }

            if (serverworld.isVillage(pLivingEntity.blockPosition())) {
                RaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(serverworld);
                raidManagerCapability.createBadOmenRaid(new VillageRaidTarget(player.blockPosition(), serverworld), player);
            }
        }
    }

    @SubscribeEvent
    public static void onPotionRemoveEvent(PotionEvent.PotionRemoveEvent event){
        if(event.getPotion() instanceof FactionBadOmenEffect && !event.getEntityLiving().level.isClientSide()) {
            if(event.getEntityLiving() instanceof Player){
                FactionInteraction cap = FactionInteractionHelper.getFactionInteractionCapability((Player) event.getEntityLiving());
                cap.clearBadOmenFactions();
            }
        }
    }
}
