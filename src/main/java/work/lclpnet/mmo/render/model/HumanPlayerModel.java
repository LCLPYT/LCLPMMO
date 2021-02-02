package work.lclpnet.mmo.render.model;

import net.minecraft.util.ResourceLocation;

public class HumanPlayerModel extends AbstractMMOPlayerModel {

	public HumanPlayerModel(boolean smallArmsIn) {
		super(0F, smallArmsIn);
	}

	@Override
	public ResourceLocation getTextureLocation() {
		return null;
	}

}
