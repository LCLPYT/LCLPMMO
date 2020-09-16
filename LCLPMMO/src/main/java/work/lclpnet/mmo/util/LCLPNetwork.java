package work.lclpnet.mmo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
	public static void setAccessToken(String token, Consumer<Boolean> callback) {
		accessToken = token;
		
		new Thread(() -> {
			File f = getAuthFile();
			if(!f.exists()) {
				f.getParentFile().mkdirs();
				try(OutputStream out = new FileOutputStream(new File(".auth", "_README.txt"))) {
					out.write(I18n.format("warn.auth.token").getBytes(StandardCharsets.UTF_8));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try (OutputStream out = new FileOutputStream(f)) {
				out.write(token.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "access token saver").start();
	}
	
	private static File getAuthFile() {
		return new File(".auth", "lclpnetwork.token");
	}
	
	public static String getAccessToken() {
		return accessToken;
	}
	
	public static void sendRequest() {
		// TODO
	}
	
}
