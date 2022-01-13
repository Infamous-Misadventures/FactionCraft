package com.patrigan.faction_craft.capabilities.patroller;

import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public interface IPatroller {

    void setPatrolTarget(BlockPos blockPos);

    BlockPos getPatrolTarget();

    boolean isPatrolLeader();

    void setPatrolLeader(boolean patrolLeader);

    boolean isPatrolling();

    void setPatrolling(boolean patrolling);

    boolean hasPatrolTarget();

    void findPatrolTarget();

    boolean canJoinPatrol(MobEntity mob);

    CompoundNBT save(CompoundNBT tag);

    void load(CompoundNBT tag);
}
