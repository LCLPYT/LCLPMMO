package work.lclpnet.mmo.cmd;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands.EnvironmentType;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import work.lclpnet.mmo.cmd.args.MusicArgumentType;
import work.lclpnet.mmo.cmd.args.ScaleArgumentType;

public class MMOCommands {

    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher, EnvironmentType type) {
        new CommandMusic().register(dispatcher);
        new CommandServerMusic().register(dispatcher);
        new CommandScale().register(dispatcher);
    }

    public static void registerArgumentTypes() {
        ArgumentTypes.register("mmo_music", MusicArgumentType.class, new ArgumentSerializer<>(MusicArgumentType::music));
        ArgumentTypes.register("mmo_scale", ScaleArgumentType.class, new ArgumentSerializer<>(ScaleArgumentType::scale));
    }

}
