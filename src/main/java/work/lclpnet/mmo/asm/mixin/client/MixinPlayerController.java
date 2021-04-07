package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.event.custom.EntityRightClickedEvent;

@Mixin(PlayerController.class)
public class MixinPlayerController {

    @Inject(
            method = "Lnet/minecraft/client/multiplayer/PlayerController;createPlayer("
                    + "Lnet/minecraft/client/world/ClientWorld;"
                    + "Lnet/minecraft/stats/StatisticsManager;"
                    + "Lnet/minecraft/client/util/ClientRecipeBook;"
                    + ")Lnet/minecraft/client/entity/player/ClientPlayerEntity;",
            at = @At("RETURN")
    )
    public void onCreatePlayer(ClientWorld world, StatisticsManager stats, ClientRecipeBook recipes, CallbackInfoReturnable<ClientPlayerEntity> cir) {
        IMMOUser.initMyPlayer(cir.getReturnValue());
    }

    @Inject(
            method = "Lnet/minecraft/client/multiplayer/PlayerController;interactWithEntity("
                    + "Lnet/minecraft/entity/player/PlayerEntity;"
                    + "Lnet/minecraft/entity/Entity;"
                    + "Lnet/minecraft/util/Hand;"
                    + ")Lnet/minecraft/util/ActionResultType;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/common/ForgeHooks;onInteractEntity("
                            + "Lnet/minecraft/entity/player/PlayerEntity;"
                            + "Lnet/minecraft/entity/Entity;"
                            + "Lnet/minecraft/util/Hand;"
                            + ")Lnet/minecraft/util/ActionResultType;",
                    remap = false
            ),
            cancellable = true
    )
    public void onRightClickEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResultType> cir) {
        if (hand != Hand.MAIN_HAND) return;

        EntityRightClickedEvent event = new EntityRightClickedEvent(player, entity, true);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.setReturnValue(ActionResultType.PASS);
            cir.cancel();
        }
    }

}
