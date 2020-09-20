package work.lclpnet.mmo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class LCLPNetwork {

	private static String accessToken = null;
	
	public static void loadAccessToken(final Consumer<Boolean> callback) {
		new Thread(() -> {
			File f = getAuthFile();
			if(!f.exists()) {
				callback.accept(false);
				return;
			}
			
			try(InputStream in = new FileInputStream(f);
					ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				byte[] buffer = new byte[1024];
				int read;
				while((read = in.read(buffer, 0, buffer.length)) != -1) 
					out.write(buffer, 0, read);
				
				accessToken = new String(out.toByteArray());
				callback.accept(true);
			} catch (IOException e) {
				e.printStackTrace();
				callback.accept(false);
			}
		}, "access token loader").start();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void setAccessToken(@Nullable String token, final Consumer<Boolean> callback) {
		Objects.requireNonNull(callback);

		accessToken = token;
		
		new Thread(() -> {
			File f = getAuthFile();
			if(!f.exists()) {
				if(token == null) {
					callback.accept(true);
					return;
				}
				
				f.getParentFile().mkdirs();
				try(OutputStream out = new FileOutputStream(new File(".auth", "_README.txt"))) {
					out.write(I18n.format("warn.auth.token").getBytes(StandardCharsets.UTF_8));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if(f.exists() && token == null) {
				callback.accept(f.delete());
				return;
			}
			
			try (OutputStream out = new FileOutputStream(f)) {
				out.write(token.getBytes());
				callback.accept(true);
			} catch (IOException e) {
				e.printStackTrace();
				callback.accept(false);
			}
		}, "access token saver").start();
	}

	public static void checkAccessToken() {
		sendRequest("api/auth/user", "GET", null, resp -> {
			if(!resp.isNoConnection() && resp.getResponseCode() != 200) {
				if(FMLEnvironment.dist == Dist.CLIENT) setAccessToken(null, b -> {});
				else throw new IllegalStateException("Server access token is not valid!");
			}
		});
	}
	
	private static File getAuthFile() {
		return new File(".auth", "lclpnetwork.token");
	}
	
	public static String getAccessToken() {
		return accessToken;
	}
	
	public static void sendRequest(String path, String requestMethod, @Nullable JsonObject body, @Nullable Consumer<HTTPResponse> callback) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(requestMethod);

		new Thread(() -> {
			try {
				URL url = new URL(String.format("http://localhost:8000/%s", path));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod(requestMethod);
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
				if (accessToken != null)
					conn.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));

				if (body != null) {
					conn.setDoOutput(true);
					try (OutputStream out = conn.getOutputStream()) {
						out.write(body.toString().getBytes(StandardCharsets.UTF_8));
						out.flush();
					}
				}

				HTTPResponse response = HTTPResponse.fromRequest(conn);

				conn.disconnect();

				if (callback != null) callback.accept(response);
			} catch (ConnectException e) {
				if(callback != null) callback.accept(HTTPResponse.NO_CONNECTION);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "HTTP Request").start();
	}

	public static void logout() {
		sendRequest("api/auth/revoke-token", "GET", null, null);
		setAccessToken(null, b -> {});
	}
	
}
