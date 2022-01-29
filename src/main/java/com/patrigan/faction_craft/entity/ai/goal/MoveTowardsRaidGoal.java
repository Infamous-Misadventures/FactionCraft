package com.patrigan.faction_craft.entity.ai.goal;

import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.LazyOptional;

import java.util.EnumSet;

public class MoveTowardsRaidGoal<T extends Mob> extends Goal {
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
        LazyOptional<Raider> raiderCapabilityLazy = RaiderHelper.getRaiderCapabilityLazy(this.mob);
        if(!raiderCapabilityLazy.isPresent()){
            return false;
        }
        Raider raiderCapability = RaiderHelper.getRaiderCapability(this.mob);
        return this.mob instanceof PathfinderMob
                && this.mob.getTarget() == null
                && !this.mob.isVehicle() && raiderCapability.hasActiveRaid()
                && !raiderCapability.getRaid().isOver()
                && !((ServerLevel)this.mob.level).isVillage(this.mob.blockPosition());
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        Raider raiderCapability = RaiderHelper.getRaiderCapability(this.mob);
        return raiderCapability.hasActiveRaid() && this.mob.getTarget() == null && !raiderCapability.getRaid().isOver() && this.mob.level instanceof ServerLevel && !((ServerLevel)this.mob.level).isVillage(this.mob.blockPosition());
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        Raider raiderCapability = RaiderHelper.getRaiderCapability(this.mob);
        if (raiderCapability.hasActiveRaid()) {
            Raid raid = raiderCapability.getRaid();
//            if (this.mob.tickCount % 20 == 0) {
//                this.recruitNearby(raid);
//            }

            if (this.mob.getNavigation().isDone()) {
                Vec3 vector3d = DefaultRandomPos.getPosTowards((PathfinderMob) this.mob, 15, 4, Vec3.atBottomCenterOf(raid.getCenter()), (double)((float)Math.PI / 2F));
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