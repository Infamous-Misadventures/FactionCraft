package com.patrigan.faction_craft.entity.ai.goal;


import com.patrigan.faction_craft.capabilities.raider.IRaider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraftforge.common.util.LazyOptional;

public class RaidOpenDoorGoal extends OpenDoorGoal {
    public RaidOpenDoorGoal(MobEntity p_i51284_2_) {
        super(p_i51284_2_, false);
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        LazyOptional<IRaider> raiderCapabilityLazy = RaiderHelper.getRaiderCapabilityLazy(this.mob);
        if(!raiderCapabilityLazy.isPresent()){
            return false;
        }
        IRaider raiderCapability = RaiderHelper.getRaiderCapability(this.mob);
        return super.canUse() && raiderCapability.hasActiveRaid();
    }
}
