package work.lclpnet.mmo.util;

import net.minecraft.util.text.TextFormatting;
import work.lclpnet.corebase.util.MessageType;

public class MessageUtils {

	public static final MessageType WARN = new MessageType(itc -> itc.mergeStyle(TextFormatting.YELLOW));
	
}
