package work.lclpnet.mmo.cmd;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.PacketDistributor;
import work.lclpnet.corebase.cmd.CommandBase;
import work.lclpnet.corebase.cmd.CoreCommands;
import work.lclpnet.corebase.util.MessageType;
import work.lclpnet.corebase.util.Substitute;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.cmd.args.MusicArgumentType;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.network.msg.MessageMusic;
import work.lclpnet.mmo.network.msg.MessageMusic.MusicAction;

public class CommandMusic extends CommandBase{

	public CommandMusic() {
		super("music");
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> transform(LiteralArgumentBuilder<CommandSource> builder) {
		return builder
				.then(Commands.literal("play")
						.then(Commands.argument("file", MusicArgumentType.music())
								.executes(this::playSelf)))
				.then(Commands.literal("youtube")
						.then(Commands.literal("url")
								.then(Commands.argument("url", StringArgumentType.greedyString()).executes(this::playYtUrlSelf)))
						.then(Commands.literal("search")
								.then(Commands.argument("ytquery", StringArgumentType.greedyString()).executes(this::playYtSearchSelf)))
						.then(Commands.literal("downloaded")
								.then(Commands.argument("downloaded", MusicArgumentType.music()).executes(this::playYtDownloadedSelf)))
						)
				.then(Commands.literal("volume")
						.then(Commands.argument("percent", FloatArgumentType.floatArg(0F, 1F))
								.executes(this::volumeAllSelf)
								.then(Commands.argument("file", MusicArgumentType.music())
										.executes(this::volumeSelf))))
				.then(Commands.literal("stop")
						.executes(this::stopAllSelf)
						.then(Commands.argument("file", MusicArgumentType.music())
								.executes(this::stopSelf)));
	}

	public int playYtDownloadedSelf(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		if(!CoreCommands.isPlayer(ctx.getSource())) {
			ctx.getSource().sendErrorMessage(LCLPMMO.TEXT.message("You must be a player to execute this command.", MessageType.ERROR));
			return 1;
		}

		String file = ctx.getArgument("downloaded", String.class);

		ServerPlayerEntity p = ctx.getSource().asPlayer();

		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Playing downloaded music file '%s'...", 
						TextFormatting.GREEN, 
						new Substitute(file, TextFormatting.YELLOW)
						), 
				false);

		MessageMusic msg = new MessageMusic(MusicAction.PLAY_YT, "downloaded:" + file);

		MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		return 0;
	}
	
	public int playYtSearchSelf(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		if(!CoreCommands.isPlayer(ctx.getSource())) {
			ctx.getSource().sendErrorMessage(LCLPMMO.TEXT.message("You must be a player to execute this command.", MessageType.ERROR));
			return 1;
		}

		String file = ctx.getArgument("ytquery", String.class);

		ServerPlayerEntity p = ctx.getSource().asPlayer();

		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Searching '%s' on YouTube...", 
						TextFormatting.GREEN, 
						new Substitute(file, TextFormatting.YELLOW)
						), 
				false);

		MessageMusic msg = new MessageMusic(MusicAction.PLAY_YT, "search:" + file);

		MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		return 0;
	}
	
	public int playYtUrlSelf(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		if(!CoreCommands.isPlayer(ctx.getSource())) {
			ctx.getSource().sendErrorMessage(LCLPMMO.TEXT.message("You must be a player to execute this command.", MessageType.ERROR));
			return 1;
		}

		String file = ctx.getArgument("url", String.class);

		ServerPlayerEntity p = ctx.getSource().asPlayer();

		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Searching for music on '%s'...", 
						TextFormatting.GREEN, 
						new Substitute(file, TextFormatting.YELLOW)
						), 
				false);

		MessageMusic msg = new MessageMusic(MusicAction.PLAY_YT, "url:" + file);

		MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		return 0;
	}

	public int playSelf(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
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
	}

	public int volumeAllSelf(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		if(!CoreCommands.isPlayer(ctx.getSource())) {
			ctx.getSource().sendErrorMessage(LCLPMMO.TEXT.message("You must be a player to execute this command.", MessageType.ERROR));
			return 1;
		}

		float percent = ctx.getArgument("percent", Float.class);
		ServerPlayerEntity p = ctx.getSource().asPlayer();

		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Setting general music volume to %s...", 
						TextFormatting.GREEN, 
						new Substitute(percent, TextFormatting.YELLOW) 
						), 
				false);

		MessageMusic msg = new MessageMusic(MusicAction.VOLUME, percent);

		MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		return 0;
	}

	public int volumeSelf(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		if(!CoreCommands.isPlayer(ctx.getSource())) {
			ctx.getSource().sendErrorMessage(LCLPMMO.TEXT.message("You must be a player to execute this command.", MessageType.ERROR));
			return 1;
		}

		String file = ctx.getArgument("file", String.class);
		float percent = ctx.getArgument("percent", Float.class);
		ServerPlayerEntity p = ctx.getSource().asPlayer();

		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Setting volume of '%s' to %s...", 
						TextFormatting.GREEN, 
						new Substitute(file, TextFormatting.YELLOW), 
						new Substitute(percent, TextFormatting.YELLOW)
						), 
				false);

		MessageMusic msg = new MessageMusic(MusicAction.VOLUME, file, percent);

		MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		return 0;
	}

	public int stopAllSelf(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		if(!CoreCommands.isPlayer(ctx.getSource())) {
			ctx.getSource().sendErrorMessage(LCLPMMO.TEXT.message("You must be a player to execute this command.", MessageType.ERROR));
			return 1;
		}

		ServerPlayerEntity p = ctx.getSource().asPlayer();

		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Stopping all music...", 
						TextFormatting.GREEN
						), 
				false);

		MessageMusic msg = new MessageMusic(MusicAction.STOP);

		MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		return 0;
	}

	public int stopSelf(CommandContext<CommandSource> ctx) throws CommandSyntaxException{
		if(!CoreCommands.isPlayer(ctx.getSource())) {
			ctx.getSource().sendErrorMessage(LCLPMMO.TEXT.message("You must be a player to execute this command.", MessageType.ERROR));
			return 1;
		}

		String file = ctx.getArgument("file", String.class);
		ServerPlayerEntity p = ctx.getSource().asPlayer();

		ctx.getSource().sendFeedback(
				LCLPMMO.TEXT.complexMessage(
						"Stopping music '%s'...", 
						TextFormatting.GREEN, 
						new Substitute(file, TextFormatting.YELLOW)
						), 
				false);

		MessageMusic msg = new MessageMusic(MusicAction.STOP, file);

		MMOPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), msg);
		return 0;
	}

}
