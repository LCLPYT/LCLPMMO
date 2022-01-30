package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mmo.client.MMOClient;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(
            method = "createPlayer(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/stat/StatHandler;Lnet/minecraft/client/recipebook/ClientRecipeBook;ZZ)Lnet/minecraft/client/network/ClientPlayerEntity;",
            at = @At("RETURN")
    )
    public void onCreatePlayer(ClientWorld world, StatHandler statHandler, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting, CallbackInfoReturnable<ClientPlayerEntity> cir) {
        MMOClient.initializeMyPlayer(cir.getReturnValue());
    }
}
