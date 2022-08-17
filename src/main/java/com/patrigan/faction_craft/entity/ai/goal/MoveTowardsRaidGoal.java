package com.patrigan.faction_craft.entity.ai.goal;

import com.patrigan.faction_craft.capabilities.raider.IRaider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;

import java.util.EnumSet;

public class MoveTowardsRaidGoal<T extends MobEntity> extends Goal {
    private final T mob;

    public MoveTowardsRaidGoal(T p_i50323_1_) {
        this.mob = p_i50323_1_;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
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
        return raiderCapability != null && this.mob instanceof CreatureEntity
                && this.mob.getTarget() == null
                && !this.mob.isVehicle() && raiderCapability.hasActiveRaid()
                && !raiderCapability.getRaid().isOver()
                && !((ServerWorld)this.mob.level).isVillage(this.mob.blockPosition());
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        IRaider raiderCapability = RaiderHelper.getRaiderCapability(this.mob);
        return raiderCapability != null &&  raiderCapability.hasActiveRaid() && this.mob.getTarget() == null && !raiderCapability.getRaid().isOver() && this.mob.level instanceof ServerWorld && !((ServerWorld)this.mob.level).isVillage(this.mob.blockPosition());
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        IRaider raiderCapability = RaiderHelper.getRaiderCapability(this.mob);
        if (raiderCapability.hasActiveRaid()) {
            Raid raid = raiderCapability.getRaid();
//            if (this.mob.tickCount % 20 == 0) {
//                this.recruitNearby(raid);
//            }

            if (this.mob.getNavigation().isDone()) {
                Vector3d vector3d = RandomPositionGenerator.getPosTowards((CreatureEntity) this.mob, 15, 4, Vector3d.atBottomCenterOf(raid.getCenter()));
                if (vector3d != null) {
                    this.mob.getNavigation().moveTo(vector3d.x, vector3d.y, vector3d.z, 1.0D);
                }
            }
        }
    }

//    private void recruitNearby(Raid pRaid) {
//        if (pRaid.isActive()) {
//            Set<MobEntity> set = Sets.newHashSet();
//            List<MobEntity> list = this.mob.level.getEntitiesOfClass(MobEntity.class, this.mob.getBoundingBox().inflate(16.0D), (p_220742_1_) -> {
//                return !p_220742_1_.hasActiveRaid() && RaidManager.canJoinRaid(p_220742_1_, pRaid);
//            });
//            set.addAll(list);
//
//            for(MobEntity abstractraiderentity : set) {
//                pRaid.joinRaid(pRaid.getGroupsSpawned(), abstractraiderentity, (BlockPos)null, true);
//            }
//        }
//    }
}