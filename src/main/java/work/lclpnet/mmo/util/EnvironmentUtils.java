package work.lclpnet.mmo.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

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
