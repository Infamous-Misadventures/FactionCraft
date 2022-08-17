package com.patrigan.faction_craft.capabilities.patroller;


import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.factionentity.IFactionEntity;
import com.patrigan.faction_craft.entity.ai.goal.PatrolGoal;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class Patroller implements IPatroller {

    private BlockPos patrolTarget = null;
    private boolean patrolLeader = false;
    private boolean patrolling = false;
    private MobEntity entity;
    private Goal goal;

    public Patroller() {
        this.entity = null;
    }

    public Patroller(MobEntity entity) {
        this.entity = entity;
        this.goal = new PatrolGoal<>(this.entity, 0.7D, 0.595D);
        updatePatrolGoals();
    }

    @Override
    public BlockPos getPatrolTarget() {
        return patrolTarget;
    }

    @Override
    public void setPatrolTarget(BlockPos patrolTarget) {
        this.patrolTarget = patrolTarget;
    }

    @Override
    public boolean isPatrolLeader() {
        return patrolLeader;
    }

    @Override
    public void setPatrolLeader(boolean patrolLeader) {
        this.patrolLeader = patrolLeader;
    }

    @Override
    public boolean isPatrolling() {
        return patrolling;
    }

    @Override
    public void setPatrolling(boolean patrolling) {
        this.patrolling = patrolling;
        updatePatrolGoals();
    }

    @Override
    public boolean hasPatrolTarget() {
        return patrolTarget != null;
    }

    @Override
    public void findPatrolTarget() {
        this.patrolTarget = this.entity.blockPosition().offset(-500 + this.entity.getRandom().nextInt(1000), 0, -500 + this.entity.getRandom().nextInt(1000));
        this.patrolling = true;
    }

    @Override
    public boolean canJoinPatrol(MobEntity mob) {
        IFactionEntity thisCap = FactionEntityHelper.getFactionEntityCapability(this.entity);
        IFactionEntity otherCap = FactionEntityHelper.getFactionEntityCapability(mob);
        if(thisCap == null || otherCap == null) return false;
        return thisCap.getFaction() != null && thisCap.getFaction().equals(otherCap.getFaction());
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNbt) {
        if (this.patrolTarget != null) {
            compoundNbt.put("PatrolTarget", NBTUtil.writeBlockPos(this.patrolTarget));
        }
        compoundNbt.putBoolean("PatrolLeader", this.patrolLeader);
        compoundNbt.putBoolean("Patrolling", this.patrolling);
        return compoundNbt;
    }

    @Override
    public void load(CompoundNBT compoundNbt) {
        if (compoundNbt.contains("PatrolTarget")) {
            this.patrolTarget = NBTUtil.readBlockPos(compoundNbt.getCompound("PatrolTarget"));
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
}
