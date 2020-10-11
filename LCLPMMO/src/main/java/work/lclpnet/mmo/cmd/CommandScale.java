package work.lclpnet.mmo.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.corebase.cmd.CommandBase;
import work.lclpnet.corebase.cmd.CoreCommands;
import work.lclpnet.corebase.util.MessageType;
import work.lclpnet.corebase.util.TextComponentHelper;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.cmd.args.ScaleArgumentType;
import work.lclpnet.mmo.util.MMOMonsterAttributes;

public class CommandScale extends CommandBase{

	public CommandScale() {
		super("scale");
	}

	@Override
	protected LiteralArgumentBuilder<CommandSource> transform(LiteralArgumentBuilder<CommandSource> builder) {
		return builder
				.requires(CoreCommands::permLevel2)
				.then(Commands.literal("set")
						.then(Commands.argument("target", EntityArgument.entities())
								.then(Commands.argument("scale", FloatArgumentType.floatArg(0F, 127F))
										.executes(this::setSize))))
				.then(Commands.literal("reset")
						.then(Commands.argument("target", EntityArgument.entities())
								.executes(this::resetSize)))
				.then(Commands.literal("setwh")
						.then(Commands.argument("target", EntityArgument.entities())
								.then(Commands.argument("widthHeight", ScaleArgumentType.scale())
										.executes(this::setWH))));
	}

	public int setSize(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");
		float scale = ctx.getArgument("scale", Float.class);

		List<LivingEntity> scaled = new ArrayList<>();
		for(Entity en : entities) {
			if(!(en instanceof LivingEntity)) continue;
			scaled.add((LivingEntity) en);
			MMOMonsterAttributes.setScale(en, scale);
		}

		if(scaled.isEmpty()) throw new CommandException(TextComponentHelper.appendSibling(
				(IFormattableTextComponent) LCLPMMO.TEXT.message("", MessageType.ERROR), 
				new TranslationTextComponent("cmd.scale.set.no_valid")));

		ctx.getSource().sendFeedback(new TranslationTextComponent("cmd.scale.set.success", scale, EntitySelector.joinNames(scaled).getString()), false);

		return 0;
	}

	public int resetSize(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");

		List<LivingEntity> scaled = new ArrayList<>();
		for(Entity en : entities) {
			if(!(en instanceof LivingEntity)) continue;
			scaled.add((LivingEntity) en);
			MMOMonsterAttributes.setScale(en, 1F);
		}

		if(scaled.isEmpty()) throw new CommandException(TextComponentHelper.appendSibling(
				(IFormattableTextComponent) LCLPMMO.TEXT.message("", MessageType.ERROR), 
				new TranslationTextComponent("cmd.scale.set.no_valid")));

		ctx.getSource().sendFeedback(new TranslationTextComponent("cmd.scale.set.success", 1F, EntitySelector.joinNames(scaled).getString()), false);

		return 0;
	}

	public int setWH(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "target");

		List<LivingEntity> scaled = new ArrayList<>();
		for(Entity en : entities) {
			if(!(en instanceof LivingEntity)) continue;
			scaled.add((LivingEntity) en);
			
			MMOMonsterAttributes.setScale(en, ScaleArgumentType.getVec2f(ctx, "widthHeight", en));
		}

		if(scaled.isEmpty()) throw new CommandException(new TranslationTextComponent("cmd.scale.set.no_valid"));

		ctx.getSource().sendFeedback(new TranslationTextComponent("cmd.scale.set.success_rel", EntitySelector.joinNames(scaled).getString()), false);

		return 0;
	}

}
