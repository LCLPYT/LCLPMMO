package work.lclpnet.mmo.render;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.entity.PixieEntity;
import work.lclpnet.mmo.render.model.PixieModel;

public class PixieRenderer extends MobRenderer<PixieEntity, PixieModel> {
	
	public PixieRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new PixieModel(), 0.15F);
	}

	@Override
	public ResourceLocation getEntityTexture(PixieEntity entity) {
		return new ResourceLocation(LCLPMMO.MODID, "textures/entity/pixie.png");
	}

	@Override
	public void render(PixieEntity entity, float f1, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light) {
		super.render(entity, f1, partialTicks, matrixStack, renderTypeBuffer, light);
	}
	
}
