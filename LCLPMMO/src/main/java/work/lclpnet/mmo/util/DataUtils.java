package work.lclpnet.mmo.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

public class DataUtils {

	public static void uncompressedToGzBase64(InputStream uncompressedIn, OutputStream gzBase64Out) throws IOException {
		try (Base64OutputStream base64Out = new Base64OutputStream(gzBase64Out, true, 0, null);
				GZIPOutputStream gzipOut = new GZIPOutputStream(base64Out)) {
			byte[] buffer = new byte[1024];
			int read;
			while((read = uncompressedIn.read(buffer)) != -1)
				gzipOut.write(buffer, 0, read);
		}
	}
	
	public static void gzBase64ToUncompressed(InputStream gzBase64In, OutputStream uncompressedOut) throws IOException {
		try (Base64InputStream base64In = new Base64InputStream(gzBase64In);
				GZIPInputStream gzipIn = new GZIPInputStream(base64In)) {
			byte[] buffer = new byte[1024];
			int read;
			while((read = gzipIn.read(buffer)) != -1) 
				uncompressedOut.write(buffer, 0, read);
		}
	}
	
}
