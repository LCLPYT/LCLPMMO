package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SystemToast.class)
public class MixinSystemToast {

    @Shadow private List<OrderedText> lines;

    @Shadow private Text title;

    @Shadow private long startTime;

    /**
     * Makes the unreachable condition with <code>SystemToast.field_238531_e_ == null</code> possible again.
     * This will make the effect of a SystemToast instance with a null subtitle the same as prior 1.16.x.
     */
    @Inject(
            method = "draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/toast/ToastManager;J)Lnet/minecraft/client/toast/Toast$Visibility;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/toast/ToastManager;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    public void afterDrawTexture(MatrixStack matrices, ToastManager manager, long startTime, CallbackInfoReturnable<Toast.Visibility> cir) {
        if (!lines.isEmpty()) return;

        manager.getGame().textRenderer.draw(matrices, this.title, 18.0F, 12.0F, -256);

        cir.setReturnValue(startTime - this.startTime < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE);
    }
}
