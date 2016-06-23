package cm.aptoide.pt.database.realm;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cm.aptoide.pt.utils.IdUtils;
import io.realm.RealmObject;

/**
 * Created by trinkes on 5/16/16.
 */

public class FileToDownload extends RealmObject {

	public static final int APK = 0;
	public static final int OBB = 1;
	public static final int GENERIC = 2;
	private String link;
	private String packageName;
	private String path;
	private int downloadId;
	private long appId;
	private int fileType = GENERIC;
	private int progress;
	private String md5;
	private String fileName;

	/**
	 * @param link
	 * @param appId
	 * @param md5
	 * @param fileName
	 * @param fileType use FileTypeConverter class to get this value
	 *
	 * @return
	 */
	public static FileToDownload createFileToDownload(String link, long appId, String md5, String fileName, int
			fileType) {
		FileToDownload fileToDownload = new FileToDownload();
		fileToDownload.setLink(link);
		fileToDownload.setAppId(appId);
		fileToDownload.setMd5(md5);
		if (!TextUtils.isEmpty(fileName)) {
			fileToDownload.setFileName(fileName);
		}
		fileToDownload.setFileType(fileType);

		return fileToDownload;
	}

	public static FileToDownload createFileToDownload(String link, long appId, String md5, String fileName, int
			fileType, String packageName) {
		FileToDownload fileToDownload = createFileToDownload(link, appId, md5, fileName, fileType);
		fileToDownload.setPackageName(packageName);
		return fileToDownload;
	}

	public String getFileName() {
		if (TextUtils.isEmpty(fileName)) {
			fileName = TextUtils.isEmpty(getMd5()) ? IdUtils.randomString() : getMd5();
		}
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(int downloadId) {
		this.downloadId = downloadId;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public
	@FileType
	int getFileType() {
		return fileType;
	}

	public void setFileType(@FileType int fileType) {
		this.fileType = fileType;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getFilePath() {
		return path + fileName;
	}

	@IntDef({APK, OBB, GENERIC})
	@Retention(RetentionPolicy.SOURCE)
	public @interface FileType {

	}
}
