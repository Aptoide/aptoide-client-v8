package cm.aptoide.pt.downloadmanager.model;

import android.os.Environment;
import android.text.TextUtils;

import cm.aptoide.pt.utils.IdUtils;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created by trinkes on 5/16/16.
 */

@Accessors(chain = true)
@Data
public class FileToDownload {

	private static final String STORAGE_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/aptoide/";
	private static final String APK_PATH = STORAGE_PATH + "apks/";
	// TODO: 5/17/16 trinkes correct path
	private static final String OBB_PATH = STORAGE_PATH + "obb/";
	// TODO: 5/17/16 trinkes correct path
	private static final String GENERIC_PATH = STORAGE_PATH + "generic/";

	String link;    //mandatory
	String packageName;
	String filePath;
	int downloadId;
	long appId;
	FileType fileType = FileType.GENERIC;
	/**
	 * download progress between 0 and
	 * <li>{@link cm.aptoide.pt.downloadmanager.DownloadTask#PROGRESS_MAX_VALUE}</li>
	 */
	int progress;
	String md5;    //mandatory
	private String fileName;

	public String getFileName() {
		if (TextUtils.isEmpty(fileName)) {
			fileName = TextUtils.isEmpty(getMd5()) ? IdUtils.randomString() : getMd5();
		}
		return fileName;
	}

	public enum FileType {
		APK(APK_PATH), OBB(OBB_PATH), GENERIC(GENERIC_PATH),;

		@Getter String path;

		FileType(String path) {
			this.path = path;
		}
	}
}
