package work.lclpnet.mmo.util;

import net.minecraft.util.text.TextFormatting;
import work.lclpnet.corebase.util.MessageType;
import work.lclpnet.corebase.util.TextComponentHelper;

public class MessageUtils {

	public static final MessageType WARN = new MessageType(itc -> TextComponentHelper.applyTextStyle(itc, TextFormatting.YELLOW));
	
}
