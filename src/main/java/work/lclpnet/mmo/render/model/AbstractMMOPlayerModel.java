package work.lclpnet.mmo.render.model;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import work.lclpnet.mmo.render.MMOModelRenderer;

public abstract class AbstractMMOPlayerModel extends PlayerModel<AbstractClientPlayerEntity> {

	public AbstractMMOPlayerModel(float modelSize, boolean smallArmsIn) {
		super(modelSize, smallArmsIn);
	}

	public abstract ResourceLocation getTextureLocation();

	public static void setMMOTranslation(ModelRenderer modelRenderer, float x, float y, float z) {
		MMOModelRenderer mmoRenderer = (MMOModelRenderer) modelRenderer;
		mmoRenderer.setMMOTranslations(x, y, z);
	}
	
}
