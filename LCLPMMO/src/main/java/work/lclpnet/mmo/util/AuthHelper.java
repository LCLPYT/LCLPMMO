package work.lclpnet.mmo.util;

import java.lang.reflect.Field;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class AuthHelper {

	public static void setLoginStateNegotiating(ServerLoginNetHandler handler) throws ReflectiveOperationException {
		setLoginState(handler, 3); // NEGOTIATING
	}
	
	public static void setLoginStateDelayAccept(ServerLoginNetHandler handler) throws ReflectiveOperationException {
		setLoginState(handler, 5); // DELAY_ACCEPT
	}
	
	public static void setLoginState(ServerLoginNetHandler handler, int i) throws ReflectiveOperationException {
		Field f = ObfuscationReflectionHelper.findField(ServerLoginNetHandler.class, "field_147328_g");
		f.setAccessible(true);
		Class<?> enumClass = Class.forName("net.minecraft.network.login.ServerLoginNetHandler$State");
		Object[] enumConstants = enumClass.getEnumConstants();
		f.set(handler, enumConstants[i]);
	}
	
	public static GameProfile getGameProfile(ServerLoginNetHandler handler) {
		GameProfile profile;
		try {
			Field f = ObfuscationReflectionHelper.findField(ServerLoginNetHandler.class, "field_147337_i");
			f.setAccessible(true);
			profile = (GameProfile) f.get(handler);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			profile = null;
		}
		
		if(profile == null || profile.getId() == null) return null;
		return profile;
	}
	
}
