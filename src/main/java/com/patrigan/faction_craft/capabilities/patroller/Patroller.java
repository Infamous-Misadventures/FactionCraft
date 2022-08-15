package com.patrigan.faction_craft.capabilities.patroller;


import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.entity.ai.goal.PatrolGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.common.util.INBTSerializable;

import static com.patrigan.faction_craft.capabilities.ModCapabilities.PATROLLER_CAPABILITY;

public class Patroller implements INBTSerializable<CompoundTag> {

    private BlockPos patrolTarget = null;
    private boolean patrolLeader = false;
    private boolean patrolling = false;
    private final Mob entity;
    private Goal goal;

    public Patroller(Mob entity) {
        this.entity = entity;
        this.goal = new PatrolGoal<>(this.entity, 0.7D, 0.595D);
    }

    public BlockPos getPatrolTarget() {
        return patrolTarget;
    }

    public void setPatrolTarget(BlockPos patrolTarget) {
        this.patrolTarget = patrolTarget;
    }

    public boolean isPatrolLeader() {
        return patrolLeader;
    }

    public void setPatrolLeader(boolean patrolLeader) {
        this.patrolLeader = patrolLeader;
    }

    public boolean isPatrolling() {
        return patrolling;
    }

    public void setPatrolling(boolean patrolling) {
        this.patrolling = patrolling;
        updatePatrolGoals();
    }

    public boolean hasPatrolTarget() {
        return patrolTarget != null;
    }

    public void findPatrolTarget() {
        this.patrolTarget = this.entity.blockPosition().offset(-500 + this.entity.getRandom().nextInt(1000), 0, -500 + this.entity.getRandom().nextInt(1000));
        this.patrolling = true;
    }

    public boolean canJoinPatrol(Mob mob) {
        FactionEntity thisCap = FactionEntityHelper.getFactionEntityCapability(this.entity);
        FactionEntity otherCap = FactionEntityHelper.getFactionEntityCapability(mob);
        if(thisCap == null || otherCap == null) return false;
        return thisCap.getFaction() != null && thisCap.equals(otherCap.getFaction());
    }

    public CompoundTag save(CompoundTag compoundNbt) {
        if (this.patrolTarget != null) {
            compoundNbt.put("PatrolTarget", NbtUtils.writeBlockPos(this.patrolTarget));
        }
        compoundNbt.putBoolean("PatrolLeader", this.patrolLeader);
        compoundNbt.putBoolean("Patrolling", this.patrolling);
        return compoundNbt;
    }

    public void load(CompoundTag compoundNbt) {
        if (compoundNbt.contains("PatrolTarget")) {
            this.patrolTarget = NbtUtils.readBlockPos(compoundNbt.getCompound("PatrolTarget"));
        }

        this.patrolLeader = compoundNbt.getBoolean("PatrolLeader");
        this.patrolling = compoundNbt.getBoolean("Patrolling");
        updatePatrolGoals();
    }

    private void updatePatrolGoals(){
        if(this.isPatrolling()){
            this.entity.goalSelector.addGoal(4, goal);
        }else{
            this.entity.goalSelector.removeGoal(goal);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        if (PATROLLER_CAPABILITY == null) {
            return new CompoundTag();
        } else {
            CompoundTag compoundNbt = new CompoundTag();
            if (this.patrolTarget != null) {
                compoundNbt.put("PatrolTarget", NbtUtils.writeBlockPos(this.patrolTarget));
            }
            compoundNbt.putBoolean("PatrolLeader", this.patrolLeader);
            compoundNbt.putBoolean("Patrolling", this.patrolling);
            return compoundNbt;
        }
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("PatrolTarget")) {
            this.patrolTarget = NbtUtils.readBlockPos(tag.getCompound("PatrolTarget"));
        }

        this.patrolLeader = tag.getBoolean("PatrolLeader");
        this.patrolling = tag.getBoolean("Patrolling");
        updatePatrolGoals();

    }
}
