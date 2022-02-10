package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.client.gui.WorldMigrationScreen;

@Mixin(EditWorldScreen.class)
public class MixinEditWorldScreen {

    @Shadow @Final private LevelStorage.Session storageSession;

    @Inject(
            method = "init",
            at = @At("TAIL")
    )
    public void afterInit(CallbackInfo ci) {
        EditWorldScreen screen = (EditWorldScreen) (Object) this;
        ((ScreenAccessor) this).invokeAddButton(new ButtonWidget(screen.width / 2 - 100 + 200 + 4, screen.height / 4 + 96 + 5, 120, 20,
                new LiteralText("Migrate Namespaces"), button -> MinecraftClient.getInstance().openScreen(new ConfirmScreen(confirmed -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (confirmed) client.openScreen(new WorldMigrationScreen(screen, storageSession));
                    else client.openScreen(screen);
        }, new LiteralText("Migrate Namespaces"), new LiteralText("Do you really want to migrate all namespaces?")))));
    }
}
