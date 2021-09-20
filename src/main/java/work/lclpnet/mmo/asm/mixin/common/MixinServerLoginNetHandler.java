package work.lclpnet.mmo.asm.mixin.common;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import work.lclpnet.lclpnetwork.api.APIException;
import work.lclpnet.lclpnetwork.api.APIResponse;
import work.lclpnet.lclpnetwork.api.ResponseEvaluationException;
import work.lclpnet.mmo.asm.helpers.HelperServerLoginNetHandler;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.facade.DummyMMOUser;
import work.lclpnet.mmo.util.network.MMOAPI;

import java.util.concurrent.CompletionException;

@Mixin(ServerLoginNetHandler.class)
public class MixinServerLoginNetHandler {

    @Shadow
    private ServerPlayerEntity player;
    @Shadow
    @Final
    private MinecraftServer server;
    @Shadow
    public GameProfile loginGameProfile;
    @Shadow
    @Final
    public NetworkManager networkManager;

    @Inject(
            method = "tryAcceptPlayer()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/management/PlayerList;getPlayerByUUID(Ljava/util/UUID;)Lnet/minecraft/entity/player/ServerPlayerEntity;",
                    shift = Shift.BY,
                    by = 2
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    public void onAcceptPlayer(CallbackInfo ci, ITextComponent itextcomponent, final ServerPlayerEntity serverplayerentity) {
        final ServerLoginNetHandler handler = (ServerLoginNetHandler) (Object) this;
        final GameProfile profile = handler.loginGameProfile != null ? (handler.loginGameProfile.getId() != null ? handler.loginGameProfile : null) : null;

        ci.cancel();

        if (profile == null) return;

        if (FMLEnvironment.dist == Dist.CLIENT
                && HelperServerLoginNetHandler.ifOnClient(this.server, profile, serverplayerentity, handler, this.loginGameProfile, this.networkManager, x -> this.player = x))
            return;

        IMMOUser mmo = new DummyMMOUser();
        MMOAPI.PUBLIC.getActiveCharacterByUuid(profile.getId().toString(), true)
                .whenComplete((character, err) -> {
                    if (err == null) return;

                    if (APIException.NO_CONNECTION.equals(err)) handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.lclpnetwork_down"));
                    else if (err instanceof ResponseEvaluationException) {
                        APIResponse resp = ((ResponseEvaluationException) err).getResponse();
                        if (resp.getResponseCode() == 406) handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.not_linked"));
                    } else {
                        handler.disconnect(new StringTextComponent("Internal server error."));
                        err.printStackTrace();
                    }

                    throw new CompletionException(new IllegalStateException("Could not fetch active character."));
                })
                .thenCompose(character -> {
                    if (character == null) {
                        handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.no_character"));
                        throw new NullPointerException("No character");
                    } else {
                        mmo.setMMOCharacter(character);
                        return MMOAPI.PUBLIC.getUserById(character.owner);
                    }
                })
                .whenComplete((character, err) -> {
                    if (err == null) return;

                    if (APIException.NO_CONNECTION.equals(err)) handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.lclpnetwork_down"));
                    else if (err instanceof ResponseEvaluationException) {
                        APIResponse resp = ((ResponseEvaluationException) err).getResponse();
                        if (resp.getResponseCode() == 204) handler.disconnect(new StringTextComponent("User not found."));
                    } else {
                        handler.disconnect(new StringTextComponent("Internal server error."));
                        err.printStackTrace();
                    }

                    throw new CompletionException(new IllegalStateException("Could not fetch user."));
                })
                .thenAccept(user -> {
                    mmo.setUser(user);
                    HelperServerLoginNetHandler.resolve(serverplayerentity, handler, this.server, loginGameProfile, this.networkManager, mmo, x -> this.player = x);
                })
                .exceptionally(err -> null); // handle uncaught exceptions (ignore them)
    }

    @Redirect(
            method = "tryAcceptPlayer()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/NetworkManager;sendPacket(Lnet/minecraft/network/IPacket;)V"
            )
    )
    public void onAcceptPlayerRemLoginSuccess(NetworkManager nm, IPacket<?> packet) {
        // this empty redirect is necessary, because otherwise the user would not see login errors. We will set this later in HelperServerLoginNetHandler.resolve
    }
}
