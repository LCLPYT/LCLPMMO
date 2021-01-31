package work.lclpnet.mmo.render;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import work.lclpnet.mmo.entity.MMOEntities;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.facade.race.Races;
import work.lclpnet.mmo.render.model.VampirePlayerModel;

@OnlyIn(Dist.CLIENT)
public class ClientRenderHandler {

	private static final Map<MMORace, PlayerRenderer> renderers = new HashMap<>();
	
	public static void setup() {
		RenderingRegistry.registerEntityRenderingHandler(MMOEntities.PIXIE, PixieRenderer::new);
		
		registerPlayerModels();
	}

	public static void registerPlayerModels() {
		EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
		if(renderManager == null) throw new NullPointerException("Render manager is null");
		
		renderers.put(Races.getByName("dwarf"), new MMOPlayerRenderer(renderManager, new VampirePlayerModel()));
	}
	
	@Nullable
	public static PlayerRenderer getPlayerRenderer(AbstractClientPlayerEntity player) {
//		return renderers.get(Races.getByName("dwarf"));
		return null;
	}
	
}
