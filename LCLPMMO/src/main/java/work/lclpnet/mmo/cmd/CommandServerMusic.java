package work.lclpnet.mmo.cmd;

import java.util.List;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.PacketDistributor;
import work.lclpnet.corebase.cmd.CommandBase;
import work.lclpnet.corebase.cmd.CoreCommands;
import work.lclpnet.corebase.util.Substitute;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.network.msg.MessageMusic;
import work.lclpnet.mmo.network.msg.MessageMusic.MusicAction;

public class CommandServerMusic extends CommandBase{

	public CommandServerMusic() {
		super("servermusic");
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> transform(LiteralArgumentBuilder<CommandSource> builder) {
		return builder
				.requires(CoreCommands::permLevel2)
				.then(Commands.argument("target", EntityArgument.players())
						.then(Commands.literal("youtube")
								.then(Commands.literal("url")
										.then(Commands.argument("url", StringArgumentType.greedyString()).executes(this::playYtUrl)))
								.then(Commands.literal("search")
										.then(Commands.argument("ytquery", StringArgumentType.greedyString()).executes(this::playYtSearch)))
								.then(Commands.literal("downloaded")
										.then(Commands.argument("downloaded", MusicArgumentType.music()).executes(this::playYtDownloaded)))
								)
						.then(Commands.literal("volume")
								.then(Commands.argument("percent", FloatArgumentType.floatArg(0F, 1F))
										.executes(this::volumeAll)
										.then(Commands.argument("file", MusicArgumentType.music())
												.executes(this::volume))))
						.then(Commands.literal("stop")
								.executes(this::stopAll)
								.then(Commands.argument("file", MusicArgumentType.music())
										.executes(this::stop))
								)
						);
	}

	public int playYtDownloaded(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		String file = ctx.getArgument("downloaded", String.class);

		final MessageMusic msg = new MessageMusic(MusicAction.PLAY_YT, "downloaded:" + file);
		
		EntitySelector sel = ctx.getArgument("target", EntitySelector.class);
		List<ServerPlayerEntity> players = sel.selectPlayers(ctx.getSource());
		players.forEach(p -> {
			MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		});
		
		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Playing downloaded server music file '%s'...", 
						TextFormatting.GREEN, 
						new Substitute(file, TextFormatting.YELLOW)
						), 
				false);

		return 0;
	}

	public int playYtSearch(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		String file = ctx.getArgument("ytquery", String.class);

		final MessageMusic msg = new MessageMusic(MusicAction.PLAY_YT, "search:" + file);
		
		EntitySelector sel = ctx.getArgument("target", EntitySelector.class);
		List<ServerPlayerEntity> players = sel.selectPlayers(ctx.getSource());
		players.forEach(p -> {
			MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		});

		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Searching '%s' on YouTube...", 
						TextFormatting.GREEN, 
						new Substitute(file, TextFormatting.YELLOW)
						), 
				false);

		return 0;
	}

	public int playYtUrl(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		String file = ctx.getArgument("url", String.class);

		final MessageMusic msg = new MessageMusic(MusicAction.PLAY_YT, "url:" + file);
		
		EntitySelector sel = ctx.getArgument("target", EntitySelector.class);
		List<ServerPlayerEntity> players = sel.selectPlayers(ctx.getSource());
		players.forEach(p -> {
			MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		});
		
		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Searching for server music on '%s'...", 
						TextFormatting.GREEN, 
						new Substitute(file, TextFormatting.YELLOW)
						), 
				false);

		return 0;
	}

	/*public int playSelf(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		if(!CoreCommands.isPlayer(ctx.getSource())) {
			ctx.getSource().sendErrorMessage(LCLPMMO.TEXT.message("You must be a player to execute this command.", MessageType.ERROR));
			return 1;
		}

		String file = ctx.getArgument("file", String.class);
		ServerPlayerEntity p = ctx.getSource().asPlayer();

		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Playing music file '%s'...", 
						TextFormatting.GREEN, 
						new Substitute(file, TextFormatting.YELLOW)
						), 
				false);

		MessageMusic msg = new MessageMusic(MusicAction.PLAY, file);

		MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		return 0;
	}*/

	public int volumeAll(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		float percent = ctx.getArgument("percent", Float.class);

		final MessageMusic msg = new MessageMusic(MusicAction.VOLUME, percent);
		
		EntitySelector sel = ctx.getArgument("target", EntitySelector.class);
		List<ServerPlayerEntity> players = sel.selectPlayers(ctx.getSource());
		players.forEach(p -> {
			MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		});
		
		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Setting general server music volume to %s for %s...", 
						TextFormatting.GREEN, 
						new Substitute(percent, TextFormatting.YELLOW),
						new Substitute(EntitySelector.joinNames(players).getString(), TextFormatting.YELLOW)
						), 
				false);

		return 0;
	}

	public int volume(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		String file = ctx.getArgument("file", String.class);
		float percent = ctx.getArgument("percent", Float.class);
		
		final MessageMusic msg = new MessageMusic(MusicAction.VOLUME, file, percent);
		
		EntitySelector sel = ctx.getArgument("target", EntitySelector.class);
		List<ServerPlayerEntity> players = sel.selectPlayers(ctx.getSource());
		players.forEach(p -> {
			MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		});

		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Setting server music volume of '%s' to %s for %s...", 
						TextFormatting.GREEN, 
						new Substitute(file, TextFormatting.YELLOW), 
						new Substitute(percent, TextFormatting.YELLOW),
						new Substitute(EntitySelector.joinNames(players).getString(), TextFormatting.YELLOW)
						), 
				false);

		return 0;
	}

	public int stopAll(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		final MessageMusic msg = new MessageMusic(MusicAction.STOP);
		
		EntitySelector sel = ctx.getArgument("target", EntitySelector.class);
		List<ServerPlayerEntity> players = sel.selectPlayers(ctx.getSource());
		players.forEach(p -> {
			MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		});

		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Stopping all server music for %s...", 
						TextFormatting.GREEN,
						new Substitute(EntitySelector.joinNames(players).getString(), TextFormatting.YELLOW)), 
				false);


		return 0;
	}

	public int stop(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		String file = ctx.getArgument("file", String.class);
		final MessageMusic msg = new MessageMusic(MusicAction.STOP, file);
		
		EntitySelector sel = ctx.getArgument("target", EntitySelector.class);
		List<ServerPlayerEntity> players = sel.selectPlayers(ctx.getSource());
		players.forEach(p -> {
			MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		});
		
		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Stopping server music '%s' for %s...", 
						TextFormatting.GREEN, 
						new Substitute(file, TextFormatting.YELLOW),
						new Substitute(EntitySelector.joinNames(players).getString(), TextFormatting.YELLOW)
						), 
				false);

		return 0;
	}

}
