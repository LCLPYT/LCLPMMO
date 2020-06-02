package work.lclpnet.mmo.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import work.lclpnet.mmo.block.MMOBlocks;

@OnlyIn(Dist.CLIENT)
public class RenderLayerHandler {

	private static Map<Block, RenderType> types = new HashMap<>();
	
	static {
		final RenderType translucent = RenderType.getTranslucent();
		types.put(MMOBlocks.GLASS_BOTTLE, translucent);
	}
	
	public static void init() {
		types.forEach(RenderTypeLookup::setRenderLayer);
	}
	
}
