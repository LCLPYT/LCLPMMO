package work.lclpnet.mmo.util.network;

import com.google.gson.JsonElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.facade.JsonSerializeable;
import work.lclpnet.mmo.facade.User;

import javax.annotation.Nullable;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

public class LCLPNetwork {

	private static String accessToken = null;
	private static boolean online = false;
	public static final IPrivateBackend BACKEND = IPrivateBackend.NONE;

	public static void setAccessToken(String accessToken) {
		LCLPNetwork.accessToken = accessToken;
	}

	public static String getAccessToken() {
		return accessToken;
	}

	public static void checkAccessToken(Consumer<User> callback) {
		sendRequest("api/auth/user", "GET", null, resp -> {
			online = !resp.isNoConnection();
			if(resp.getResponseCode() == 200) {
				if(FMLEnvironment.dist == Dist.CLIENT) callback.accept(JsonSerializeable.parse(resp.getRawResponse(), User.class));
				else callback.accept(null);
			} else {
				if(resp.isNoConnection()) {
					callback.accept(null);
					return;
				}
				if(FMLEnvironment.dist == Dist.CLIENT) AccessTokenStorage.store(null, b -> {});
				else throw new IllegalStateException("Server access token is not valid!");
				callback.accept(null);
			}
		});
	}

	public static void sendRequest(String path, String requestMethod, @Nullable JsonElement body, @Nullable Consumer<HTTPResponse> callback) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(requestMethod);

		new Thread(() -> {
			try {
				URL url = new URL(String.format("%s/%s", Config.getEffectiveHost(), path));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod(requestMethod);
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
				if (accessToken != null)
					conn.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
				System.out.println(accessToken);

				if (body != null) {
					conn.setDoOutput(true);
					try (OutputStream out = conn.getOutputStream()) {
						out.write(body.toString().getBytes(StandardCharsets.UTF_8));
						out.flush();
					}
				}

				HTTPResponse response = HTTPResponse.fromRequest(conn);

				conn.disconnect();

				if(callback != null) callback.accept(response);
			} catch (ConnectException e) {
				if(callback != null) callback.accept(HTTPResponse.NO_CONNECTION);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}			
		}, "HTTP Request").start();
	}

	public static void post(String path, @Nullable JsonElement body, @Nullable Consumer<HTTPResponse> callback) {
		sendRequest(path, "POST", body, callback);
	}

	public static void logout() {
		sendRequest("api/auth/revoke-token", "GET", null, resp -> {
			AccessTokenStorage.store(null, b -> {});
			User.reloadUser(null, () -> {});
		});
	}

	@OnlyIn(Dist.CLIENT)
	public static boolean isLoggedIn() {
		return accessToken != null && User.getCurrent() != null;
	}
	
	public static boolean isOnline() {
		return online;
	}

	public static void setup(Runnable callback) {
		AccessTokenStorage.load(loaded -> LCLPNetwork.checkAccessToken(user -> {
			if(FMLEnvironment.dist == Dist.CLIENT) User.reloadUser(user, callback);
		}));
	}
	
}
