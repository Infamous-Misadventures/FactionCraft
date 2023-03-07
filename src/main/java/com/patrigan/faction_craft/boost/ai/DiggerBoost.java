package com.patrigan.faction_craft.boost.ai;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.patrigan.faction_craft.boost.Boost;
import com.patrigan.faction_craft.entity.ai.goal.FactionDigGoal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;

import static com.patrigan.faction_craft.boost.Boost.BoostType.SPECIAL;
import static com.patrigan.faction_craft.boost.Boost.Rarity.NONE;

public class DiggerBoost extends Boost {

    public static final Codec<DiggerBoost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("strength_adjustment", 1).forGetter(DiggerBoost::getStrengthAdjustment),
            Codec.BOOL.optionalFieldOf("requires_tool", true).forGetter(DiggerBoost::isRequiresTool),
            Codec.BOOL.optionalFieldOf("requires_proper_tool", true).forGetter(DiggerBoost::isRequiresProperTool),
            Codec.BOOL.optionalFieldOf("off_hand_tool", false).forGetter(DiggerBoost::isOffHandTool)
    ).apply(instance, DiggerBoost::new));

    private final int strengthAdjustment;
    private final boolean requiresTool;
    private final boolean requiresProperTool;
    private final boolean offHandTool;

    public DiggerBoost(int strengthAdjustment, boolean requiresTool, boolean requiresProperTool, boolean offHandTool) {
        this.strengthAdjustment = strengthAdjustment;
        this.requiresTool = requiresTool;
        this.requiresProperTool = requiresProperTool;
        this.offHandTool = offHandTool;
    }

    public int getStrengthAdjustment() {
        return strengthAdjustment;
    }

    public boolean isRequiresTool() {
        return requiresTool;
    }

    public boolean isRequiresProperTool() {
        return requiresProperTool;
    }

    public boolean isOffHandTool() {
        return offHandTool;
    }

    public EquipmentSlot getHand() {
        return offHandTool ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
    }

    @Override
    public Codec<? extends Boost> getCodec() {
        return CODEC;
    }

    @Override
    public BoostType getType() {
        return SPECIAL;
    }

    @Override
    public Rarity getRarity() {
        return NONE;
    }

    @Override
    public int apply(LivingEntity livingEntity) {
        if (!canApply(livingEntity)) {
            return 0;
        }
        if (livingEntity instanceof Mob mob) {
            applyAIChanges(mob);
        }
        super.apply(livingEntity);
        return strengthAdjustment;
    }

    @Override
    public boolean canApply(LivingEntity livingEntity) {
        return livingEntity instanceof Mob;
    }

    @Override
    public void applyAIChanges(Mob mobEntity) {
        if (mobEntity instanceof PathfinderMob pathfinder) {
            FactionDigGoal meleeGoal = new FactionDigGoal(pathfinder, requiresTool, requiresProperTool, getHand());
            mobEntity.goalSelector.addGoal(2, meleeGoal);
        }
    }
}
