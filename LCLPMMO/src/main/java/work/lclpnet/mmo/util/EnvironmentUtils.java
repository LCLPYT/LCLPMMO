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
			FileUtils.forceDelete(getTmpDir());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
