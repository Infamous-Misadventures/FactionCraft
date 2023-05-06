package com.patrigan.faction_craft.faction;

import net.minecraft.resources.ResourceLocation;

public class FactionRelation{
    public static final int ALLY_THRESHOLD = 40;
    public static final int ENEMY_THRESHOLD = -40;
    public static final int NEUTRAL = 0;
    public static final int ALLY_MAX = 100;
    public static final int ENEMY_MAX = -100;

    private ResourceLocation faction;
    private int relation;

    public FactionRelation(ResourceLocation faction, int relation) {
        this.faction = faction;
        this.relation = relation;
    }

    public ResourceLocation getFaction() {
        return faction;
    }

    public int getRelation() {
        return relation;
    }

    public boolean isAlly(){
        return relation >= ALLY_THRESHOLD;
    }

    public boolean isEnemy(){
        return relation <= ENEMY_THRESHOLD;
    }

    public boolean isNeutral(){
        return !isAlly() && !isEnemy();
    }

    public boolean isFaction(Faction otherFaction) {
        return faction.equals(otherFaction.getName());
    }

    public void adjustRelation(int amount) {
        relation += amount;
        if (relation > ALLY_MAX) {
            relation = ALLY_MAX;
        } else if (relation < ENEMY_MAX) {
            relation = ENEMY_MAX;
        }
    }
}
