package com.patrigan.faction_craft.effect;

import com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteractionHelper;
import com.patrigan.faction_craft.capabilities.factioninteraction.IFactionInteraction;
import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.raid.target.VillageRaidTarget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
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
                raidManagerCapability.createBadOmenRaid(new VillageRaidTarget(player.blockPosition(), serverworld), player);
            }
        }
    }

    @SubscribeEvent
    public static void onPotionRemoveEvent(PotionEvent.PotionRemoveEvent event){
        if(event.getPotion() instanceof FactionBadOmenEffect && !event.getEntityLiving().level.isClientSide()) {
            if(event.getEntityLiving() instanceof PlayerEntity){
                IFactionInteraction cap = FactionInteractionHelper.getFactionInteractionCapability((PlayerEntity) event.getEntityLiving());
                cap.clearBadOmenFactions();
            }
        }
    }
}
