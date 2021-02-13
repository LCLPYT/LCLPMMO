package work.lclpnet.mmo.asm.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.render.ClientRenderHandler;
import work.lclpnet.mmo.render.MMOPlayerRenderer;

@Mixin(FirstPersonRenderer.class)
public class MixinFirstPersonRenderer {

	@Shadow
	@Final
	private Minecraft mc;

	@Redirect(
			method = "Lnet/minecraft/client/renderer/FirstPersonRenderer;renderArm("
					+ "Lcom/mojang/blaze3d/matrix/MatrixStack;"
					+ "Lnet/minecraft/client/renderer/IRenderTypeBuffer;"
					+ "I"
					+ "Lnet/minecraft/util/HandSide;"
					+ ")V",
					at = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture("
									+ "Lnet/minecraft/util/ResourceLocation;"
									+ ")V"
							)
			)
	public void onArmTexture(TextureManager texMan, ResourceLocation loc) {
		bindMMOPlayerTexture(texMan, loc);
	}
	
	@Redirect(
			method = "renderArmFirstPerson(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IFFLnet/minecraft/util/HandSide;)V",
					at = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture("
									+ "Lnet/minecraft/util/ResourceLocation;"
									+ ")V"
							)
			)
	public void onArmFirstPersonTexture(TextureManager texMan, ResourceLocation loc) {
		bindMMOPlayerTexture(texMan, loc);
	}

	private void bindMMOPlayerTexture(TextureManager texMan, ResourceLocation loc) {
		MMOPlayerRenderer renderer = ClientRenderHandler.getPlayerRenderer(this.mc.player);
		if(renderer != null && renderer.textureLocation != null) texMan.bindTexture(renderer.textureLocation);
		else texMan.bindTexture(loc);
	}
	
}
