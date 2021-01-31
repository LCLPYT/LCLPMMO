package work.lclpnet.mmo.render.model;

import net.minecraft.util.ResourceLocation;
import work.lclpnet.mmo.LCLPMMO;

public class VampirePlayerModel extends AbstractMMOPlayerModel {

	public VampirePlayerModel() {
		super(0F, false);
	}

	@Override
	public ResourceLocation getTextureLocation() {
		return new ResourceLocation(LCLPMMO.MODID, "textures/entity/vampire/vampire.png");
	}

}
