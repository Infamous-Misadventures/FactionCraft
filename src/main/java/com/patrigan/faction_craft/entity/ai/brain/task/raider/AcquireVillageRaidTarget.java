package com.patrigan.faction_craft.entity.ai.brain.task.raider;

import com.google.common.collect.ImmutableMap;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.raid.Raid;
import com.patrigan.faction_craft.registry.ModMemoryModuleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class AcquireVillageRaidTarget<E extends LivingEntity> extends Behavior<E> {

    private MemoryModuleType<GlobalPos> memoryToAcquire;
    private int closeEnoughDist;

    public AcquireVillageRaidTarget(MemoryModuleType<GlobalPos> memoryToAcquire, int closeEnoughDist) {
        super(constructEntryConditionMap(memoryToAcquire));

        this.memoryToAcquire = memoryToAcquire;
        this.closeEnoughDist = closeEnoughDist;
    }

    private static ImmutableMap<MemoryModuleType<?>, MemoryStatus> constructEntryConditionMap(MemoryModuleType<GlobalPos> pMemoryToAcquire) {
        ImmutableMap.Builder<MemoryModuleType<?>, MemoryStatus> builder = ImmutableMap.builder();
        builder.put(pMemoryToAcquire, MemoryStatus.REGISTERED);
        builder.put(ModMemoryModuleTypes.RAIDED_VILLAGE_POI.get(), MemoryStatus.VALUE_PRESENT);

        return builder.build();
    }

    protected boolean checkExtraStartConditions(ServerLevel pLevel, PathfinderMob pEntity) {
        return RaiderHelper.getRaiderCapability(pEntity).getRaid() != null;
    }


    @Override
    protected void start(ServerLevel pLevel, E pEntity, long pGameTime) {
        if (pEntity instanceof Mob mob) {
            Raid raid = RaiderHelper.getRaiderCapability(mob).getRaid();
            if (raid != null && pLevel.isVillage(pEntity.blockPosition())) {
                Optional<GlobalPos> memoryToAcquireOptional = pEntity.getBrain().getMemory(memoryToAcquire);
                if(memoryToAcquireOptional.isPresent() && closeEnough(pLevel, pEntity, memoryToAcquireOptional.get())) {
                    Optional<List<GlobalPos>> memory = pEntity.getBrain().getMemory(ModMemoryModuleTypes.RAIDED_VILLAGE_POI.get());
                    memory.get().add(GlobalPos.of(pLevel.dimension(), memoryToAcquireOptional.get().pos()));
                    pEntity.getBrain().eraseMemory(memoryToAcquire);
                }
                if (memoryToAcquireOptional.isEmpty()) {
                    getNewRaidPoi(pLevel, pEntity, raid);
                }
            }else{
                pEntity.getBrain().eraseMemory(ModMemoryModuleTypes.RAIDED_VILLAGE_POI.get());
            }
        }
        super.start(pLevel, pEntity, pGameTime);
    }

    private void getNewRaidPoi(ServerLevel pLevel, E pEntity, Raid raid) {
        BlockPos blockpos = pEntity.blockPosition();
        Optional<BlockPos> optional = pLevel.getPoiManager().getRandom(poiType -> poiType.is(PoiTypes.HOME), getHasNotVisited(pEntity), PoiManager.Occupancy.ANY, blockpos, 48, pEntity.getRandom());
        optional.ifPresent(blockPos -> pEntity.getBrain().setMemory(this.memoryToAcquire, GlobalPos.of(pLevel.dimension(), blockPos)));
    }

    private Predicate<BlockPos> getHasNotVisited(E pEntity) {
        Optional<List<GlobalPos>> memory = pEntity.getBrain().getMemory(ModMemoryModuleTypes.RAIDED_VILLAGE_POI.get());
        return (blockPos) -> {
            if (memory.isPresent()) {
                for (GlobalPos globalpos : memory.get()) {
                    if (globalpos.pos().equals(blockPos)) {
                        return false;
                    }
                }
            }
            return true;
        };
    }

    private boolean closeEnough(ServerLevel pLevel, E entity, GlobalPos pMemoryPos) {
        return pMemoryPos.dimension() == pLevel.dimension() && pMemoryPos.pos().distManhattan(entity.blockPosition()) <= this.closeEnoughDist;
    }
}