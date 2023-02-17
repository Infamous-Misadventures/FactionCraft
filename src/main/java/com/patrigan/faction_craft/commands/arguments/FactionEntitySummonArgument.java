package com.patrigan.faction_craft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.patrigan.faction_craft.faction.Faction;
import com.patrigan.faction_craft.faction.entity.FactionEntityType;
import com.patrigan.faction_craft.registry.FactionEntityTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class FactionEntitySummonArgument implements ArgumentType<FactionEntityType> {
    private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:vanilla");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_FACTION_ENTITY_TYPE = new DynamicCommandExceptionType((p_208663_0_) -> {
        return Component.translatable ("commands.argument.factionEntityTypeNotFound", p_208663_0_);
    });

    public static FactionEntitySummonArgument id() {
        return new FactionEntitySummonArgument();
    }

    public static FactionEntityType getSummonableEntity(CommandContext<CommandSourceStack> source, String name) throws CommandSyntaxException {
        return source.getArgument(name, FactionEntityType.class);
    }

    public FactionEntityType parse(StringReader p_parse_1_) throws CommandSyntaxException {
        ResourceLocation resourcelocation = ResourceLocation.read(p_parse_1_);
        if (FactionEntityTypes.factionEntityTypeExists(resourcelocation)) {
            return FactionEntityTypes.getFactionEntityType(resourcelocation);
        } else {
            throw ERROR_UNKNOWN_FACTION_ENTITY_TYPE.create(resourcelocation);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        Faction faction = commandContext.getArgument("faction", Faction.class);
        if(faction == null) return SharedSuggestionProvider.suggestResource(FactionEntityTypes.factionEntityTypeKeys(), suggestionsBuilder);;
        return SharedSuggestionProvider.suggestResource(FactionEntityTypes.getFactionEntityTypeData(faction).keySet(), suggestionsBuilder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
