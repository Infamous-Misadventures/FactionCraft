package com.patrigan.faction_craft.capabilities.raider;


import com.patrigan.faction_craft.capabilities.raidmanager.IRaidManager;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper;
import com.patrigan.faction_craft.entity.ai.goal.InvadeHomeGoal;
import com.patrigan.faction_craft.entity.ai.goal.MoveTowardsRaidGoal;
import com.patrigan.faction_craft.entity.ai.goal.RaidOpenDoorGoal;
import com.patrigan.faction_craft.raid.Raid;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.world.server.ServerWorld;

public class Raider implements IRaider {

    protected Raid raid;
    private int wave;
    private boolean canJoinRaid = true;
    private int ticksOutsideRaid;
    private boolean waveLeader = false;
    private MobEntity entity;
    private MoveTowardsRaidGoal<MobEntity> moveTowardsRaidGoal;
    private InvadeHomeGoal invadeHomeGoal;
    private RaidOpenDoorGoal raidOpenDoorGoal;
    private NearestAttackableTargetGoal<AbstractVillagerEntity> abstractVillagerEntityNearestAttackableTargetGoal;


    public Raider() {
        this.entity = null;
    }

    public Raider(MobEntity entity) {
        this.entity = entity;
        moveTowardsRaidGoal = new MoveTowardsRaidGoal<>(this.entity);
        invadeHomeGoal = new InvadeHomeGoal(entity, 1.05F, 1);
        abstractVillagerEntityNearestAttackableTargetGoal = new NearestAttackableTargetGoal<>(this.entity, AbstractVillagerEntity.class, false);
        if(GroundPathHelper.hasGroundPathNavigation(this.entity)) {
            raidOpenDoorGoal = new RaidOpenDoorGoal(entity);
        }
    }

    @Override
    public Raid getRaid() {
        return raid;
    }

    @Override
    public void setRaid(Raid raid) {
        this.raid = raid;
        updateRaidGoals();
    }

    @Override
    public boolean hasActiveRaid() {
        return raid != null && raid.isActive();
    }

    @Override
    public int getWave() {
        return wave;
    }

    @Override
    public void setWave(int wave) {
        this.wave = wave;
    }

    public boolean isCanJoinRaid() {
        return canJoinRaid;
    }

    @Override
    public void setCanJoinRaid(boolean canJoinRaid) {
        this.canJoinRaid = canJoinRaid;
    }

    @Override
    public int getTicksOutsideRaid() {
        return ticksOutsideRaid;
    }

    @Override
    public void setTicksOutsideRaid(int ticksOutsideRaid) {
        this.ticksOutsideRaid = ticksOutsideRaid;
    }

    public boolean isWaveLeader() {
        return waveLeader;
    }

    public void setWaveLeader(boolean waveLeader) {
        this.waveLeader = waveLeader;
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putInt("Wave", this.wave);
        tag.putBoolean("CanJoinRaid", this.canJoinRaid);
        if (this.raid != null) {
            tag.putInt("RaidId", this.raid.getId());
        }
        return tag;
    }

    @Override
    public void load(CompoundNBT tag) {
        this.wave = tag.getInt("Wave");
        this.canJoinRaid = tag.getBoolean("CanJoinRaid");
        if (tag.contains("RaidId", 3)) {
            if (this.entity.level instanceof ServerWorld) {
                ServerWorld level = (ServerWorld) this.entity.level;
                IRaidManager raidManagerCapability = RaidManagerHelper.getRaidManagerCapability(level);
                this.raid = raidManagerCapability.getRaids().get(tag.getInt("RaidId"));
                updateRaidGoals();
            }

            if (this.raid != null) {
                this.raid.addWaveMob(this.wave, this.entity, false);
                if (this.waveLeader) {//TODO: How does this know?
                    this.raid.setLeader(this.wave, this.entity);
                }
            }
        }
    }

    private void updateRaidGoals(){
        if(this.raid != null){
            if(raidOpenDoorGoal != null) {
                this.entity.goalSelector.addGoal(2, raidOpenDoorGoal);
            }
            this.entity.goalSelector.addGoal(3, moveTowardsRaidGoal);
            this.entity.goalSelector.addGoal(4, invadeHomeGoal);
            this.entity.targetSelector.addGoal(3, abstractVillagerEntityNearestAttackableTargetGoal);
        }else{
            this.entity.goalSelector.removeGoal(raidOpenDoorGoal);
            this.entity.goalSelector.removeGoal(moveTowardsRaidGoal);
            this.entity.goalSelector.removeGoal(invadeHomeGoal);
        }
    }
}
