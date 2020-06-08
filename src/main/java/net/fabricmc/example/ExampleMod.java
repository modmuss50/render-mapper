package net.fabricmc.example;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class ExampleMod implements ModInitializer {

	public static String ACTIVE = "";
	public static List<String> PARTS = new CopyOnWriteArrayList<>();
	SuggestionProvider<ServerCommandSource> CLASSES = (context, builder) -> CommandSource.suggestMatching(PARTS.stream().map((s -> s.split("\\.")[0])), builder);
	SuggestionProvider<ServerCommandSource> FIELDS = (context, builder) -> CommandSource.suggestMatching(PARTS.stream().filter(s -> s.startsWith(getString(context, "class"))).map((s -> s.split("\\.")[1])), builder);


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");

		CommandRegistrationCallback.EVENT.register((cd, b) -> cd.register(
				literal("part_render")
				.then(
						argument("class", word())
						.suggests(CLASSES)
								.then(
										argument("field", word())
												.suggests(FIELDS)
												.executes(ExampleMod::execute)
								)

				)
		));
	}

	private static int execute(CommandContext<ServerCommandSource> s) {
		String clazz = getString(s, "class");
		String field = getString(s, "field");

		ACTIVE = clazz + "." + field;

		return Command.SINGLE_SUCCESS;
	}
}
