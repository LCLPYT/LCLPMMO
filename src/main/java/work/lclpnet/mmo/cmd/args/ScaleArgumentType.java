package work.lclpnet.mmo.cmd.args;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class ScaleArgumentType implements ArgumentType<ScaleArgumentResult> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "0.1 0.5", "~1 ~2");
	public static final SimpleCommandExceptionType VEC2_INCOMPLETE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos2d.incomplete"));

	public static ScaleArgumentType scale() {
		return new ScaleArgumentType();
	}

	public static Vector2f getVec2f(CommandContext<CommandSource> context, String name, Entity target) throws CommandSyntaxException {
		return context.getArgument(name, ScaleArgumentResult.class).getScale(target);
	}

	public ScaleArgumentResult parse(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();
		if (!reader.canRead()) {
			throw VEC2_INCOMPLETE.createWithContext(reader);
		} else {
			ScalePart locationpart = ScalePart.parseFloat(reader);
			if (reader.canRead() && reader.peek() == ' ') {
				reader.skip();
				ScalePart locationpart1 = ScalePart.parseFloat(reader);
				return new ScaleArgumentResult(locationpart, locationpart1);
			} else {
				reader.setCursor(i);
				throw VEC2_INCOMPLETE.createWithContext(reader);
			}
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder builder) {
		if (!(ctx.getSource() instanceof ISuggestionProvider)) {
			return Suggestions.empty();
		} else {
			String s = builder.getRemaining();
			Collection<ScaleArgumentType.Values> collection = Collections.singleton(ScaleArgumentType.Values.DEFAULT_GLOBAL);
			return getCompletableFutures(s, collection, builder, Commands.predicate(this::parse));
		}
	}

	private CompletableFuture<Suggestions> getCompletableFutures(String remaining, Collection<ScaleArgumentType.Values> collection, SuggestionsBuilder builder, Predicate<String> condition) {
		List<String> list = Lists.newArrayList();
		if (Strings.isNullOrEmpty(remaining)) {
			for(ScaleArgumentType.Values valuePair : collection) {
				String s = valuePair.width + " " + valuePair.height;
				if (condition.test(s)) {
					list.add(valuePair.width);
					list.add(s);
				}
			}
		} else {
			String[] astring = remaining.split(" ");
			if (astring.length == 1) {
				for(ScaleArgumentType.Values valuePair : collection) {
					String s1 = astring[0] + " " + valuePair.height;
					if (condition.test(s1)) {
						list.add(s1);
					}
				}
			}
		}

		return ISuggestionProvider.suggest(list, builder);
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static class Values {
		public static final ScaleArgumentType.Values DEFAULT_GLOBAL = new ScaleArgumentType.Values("~", "~");
		public final String width;
		public final String height;

		public Values(String widthIn, String heightIn) {
			this.width = widthIn;
			this.height = heightIn;
		}
	}
}
