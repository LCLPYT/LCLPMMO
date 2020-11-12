package work.lclpnet.mmo.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class EnvironmentUtils {

	public static File getTmpDir() {
		return new File("_tmp");
	}

	public static void deleteTmpDir() {
		try {
			File tmpDir = getTmpDir();
			if(tmpDir.exists()) FileUtils.forceDelete(tmpDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
