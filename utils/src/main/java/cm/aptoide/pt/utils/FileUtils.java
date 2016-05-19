package cm.aptoide.pt.utils;

import java.io.File;

/**
 * Created by trinkes on 5/18/16.
 */
public class FileUtils {

	public static boolean fileExists(String path) {
		return new File(path).exists();
	}
}
