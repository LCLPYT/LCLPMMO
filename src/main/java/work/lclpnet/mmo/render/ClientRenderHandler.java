package work.lclpnet.mmo.render;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import work.lclpnet.mmo.entity.MMOEntities;

@OnlyIn(Dist.CLIENT)
public class ClientRenderHandler {

	public static void setup() {
		RenderingRegistry.registerEntityRenderingHandler(MMOEntities.PIXIE, PixieRenderer::new);
	}
	
}
