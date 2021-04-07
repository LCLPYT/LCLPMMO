package work.lclpnet.mmo.cmd.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import work.lclpnet.mmo.audio.MusicSystem;

import java.util.concurrent.CompletableFuture;

public class MusicArgumentType implements ArgumentType<String> {

    @Override
    public String parse(StringReader reader) {
        final String text = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return text;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String input = context.getInput();
        if (input.startsWith("/music stop ") || input.startsWith("/music volume "))
            MusicSystem.getAllPlaying().forEach(builder::suggest);
        else if (input.startsWith("/music youtube downloaded "))
            MusicSystem.getDownloadedVideoTitles().forEach(builder::suggest);
        else MusicSystem.getAllMusicFiles().forEach(builder::suggest);

        return builder.buildFuture();
    }

    public static String getMusic(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    public static MusicArgumentType music() {
        return new MusicArgumentType();
    }

}
