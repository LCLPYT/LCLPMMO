package work.lclpnet.mmo.cmd;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;

public class MMOCommands {

	public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
		new CommandMusic().register(dispatcher);
	}
	
}
