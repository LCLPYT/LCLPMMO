package work.lclpnet.mmo.asm.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.IToast.Visibility;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SystemToast.class)
public class MixinSystemToast {

	@Shadow
	private long firstDrawTime;
	@Shadow
	public List<IReorderingProcessor> field_238531_e_;
	@Shadow
	public ITextComponent title;
	
	/**
	 * Makes the unreachable condition with <code>SystemToast.field_238531_e_ == null</code> possible again.
	 * This will make the effect of a SystemToast instance with a null subtitle the same as prior 1.16.x. 
	 */
	@Inject(
			method = "Lnet/minecraft/client/gui/toasts/SystemToast;func_230444_a_("
					+ "Lcom/mojang/blaze3d/matrix/MatrixStack;"
					+ "Lnet/minecraft/client/gui/toasts/ToastGui;"
					+ "J"
					+ ")Lnet/minecraft/client/gui/toasts/IToast$Visibility;",
					at = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/gui/toasts/ToastGui;blit("
									+ "Lcom/mojang/blaze3d/matrix/MatrixStack;"
									+ "IIIIII"
									+ ")V",
									shift = Shift.AFTER
							),
					cancellable = true
			)
	public void afterBlit(MatrixStack mStack, ToastGui toastGui, long delta, CallbackInfoReturnable<Visibility> cir) {
		if(!field_238531_e_.isEmpty()) return;
		
		toastGui.getMinecraft().fontRenderer.func_243248_b(mStack, this.title, 18.0F, 12.0F, -256);
		
		cir.setReturnValue(delta - this.firstDrawTime < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE);
		cir.cancel();
	}

}
