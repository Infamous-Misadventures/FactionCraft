package com.patrigan.faction_craft.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.patrigan.faction_craft.config.FactionCraftConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.patrigan.faction_craft.FactionCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class RenderEvents {

    @SubscribeEvent
    public static void onEntityRender(RenderLivingEvent.Post event) {
        if (FactionCraftConfig.ENABLE_EXPERIMENTAL_FEATURES.get() && event.getEntity() instanceof Mob mob && mob.isAlive() && mob.getUseItem().canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK)) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(((Minecraft.getInstance().player.getViewYRot(event.getPartialTick()) % 360)*-1)-180F));
            poseStack.translate(mob.getBbWidth()/2, mob.getBbHeight() + 0.5, mob.getBbWidth()/2);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            Minecraft.getInstance().getItemRenderer().renderStatic(event.getEntity(), mob.getUseItem(), ItemTransforms.TransformType.HEAD, mob.isLeftHanded(), poseStack, event.getMultiBufferSource(), mob.getLevel(), event.getPackedLight(), LivingEntityRenderer.getOverlayCoords(mob, 0.0F), mob.getId());
            poseStack.popPose();
        }
    }

}
