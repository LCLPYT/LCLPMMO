package work.lclpnet.mmo.asm.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.dedicated.DedicatedServer;
import work.lclpnet.mmo.LCLPMMO;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {
	
	@Inject(method = "Lnet/minecraft/server/dedicated/DedicatedServer;<init>("
			+ "Ljava/io/File;"
			+ "Lnet/minecraft/server/ServerPropertiesProvider;"
			+ "Lcom/mojang/datafixers/DataFixer;"
			+ "Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;"
			+ "Lcom/mojang/authlib/minecraft/MinecraftSessionService;"
			+ "Lcom/mojang/authlib/GameProfileRepository;"
			+ "Lnet/minecraft/server/management/PlayerProfileCache;"
			+ "Lnet/minecraft/world/chunk/listener/IChunkStatusListenerFactory;"
			+ "Ljava/lang/String;"
			+ ")V", at = @At("RETURN"))
	public void onInitEnd(CallbackInfo callbackInfo) {
		System.out.printf("Mixins are enabled for '%s'. (DEDICATED SERVER)\n", LCLPMMO.MODID);
	}
	
}
