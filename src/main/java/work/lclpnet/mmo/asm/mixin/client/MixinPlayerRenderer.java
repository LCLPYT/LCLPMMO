package work.lclpnet.mmo.asm.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.util.ResourceLocation;
import work.lclpnet.mmo.render.ClientRenderHandler;
import work.lclpnet.mmo.render.MMOPlayerRenderer;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {

	@Redirect(
			method = "Lnet/minecraft/client/renderer/entity/PlayerRenderer;renderItem("
					+ "Lcom/mojang/blaze3d/matrix/MatrixStack;"
					+ "Lnet/minecraft/client/renderer/IRenderTypeBuffer;"
					+ "I"
					+ "Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;"
					+ "Lnet/minecraft/client/renderer/model/ModelRenderer;"
					+ "Lnet/minecraft/client/renderer/model/ModelRenderer;"
					+ ")V",
					at = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;getLocationSkin()"
									+ "Lnet/minecraft/util/ResourceLocation;"
							)
			)
	public ResourceLocation onGetSkinLocation(AbstractClientPlayerEntity player) {
		MMOPlayerRenderer renderer = ClientRenderHandler.getPlayerRenderer(player);
		return renderer != null ? renderer.textureLocation : player.getLocationSkin();
	}
	
}
