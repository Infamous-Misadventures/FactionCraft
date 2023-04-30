package com.patrigan.faction_craft.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntity;
import com.patrigan.faction_craft.capabilities.factionentity.FactionEntityHelper;
import com.patrigan.faction_craft.capabilities.raider.Raider;
import com.patrigan.faction_craft.capabilities.raider.RaiderHelper;
import com.patrigan.faction_craft.capabilities.raidmanager.RaidManager;
import com.patrigan.faction_craft.event.FactionRaidEvent;
import com.patrigan.faction_craft.faction.EntityWeightMapProperties;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.FactionBoostHelper;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.raid.target.RaidTarget;
import com.patrigan.faction_craft.raid.target.RaidTargetHelper;
import com.patrigan.faction_craft.registry.Factions;
import com.patrigan.faction_craft.util.GeneralUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.patrigan.faction_craft.capabilities.raidmanager.RaidManagerHelper.getRaidManagerCapability;
import static com.patrigan.faction_craft.config.FactionCraftConfig.*;
import static com.patrigan.faction_craft.util.GeneralUtils.getRandomEntry;

public class Raid {
    private final int id;
    private final ServerLevel level;
    private final RaidTarget raidTarget;
    private final ServerBossEvent raidEvent = new ServerBossEvent(Component.literal(""), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private final int numGroups;
    private final Queue<BlockPos> waveSpawnPos = new LinkedList<>();
    private final Map<Integer, Mob> groupToLeaderMap = Maps.newHashMap();
    private final Map<Integer, Set<Mob>> groupRaiderMap = Maps.newHashMap();
    private final Set<UUID> heroesOfTheVillage = Sets.newHashSet();
    private List<Faction> factions;
    private int badOmenLevel;
    private float totalHealth;
    private int groupsSpawned = 0;
    private boolean started;
    private boolean active;
    private Status status;
    private long ticksActive;
    private int raidCooldownTicks;
    private int postRaidTicks;
    private int celebrationTicks;

    public Raid(int uniqueId, List<Faction> factions, ServerLevel level, RaidTarget raidTarget) {
        this.id = uniqueId;
        this.factions = factions;
        if (this.factions.isEmpty()) {
            this.factions.add(Factions.getDefaultFaction());
        }
        this.level = level;
        this.raidTarget = raidTarget;
        this.numGroups = this.getNumGroups(level.getDifficulty(), raidTarget);
        this.groupsSpawned = raidTarget.getStartingWave();
        this.active = true;
        this.raidEvent.setName(getRaidEventName(raidTarget));
        this.raidEvent.setProgress(0.0F);
        this.status = Status.ONGOING;
    }


    public Raid(ServerLevel level, CompoundTag compoundNBT) {
        this.level = level;
        ListTag factionListnbt = compoundNBT.getList("Factions", 10);
        factions = new ArrayList<>();
        for (int i = 0; i < factionListnbt.size(); ++i) {
            CompoundTag compoundnbt = factionListnbt.getCompound(i);
            ResourceLocation factionName = new ResourceLocation(compoundnbt.getString("Faction"));
            if (Factions.factionExists(factionName)) {
                Faction faction = Factions.getFaction(factionName);
                this.factions.add(faction);
            }
        }
        if (this.factions.isEmpty()) {
            this.factions.add(Factions.getDefaultFaction());
        }
        this.raidTarget = RaidTargetHelper.load(level, compoundNBT.getCompound("RaidTarget"));
        this.raidEvent.setName(getRaidEventName(this.raidTarget));
        this.id = compoundNBT.getInt("Id");
        this.started = compoundNBT.getBoolean("Started");
        this.active = compoundNBT.getBoolean("Active");
        this.ticksActive = compoundNBT.getLong("TicksActive");
        this.badOmenLevel = compoundNBT.getInt("BadOmenLevel");
        this.groupsSpawned = compoundNBT.getInt("GroupsSpawned");
        this.raidCooldownTicks = compoundNBT.getInt("PreRaidTicks");
        this.postRaidTicks = compoundNBT.getInt("PostRaidTicks");
        this.totalHealth = compoundNBT.getFloat("TotalHealth");
        this.numGroups = compoundNBT.getInt("NumGroups");
        this.status = Status.getByName(compoundNBT.getString("Status"));
        this.heroesOfTheVillage.clear();
        if (compoundNBT.contains("HeroesOfTheVillage", 9)) {
            ListTag listnbt = compoundNBT.getList("HeroesOfTheVillage", 11);
            for (int i = 0; i < listnbt.size(); ++i) {
                this.heroesOfTheVillage.add(NbtUtils.loadUUID(listnbt.get(i)));
            }
        }
    }

    public ServerLevel getLevel() {
        return level;
    }

    public RaidTarget getRaidTarget() {
        return raidTarget;
    }

    public int getId() {
        return id;
    }

    public BlockPos getCenter() {
        return this.raidTarget.getTargetBlockPos();
    }

    public Collection<Faction> getFactions() {
        return factions;
    }

    public void addFactions(Collection<Faction> factions) {
        this.factions = Stream.of(this.factions, factions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public void addFaction(Faction faction) {
        this.factions.add(faction);
    }

    public void tick() {
        if (!this.isStopped()) {
            if (this.status == Status.ONGOING) {
                boolean flag = this.active;
                this.active = this.level.hasChunkAt(this.raidTarget.getTargetBlockPos());
                if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                    this.stop();
                    return;
                }
                if (flag != this.active) {
                    this.raidEvent.setVisible(this.active);
                }
                if (!this.active) {
                    return;
                }

                raidTarget.updateTargetBlockPos(level);

                if (raidTarget.isDefeat(this, level)) {
                    if (this.groupsSpawned > 0) {
                        FactionRaidEvent.Defeat event = new FactionRaidEvent.Defeat(this);
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
                        this.status = Status.LOSS;
                        this.playSound(raidTarget.getTargetBlockPos(), factions.get(0).getRaidConfig().getDefeatSoundEvent());
                        this.raidEvent.setName(getRaidEventNameDefeat(raidTarget));
                    } else {
                        this.stop();
                    }
                }

                ++this.ticksActive;
                if (this.ticksActive >= 48000L) {
                    this.stop();
                    return;
                }


                int i = this.getTotalRaidersAlive();
                if (i == 0 && this.hasMoreWaves()) {
                    if (raidCooldownTick()) {
                        return;
                    }
                }

                if (this.ticksActive % 20L == 0L) {
                    this.updatePlayers();
                    this.updateRaiders();
                    if (i > 0) {
                        if (i <= 2) {
                            this.raidEvent.setName(getRaidEventName(raidTarget).copy().append(" - ").append(Component.translatable("event.minecraft.raid.raiders_remaining", i)));
                        } else {
                            this.raidEvent.setName(getRaidEventName(raidTarget));
                        }
                    } else {
                        this.raidEvent.setName(getRaidEventName(raidTarget));
                    }
                }

                boolean flag3 = false;
                int k = 0;

                //Something with waveSpawnPos
                while (this.shouldSpawnGroup()) {
                    for (int j = this.waveSpawnPos.size(); j < factions.size(); j++) {
                        BlockPos randomSpawnPos = this.findRandomSpawnPos(k, 20);
                        if (randomSpawnPos != null) {
                            this.waveSpawnPos.add(randomSpawnPos);
                        }
                    }
                    if (this.waveSpawnPos.size() >= factions.size()) {
                        this.started = true;
                        this.spawnGroup();
                        if (!flag3) {
                            FactionRaidEvent.Wave event = new FactionRaidEvent.Wave(this);
                            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
                            flag3 = true;
                        }
                    } else {
                        ++k;
                    }

                    if (k > 3) {
                        this.stop();
                        break;
                    }
                }

                if (this.isStarted() && !this.hasMoreWaves() && i == 0) {
                    if (this.postRaidTicks < 40) {
                        ++this.postRaidTicks;
                    } else {
                        this.status = Status.VICTORY;
                        FactionRaidEvent.Victory event = new FactionRaidEvent.Victory(this);
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
                        if (factions.get(0).getRaidConfig().getVictorySoundEvent() != null) {
                            this.playSound(raidTarget.getTargetBlockPos(), factions.get(0).getRaidConfig().getVictorySoundEvent());
                        }
                        this.raidEvent.setName(getRaidEventNameVictory(raidTarget));

                        for (UUID uuid : this.heroesOfTheVillage) {
                            Entity entity = this.level.getEntity(uuid);
                            if (entity instanceof LivingEntity && !entity.isSpectator()) {
                                LivingEntity livingentity = (LivingEntity) entity;
                                livingentity.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 48000, this.badOmenLevel - 1, false, false, true));
                                if (livingentity instanceof ServerPlayer) {
                                    ServerPlayer serverplayerentity = (ServerPlayer) livingentity;
                                    serverplayerentity.awardStat(Stats.RAID_WIN);
                                    CriteriaTriggers.RAID_WIN.trigger(serverplayerentity);
                                }
                            }
                        }
                    }
                }
            } else if (this.isOver()) {
                ++this.celebrationTicks;
                if (this.celebrationTicks >= 600) {
                    this.stop();
                    return;
                }

                if (this.celebrationTicks % 20 == 0) {
                    this.updatePlayers();
                    this.raidEvent.setVisible(true);
                    if (this.isVictory()) {
                        this.raidEvent.setProgress(0.0F);
                        this.raidEvent.setName(getRaidEventNameVictory(raidTarget));
                    } else {
                        this.raidEvent.setName(getRaidEventNameDefeat(raidTarget));
                    }
                }
            }
        }
    }

    private void playSound(BlockPos p_221293_1_, SoundEvent soundEvent) {
        float f = 13.0F;
        int i = 64;
        Collection<ServerPlayer> collection = this.raidEvent.getPlayers();

        for (ServerPlayer serverplayerentity : this.level.players()) {
            Vec3 vector3d = serverplayerentity.position();
            Vec3 vector3d1 = Vec3.atCenterOf(p_221293_1_);
            double f1 = Math.sqrt((vector3d1.x - vector3d.x) * (vector3d1.x - vector3d.x) + (vector3d1.z - vector3d.z) * (vector3d1.z - vector3d.z));
            double d0 = vector3d.x + (double) (13.0F / f1) * (vector3d1.x - vector3d.x);
            double d1 = vector3d.z + (double) (13.0F / f1) * (vector3d1.z - vector3d.z);
            if (f1 <= 64.0F || collection.contains(serverplayerentity)) {
                serverplayerentity.connection.send(new ClientboundSoundPacket(soundEvent, SoundSource.NEUTRAL, d0, serverplayerentity.getY(), d1, 64.0F, 1.0F, serverplayerentity.getRandom().nextLong()));
            }
        }
    }

    private boolean raidCooldownTick() {
        if (this.raidCooldownTicks <= 0) {
            if (this.raidCooldownTicks == 0 && this.groupsSpawned > 0) {
                this.raidCooldownTicks = 300;
                this.raidEvent.setName(getRaidEventName(raidTarget));
                return true;
            }
        } else {
            boolean flag1 = this.waveSpawnPos.size() >= factions.size();
            boolean flag2 = !flag1 && this.raidCooldownTicks % 5 == 0;
            if (flag1 && !this.level.isPositionEntityTicking(this.waveSpawnPos.peek())) {
                this.waveSpawnPos.poll();
                flag2 = true;
            }

            if (flag2) {
                int j = 0;
                if (this.raidCooldownTicks < 100) {
                    j = 1;
                } else if (this.raidCooldownTicks < 40) {
                    j = 2;
                }

                Optional<BlockPos> validSpawnPos = this.getValidSpawnPos(j);
                if (validSpawnPos.isPresent()) {
                    this.waveSpawnPos.add(validSpawnPos.get());
                }
            }

            if (this.raidCooldownTicks == 300 || this.raidCooldownTicks % 20 == 0) {
                this.updatePlayers();
            }

            --this.raidCooldownTicks;
            this.raidEvent.setProgress(Mth.clamp((float) (300 - this.raidCooldownTicks) / 300.0F, 0.0F, 1.0F));
        }
        return false;
    }

    private boolean shouldSpawnGroup() {
        return this.raidCooldownTicks == 0 && (this.groupsSpawned < this.numGroups) && this.getTotalRaidersAlive() == 0;
    }

    private void spawnGroup() {
        int waveNumber = this.groupsSpawned + 1;
        this.totalHealth = 0.0F;

        double waveMultiplier = BASE_WAVE_MULTIPLIER.get() + (this.groupsSpawned * MULTIPLIER_INCREASE_PER_WAVE.get());
        double spreadMultiplier = ((level.random.nextFloat() * 2) - 1) * WAVE_TARGET_STRENGTH_SPREAD.get();
        double difficultyMultiplier = getDifficultyMultiplier(level.getDifficulty());
        double badOmenMultiplier = MULTIPLIER_INCREASE_PER_BAD_OMEN.get() * (factions.size() - 1);
        double totalMultiplier = waveMultiplier + spreadMultiplier + difficultyMultiplier + badOmenMultiplier;
        int targetStrength = (int) Math.floor(raidTarget.getTargetStrength() * totalMultiplier);
        Map<Faction, Integer> factionFractions = determineFactionFractions(targetStrength);
        factionFractions.entrySet().forEach(entry -> spawnGroupForFaction(this.waveSpawnPos.poll(), waveNumber, entry.getValue(), entry.getKey()));

        this.waveSpawnPos.clear();
        ++this.groupsSpawned;
        this.updateBossbar();
    }

    private Map<Faction, Integer> determineFactionFractions(int targetStrength) {
        Map<Faction, Integer> factionFractions = new HashMap<>();
        int perFactionStrength = (int) Math.floor(targetStrength / factions.size());
        factions.forEach(faction -> factionFractions.merge(faction, perFactionStrength, Integer::sum));
        return factionFractions;
    }

    private void spawnGroupForFaction(BlockPos spawnBlockPos, int waveNumber, int targetStrength, Faction faction) {
        int mobsFraction = (int) Math.floor(targetStrength * faction.getRaidConfig().getMobsFraction());

        int waveStrength = 0;
        Map<FactionEntityType, Integer> waveFactionEntities = determineFactionEntityTypes(mobsFraction, waveNumber, faction, spawnBlockPos);
        List<Mob> entities = new ArrayList<>();
        // Collect Entities
        for (Map.Entry<FactionEntityType, Integer> entry : waveFactionEntities.entrySet()) {
            FactionEntityType factionEntityType = entry.getKey();
            Integer amount = entry.getValue();
            for (int i = 0; i < amount; i++) {
                Entity entity = factionEntityType.createEntity(level, faction, spawnBlockPos, false, MobSpawnType.PATROL);
                if (entity instanceof Mob mobEntity) {
                    //Add to Raid
                    addToRaid(waveNumber, faction, entities, factionEntityType, mobEntity);
                    waveStrength += factionEntityType.getStrength();
                }

            }
        }
        // Apply Boosts
        FactionBoostHelper.applyBoosts(targetStrength - waveStrength, entities, faction, this.level);

        List<Entity> newEntities = entities.stream().flatMap(mobEntity -> mobEntity.getRootVehicle().getSelfAndPassengers()).filter(entity -> !entities.contains(entity)).toList();
        newEntities.forEach(entity -> {
            if (entity instanceof Mob) {
                Mob mobEntity = (Mob) entity;
                FactionEntity entityCapability = FactionEntityHelper.getFactionEntityCapability(mobEntity);
                if (entityCapability.getFaction() != null && entityCapability.getFactionEntityType() != null) {
                    this.joinRaid(waveNumber, mobEntity);
                    entities.add(mobEntity);
                }
            }
        });

        List<Mob> captainEntities = entities.stream().filter(mobEntity -> FactionEntityHelper.getFactionEntityCapability(mobEntity).getFactionEntityType().canBeBannerHolder()).toList();
        Mob randomItem = GeneralUtils.getRandomItem(captainEntities, level.getRandom());
        if (randomItem != null) {
            faction.makeBannerHolder(randomItem);
            Raider raiderCapability = RaiderHelper.getRaiderCapability(randomItem);
            raiderCapability.setWaveLeader(true);
        }
        this.playSound(spawnBlockPos, factions.get(0).getRaidConfig().getWaveSoundEvent());
    }

    private void addToRaid(int waveNumber, Faction faction, List<Mob> entities, FactionEntityType factionEntityType, Mob baseEntity) {
        baseEntity.getRootVehicle().getSelfAndPassengers().forEach(entity -> {
            if (entity instanceof Mob) {
                Mob mobEntity = (Mob) entity;
                FactionEntity factionEntityCapability = FactionEntityHelper.getFactionEntityCapability(mobEntity);
                if (factionEntityCapability != null && faction.equals(factionEntityCapability.getFaction())) {
                    this.joinRaid(waveNumber, mobEntity);
                    entities.add(mobEntity);
                }
            }
        });
    }

    private Map<FactionEntityType, Integer> determineFactionEntityTypes(int targetStrength, int waveNumber, Faction faction, BlockPos spawnBlockPos) {
        Map<FactionEntityType, Integer> waveFactionEntities = new HashMap<>();
        int selectedStrength = 0;
        Holder<Biome> biome = this.level.getBiome(spawnBlockPos);
        EntityWeightMapProperties entityWeightMapProperties = new EntityWeightMapProperties().setWave(waveNumber).setBiome(biome.get()).setBlockPos(spawnBlockPos);
        List<Pair<FactionEntityType, Integer>> weightMap = faction.getWeightMap(entityWeightMapProperties);
        for (Pair<FactionEntityType, Integer> pair : weightMap) {
            if (selectedStrength < targetStrength) {
                FactionEntityType factionEntityType = pair.getFirst();
                if (factionEntityType.getSpawnedRange().min() > 0) {
                    int strength = factionEntityType.getStrength();
                    int amount = Math.min((int) Math.ceil((targetStrength - selectedStrength) / strength), factionEntityType.getSpawnedRange().min());
                    selectedStrength += strength * amount;
                    waveFactionEntities.merge(factionEntityType, amount, Integer::sum);
                }
            }
            if (selectedStrength >= targetStrength) {
                break;
            }
        }
        while (selectedStrength < targetStrength && weightMap.size() > 0) {
            FactionEntityType randomEntry = getRandomEntry(weightMap, level.random);
            waveFactionEntities.merge(randomEntry, 1, Integer::sum);
            selectedStrength += randomEntry.getStrength();
            if (waveFactionEntities.get(randomEntry) >= randomEntry.getSpawnedRange().getMax()) {
                weightMap = weightMap.stream().filter(pair -> !pair.getFirst().equals(randomEntry)).toList();
            }
        }
        return waveFactionEntities;
    }

    public double getDifficultyMultiplier(Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_EASY.get();
            case NORMAL:
                return TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_NORMAL.get();
            case HARD:
                return TARGET_STRENGTH_DIFFICULTY_MULTIPLIER_HARD.get();
            default:
                return 0;
        }
    }

    public void setLeader(int pRaidId, Mob mobEntity) {
        this.groupToLeaderMap.put(pRaidId, mobEntity);
    }

    public void removeLeader(int wave) {
        this.groupToLeaderMap.remove(wave);
    }

    public void joinRaid(int pWave, Mob mobEntity) {
        this.addWaveMob(pWave, mobEntity, true);
        RaiderHelper.getRaiderCapability(mobEntity).addToRaid(pWave, this);
    }

    public void addWaveMob(int wave, Mob mobEntity, boolean fresh) {
        this.groupRaiderMap.computeIfAbsent(wave, p_221323_0_ -> Sets.newHashSet());
        Set<Mob> set = this.groupRaiderMap.get(wave);
        Mob abstractraiderentity = null;

        for (Mob abstractraiderentity1 : set) {
            if (abstractraiderentity1.getUUID().equals(mobEntity.getUUID())) {
                abstractraiderentity = abstractraiderentity1;
                break;
            }
        }

        if (abstractraiderentity != null) {
            set.remove(abstractraiderentity);
        }

        set.add(mobEntity);
        if (fresh) {
            this.totalHealth += mobEntity.getHealth();
        }

        this.updateBossbar();
    }

    public void removeFromRaid(Mob mobEntity, int wave, boolean p_221322_2_) {
        Set<Mob> set = this.groupRaiderMap.get(wave);
        if (set != null) {
            boolean flag = set.remove(mobEntity);
            if (flag) {
                if (p_221322_2_) {
                    this.totalHealth -= mobEntity.getHealth();
                }

                RaiderHelper.getRaiderCapability(mobEntity).setRaid(null);
                this.updateBossbar();
            }
        }
    }

    private void updateRaiders() {
        Iterator<Map.Entry<Integer, Set<Mob>>> iterator = this.groupRaiderMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Set<Mob> set = Sets.newHashSet();
            Map.Entry<Integer, Set<Mob>> waveEntry = iterator.next();

            for (Mob mobEntity : waveEntry.getValue()) {
                BlockPos blockpos = mobEntity.blockPosition();
                if (mobEntity.isAlive() && mobEntity.level.dimension() == this.level.dimension() && !(this.getCenter().distSqr(blockpos) >= 12544.0D)) {
                    if (mobEntity.tickCount > 600) {
                        Raider raiderCapability = RaiderHelper.getRaiderCapability(mobEntity);
                        if (this.level.getEntity(mobEntity.getUUID()) == null) {
                            set.add(mobEntity);
                        }

                        if (mobEntity.getNoActionTime() > 2400) {
                            raiderCapability.setTicksOutsideRaid(raiderCapability.getTicksOutsideRaid() + 1); //TODO: RaiderCapability: TickOutsideRaid
                        }

                        if (raiderCapability.getTicksOutsideRaid() >= 30) {
                            set.add(mobEntity);
                        }
                    }
                } else {
                    set.add(mobEntity);
                }
            }
            for (Mob abstractraiderentity1 : set) {
                this.removeFromRaid(abstractraiderentity1, waveEntry.getKey(), true);
            }
        }
    }

    public void updateBossbar() {
        this.raidEvent.setProgress(Mth.clamp(this.getHealthOfLivingRaiders() / this.totalHealth, 0.0F, 1.0F));
    }

    public float getHealthOfLivingRaiders() {
        float f = 0.0F;

        for (Set<Mob> set : this.groupRaiderMap.values()) {
            for (Mob mobEntity : set) {
                f += mobEntity.getHealth();
            }
        }

        return f;
    }

    private Optional<BlockPos> getValidSpawnPos(int p_221313_1_) {
        for (int i = 0; i < 3; ++i) {
            BlockPos blockpos = this.findRandomSpawnPos(p_221313_1_, 1);
            if (blockpos != null) {
                return Optional.of(blockpos);
            }
        }

        return Optional.empty();
    }

    @Nullable
    private BlockPos findRandomSpawnPos(int outerAttempt, int maxInnerAttempts) {
        int i = 2 - outerAttempt;
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        for (int i1 = 0; i1 < maxInnerAttempts; ++i1) {
            float f = this.level.random.nextFloat() * ((float) Math.PI * 2F);
            int j = this.raidTarget.getTargetBlockPos().getX() + Mth.floor(Mth.cos(f) * raidTarget.getSpawnDistance() * (float) i) + this.level.random.nextInt(5);
            int l = this.raidTarget.getTargetBlockPos().getZ() + Mth.floor(Mth.sin(f) * raidTarget.getSpawnDistance() * (float) i) + this.level.random.nextInt(5);
            int k = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, j, l);
            blockpos$mutable.set(j, k, l);
            if (isValidSpawnPos(blockpos$mutable) && raidTarget.isValidSpawnPos(outerAttempt, blockpos$mutable, this.level)) {
                return blockpos$mutable;
            }
        }

        return null;
    }

    private boolean isValidSpawnPos(BlockPos.MutableBlockPos blockpos$mutable) {
        return this.waveSpawnPos.stream().map(existingWaveSpawnPos -> blockpos$mutable.distSqr(existingWaveSpawnPos) > 40).reduce((aBoolean, aBoolean2) -> aBoolean && aBoolean2).orElse(true);
    }

    public int getNumGroups(Difficulty difficulty, RaidTarget raidTarget) {
        int numberOfWaves = 0;
        switch (difficulty) {
            case EASY:
                numberOfWaves = NUMBER_WAVES_EASY.get();
                break;
            case NORMAL:
                numberOfWaves = NUMBER_WAVES_NORMAL.get();
                break;
            case HARD:
                numberOfWaves = NUMBER_WAVES_HARD.get();
                break;
            default:
                numberOfWaves = 0;
        }
        numberOfWaves = numberOfWaves + raidTarget.getAdditionalWaves();
        return Math.min(numberOfWaves, MAX_NUMBER_WAVES.get());
    }

    private boolean hasMoreWaves() {
        return !this.isFinalWave();
    }

    private boolean isFinalWave() {
        return this.getGroupsSpawned() >= this.numGroups;
    }

    public int getGroupsSpawned() {
        return groupsSpawned;
    }

    private Predicate<ServerPlayer> validPlayer() {
        return (serverPlayerEntity) -> {
            BlockPos blockpos = serverPlayerEntity.blockPosition();
            RaidManager cap = getRaidManagerCapability(this.level);
            return serverPlayerEntity.isAlive() && cap.getRaidAt(blockpos) == this;
        };
    }

    private void updatePlayers() {
        Set<ServerPlayer> set = Sets.newHashSet(this.raidEvent.getPlayers());
        List<ServerPlayer> list = this.level.getPlayers(this.validPlayer());

        for (ServerPlayer serverplayerentity : list) {
            if (!set.contains(serverplayerentity)) {
                this.raidEvent.addPlayer(serverplayerentity);
            }
        }

        for (ServerPlayer serverplayerentity1 : set) {
            if (!list.contains(serverplayerentity1)) {
                this.raidEvent.removePlayer(serverplayerentity1);
            }
        }
    }

    public int getTotalRaidersAlive() {
        return this.groupRaiderMap.values().stream().mapToInt(Set::size).sum();
    }

    public Set<Mob> getRaidersInWave(int wave) {
        return this.groupRaiderMap.get(wave);
    }

    public void stop() {
        this.active = false;
        this.raidEvent.removeAllPlayers();
        Set<Mob> raidersInWave = getRaidersInWave(getGroupsSpawned());
        if (raidersInWave != null && !this.isLoss()) {
            new HashSet<>(raidersInWave).forEach(LivingEntity::kill);
        }
        this.status = Status.STOPPED;
    }

    public void addHeroOfTheVillage(Entity p_221311_1_) {
        this.heroesOfTheVillage.add(p_221311_1_.getUUID());
    }


    public boolean isBetweenWaves() {
        return this.hasFirstWaveSpawned() && this.getTotalRaidersAlive() == 0 && this.raidCooldownTicks > 0;
    }

    public boolean hasFirstWaveSpawned() {
        return this.groupsSpawned > 0;
    }

    public boolean isStarted() {
        return this.started;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isStopped() {
        return this.status == Status.STOPPED;
    }

    public boolean isVictory() {
        return this.status == Status.VICTORY;
    }

    public boolean isLoss() {
        return this.status == Status.LOSS;
    }

    public boolean isOver() {
        return this.isVictory() || this.isLoss();
    }

    private Component getRaidEventName(RaidTarget raidTarget) {
        return raidTarget.getRaidType() == RaidTarget.Type.BATTLE ? Component.translatable("event.faction_craft.battle") : this.factions.get(0).getRaidConfig().getRaidBarNameComponent();
    }

    private Component getRaidEventNameDefeat(RaidTarget raidTarget) {
        return raidTarget.getRaidType() == RaidTarget.Type.BATTLE ? Component.translatable("event.faction_craft.battle.over") : this.factions.get(0).getRaidConfig().getRaidBarDefeatComponent();
    }

    private Component getRaidEventNameVictory(RaidTarget raidTarget) {
        return raidTarget.getRaidType() == RaidTarget.Type.BATTLE ? Component.translatable("event.faction_craft.battle.over") : this.factions.get(0).getRaidConfig().getRaidBarVictoryComponent();
    }

    public void endWave() {
        Set<Mob> raidersInWave = getRaidersInWave(getGroupsSpawned());
        if (raidersInWave != null) {
            new HashSet<Mob>(raidersInWave).forEach(LivingEntity::kill);
        }
    }

    public void spawnDigger(Faction faction, BlockPos spawnBlockPos, Mob mob) {
        if(FactionEntityHelper.getFactionEntityCapability(mob).getFactionEntityType().hasRank(FactionEntityType.FactionRank.DIGGER)) return;
        this.spawnDigger(faction, spawnBlockPos);
    }

    public void spawnDigger(Faction faction, BlockPos spawnBlockPos) {
        // check how many diggers are already spawned
        if(this.getRaidersInWave(this.getGroupsSpawned()).stream().filter(entity -> FactionEntityHelper.getFactionEntityCapability(entity).getFactionEntityType().hasRank(FactionEntityType.FactionRank.DIGGER)).count() > Mth.ceil(this.getRaidersInWave(this.getGroupsSpawned()).size() * 0.25)) return;
        EntityWeightMapProperties entityWeightMapProperties = new EntityWeightMapProperties().setAllowedRanks(List.of(FactionEntityType.FactionRank.DIGGER)).setBlockPos(spawnBlockPos);
        List<Pair<FactionEntityType, Integer>> weightMap = faction.getWeightMap(entityWeightMapProperties);
        if (weightMap.isEmpty()) return;
        FactionEntityType randomEntry = getRandomEntry(weightMap, level.random);
        Entity entity = randomEntry.createEntity(level, faction, spawnBlockPos, false, MobSpawnType.PATROL);
        if(entity instanceof Mob mob) {
            this.joinRaid(this.getGroupsSpawned(), mob);
        }
    }

    public CompoundTag save(CompoundTag pNbt) {
        pNbt.putInt("Id", this.id);
        pNbt.putBoolean("Started", this.started);
        pNbt.putBoolean("Active", this.active);
        pNbt.putLong("TicksActive", this.ticksActive);
        pNbt.putInt("BadOmenLevel", this.badOmenLevel);
        pNbt.putInt("GroupsSpawned", this.groupsSpawned);
        pNbt.putInt("PreRaidTicks", this.raidCooldownTicks);
        pNbt.putInt("PostRaidTicks", this.postRaidTicks);
        pNbt.putFloat("TotalHealth", this.totalHealth);
        pNbt.putInt("NumGroups", this.numGroups);
        pNbt.putString("Status", this.status.getName());

        ListTag factionListnbt = new ListTag();
        for (Faction faction : this.factions) {
            CompoundTag compoundnbt = new CompoundTag();
            compoundnbt.putString("Faction", faction.getName().toString());
            factionListnbt.add(compoundnbt);
        }
        pNbt.put("Factions", factionListnbt);

        CompoundTag raidTargetNbt = new CompoundTag();
        raidTarget.save(raidTargetNbt);
        pNbt.put("RaidTarget", raidTargetNbt);

        ListTag listnbt = new ListTag();

        for (UUID uuid : this.heroesOfTheVillage) {
            listnbt.add(NbtUtils.createUUID(uuid));
        }
        pNbt.put("HeroesOfTheVillage", listnbt);
        return pNbt;
    }

    private enum Status {
        ONGOING,
        VICTORY,
        LOSS,
        STOPPED;

        private static final Status[] VALUES = values();

        private static Status getByName(String pName) {
            for (Status raid$status : VALUES) {
                if (pName.equalsIgnoreCase(raid$status.name())) {
                    return raid$status;
                }
            }

            return ONGOING;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
