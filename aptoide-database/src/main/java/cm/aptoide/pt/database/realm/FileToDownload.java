package cm.aptoide.pt.database.realm;

import android.support.annotation.IntDef;
import android.text.TextUtils;
import cm.aptoide.pt.utils.IdUtils;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by trinkes on 5/16/16.
 */
public class FileToDownload extends RealmObject {

  public static final int APK = 0;
  public static final int OBB = 1;
  public static final int GENERIC = 2;

  @PrimaryKey private String md5;
  private int downloadId;
  private String altLink;
  private String link;
  private String packageName;
  private String path;
  private @FileType int fileType = GENERIC;
  private int progress;
  private @Download.DownloadState int status;
  private String fileName;
  private int versionCode;
  private String versionName;

  public static FileToDownload createFileToDownload(String link, String altLink, String md5,
      String fileName, @FileType int fileType, String packageName, int versionCode,
      String versionName) {
    FileToDownload fileToDownload = new FileToDownload();
    fileToDownload.setLink(link);
    fileToDownload.setMd5(md5);
    fileToDownload.setAltLink(altLink);
    fileToDownload.versionCode = versionCode;
    fileToDownload.versionName = versionName;
    fileToDownload.setFileType(fileType);
    if (!TextUtils.isEmpty(fileName)) {
      if (fileType == APK) {
        fileToDownload.setFileName(fileName + ".apk");
      } else {
        fileToDownload.setFileName(fileName);
      }
    }
    fileToDownload.setPackageName(packageName);
    return fileToDownload;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public String getVersionName() {
    return versionName;
  }

  public String getAltLink() {
    return altLink;
  }

  public void setAltLink(String altLink) {
    this.altLink = altLink;
  }

  public @Download.DownloadState int getStatus() {
    return status;
  }

  public void setStatus(@Download.DownloadState int status) {
    this.status = status;
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

  public int getDownloadId() {
    return downloadId;
  }

  public void setDownloadId(int downloadId) {
    this.downloadId = downloadId;
  }

  public @FileType int getFileType() {
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

  public String getFilePath() {
    return getPath() + getFileName();
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getFileName() {
    if (TextUtils.isEmpty(fileName)) {
      return TextUtils.isEmpty(getMd5()) ? IdUtils.randomString() : getMd5();
    }
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  @IntDef({ APK, OBB, GENERIC }) @Retention(RetentionPolicy.SOURCE) public @interface FileType {

  }
}
