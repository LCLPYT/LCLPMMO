package work.lclpnet.mmo.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import work.lclpnet.mmo.block.MMOBlocks;

public class RenderLayerHandler {

	private static Map<Block, RenderType> types = new HashMap<>();
	
	static {
		final RenderType translucent = RenderType.getTranslucent();
		types.put(MMOBlocks.GLASS_BOTTLE_EMPTY, translucent);
	}
	
	public static void init() {
		types.forEach(RenderTypeLookup::setRenderLayer);
	}
	
}
