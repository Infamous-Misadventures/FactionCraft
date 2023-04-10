package com.patrigan.faction_craft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.registry.Factions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class FactionArgument implements ArgumentType<Faction> {
    private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:vanilla");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_FACTION = new DynamicCommandExceptionType((p_208663_0_) -> {
        return Component.translatable ("commands.argument.factionNotFound", p_208663_0_);
    });
    private final String enemyFactionArgName;
    private final boolean loadEnemyFactions;

    public FactionArgument(String enemyFactionArgName) {
        this.enemyFactionArgName = enemyFactionArgName;
        this.loadEnemyFactions = true;
    }

    public FactionArgument() {
        this.enemyFactionArgName = "";
        this.loadEnemyFactions = false;
    }

    public static FactionArgument factions() {
        return new FactionArgument();
    }

    public static FactionArgument enemyFactions(String enemyFactionArgumentName) {
        return new FactionArgument(enemyFactionArgumentName);
    }

    public static Faction getFaction(CommandContext<CommandSourceStack> source, String name) throws CommandSyntaxException {
        return source.getArgument(name, Faction.class);
    }

    public Faction parse(StringReader p_parse_1_) throws CommandSyntaxException {
        ResourceLocation resourcelocation = ResourceLocation.read(p_parse_1_);
        if (Factions.factionExists(resourcelocation)) {
            return Factions.getFaction(resourcelocation);
        } else {
            throw ERROR_UNKNOWN_FACTION.create(resourcelocation);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder p_listSuggestions_2_) {
        if(loadEnemyFactions) {
            Faction enemyFaction = commandContext.getArgument(enemyFactionArgName, Faction.class);
            return SharedSuggestionProvider.suggestResource(Factions.getEnemyFactionKeysOf(enemyFaction), p_listSuggestions_2_);
        }else {
            return SharedSuggestionProvider.suggestResource(Factions.factionKeys(), p_listSuggestions_2_);
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
