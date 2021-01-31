package work.lclpnet.mmo.render.model;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractMMOPlayerModel extends PlayerModel<AbstractClientPlayerEntity> {

	public AbstractMMOPlayerModel(float modelSize, boolean smallArmsIn) {
		super(modelSize, smallArmsIn);
	}

	public abstract ResourceLocation getTextureLocation();

}
