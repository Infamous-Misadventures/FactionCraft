package com.patrigan.faction_craft.capabilities.patroller;

import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.IFactionEntity;
import com.patrigan.faction_craft.capabilities.factioninteraction.FactionInteractionHelper;
import com.patrigan.faction_craft.capabilities.factioninteraction.IFactionInteraction;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import com.patrigan.faction_craft.effect.Effects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class PatrollerEvents {

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event){
        LivingEntity livingEntity = event.getEntityLiving();
        if(!livingEntity.level.isClientSide() && livingEntity instanceof MobEntity) {
            PatrollerHelper.getPatrollerCapabilityLazy((MobEntity) livingEntity).ifPresent(cap -> {
            });
        }
    }
    @SubscribeEvent
    public static void onLivingDeathEvent(LivingDeathEvent event){
        LivingEntity livingEntity = event.getEntityLiving();
        Entity sourceEntity = event.getSource().getEntity();
        if(!livingEntity.level.isClientSide() && livingEntity instanceof MobEntity && sourceEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) sourceEntity;
            PatrollerHelper.getPatrollerCapabilityLazy((MobEntity) livingEntity).ifPresent(cap -> {
                if(cap.isPatrolLeader()){
                    EffectInstance effectinstance1 = playerEntity.getEffect(Effects.FACTION_BAD_OMEN);
                    int i = 1;
                    if (effectinstance1 != null) {
                        i += effectinstance1.getAmplifier();
                        playerEntity.removeEffectNoUpdate(Effects.FACTION_BAD_OMEN);
                    } else {
                        --i;
                    }

                    i = MathHelper.clamp(i, 0, FactionCraftConfig.RAID_MAX_FACTIONS.get()-1);
                    EffectInstance effectinstance = new EffectInstance(Effects.FACTION_BAD_OMEN, 120000, i, false, false, true);
                    if (!FactionCraftConfig.DISABLE_FACTION_RAIDS.get()) {
                        IFactionInteraction factionInteractionCapability = FactionInteractionHelper.getFactionInteractionCapability(playerEntity);
                        if(factionInteractionCapability.getBadOmenFactions().size() < FactionCraftConfig.RAID_MAX_FACTIONS.get()) {
                            IFactionEntity factionEntityCapability = FactionEntityHelper.getFactionEntityCapability((MobEntity) livingEntity);
                            if (factionEntityCapability.getFaction() != null){
                                factionInteractionCapability.addBadOmenFaction(factionEntityCapability.getFaction());
                            }
                        }
                        playerEntity.addEffect(effectinstance);
                    }
                }
            });
        }
    }

}
