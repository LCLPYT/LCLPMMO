package work.lclpnet.mmo.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.Random;

public class MMOModelRenderer extends ModelRenderer {

	protected float mmoTranslateX = 0F, 
			mmoTranslateY = 0F, 
			mmoTranslateZ = 0F;
	
	public MMOModelRenderer(Model model) {
		super(model);
	}
	
	public void setMMOTranslations(float x, float y, float z) {
		this.mmoTranslateX = x;
		this.mmoTranslateY = y;
		this.mmoTranslateZ = z;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		matrixStackIn.push();
		matrixStackIn.translate(mmoTranslateX, mmoTranslateY, mmoTranslateZ);
		super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		matrixStackIn.pop();
	}

	@Override
	public ModelBox getRandomCube(Random randomIn) {
		try {
			return super.getRandomCube(randomIn);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
}
