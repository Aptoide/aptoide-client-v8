package cm.aptoide.pt.database.realm;

import android.support.annotation.IntDef;
import android.text.TextUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.IdUtils;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by trinkes on 5/16/16.
 */

public @EqualsAndHashCode class FileToDownload extends RealmObject {

  public static final int APK = 0;
  public static final int OBB = 1;
  public static final int GENERIC = 2;

  @PrimaryKey private String md5;
  private int downloadId;
  //private long appId;

  private String altLink;
  private String link;
  private String packageName;
  private String path;
  private @FileType int fileType = GENERIC;
  private int progress;
  private @Download.DownloadState int status;
  private String fileName;
  @Getter private int versionCode;
  @Getter private String versionName;

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

    String imeis[] = AptoideUtils.SystemU.getImeis();
    String ip;
    String email;
    String imei = imeis[0];
    String imei2 = imeis[1];
    String serialNumber;
    String model = AptoideUtils.SystemU.getModel();
    String appVersion;
    String appName;
    String androidVersion = String.valueOf(AptoideUtils.SystemU.getSdkVer());
    String language = AptoideUtils.SystemU.getCountryCode();
    String country;
    String city;
    String state;
    String zip;
    String latitude;
    String longitude;
    String mobileNumber = AptoideUtils.SystemU.getPhoneNumber();
    String googleAdId;
    String carrierName = AptoideUtils.SystemU.getCarrierName();
    String mcc = AptoideUtils.SystemU.getMCC();
    String mnc = AptoideUtils.SystemU.getMNC();
    String manufacturer;

    return path;
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

  public void setPath(String path) {
    this.path = path;
  }

  //@Override protected FileToDownload clone() {
  //  FileToDownload clone = new FileToDownload();
  //  clone.setAppId(getAppId());
  //  if (this.getLink() != null) {
  //    clone.setLink(new String(this.getLink()));
  //  }
  //  clone.setStatus(this.getStatus());
  //  if (this.getPath() != null) {
  //    clone.setPath(new String(this.getPath()));
  //  }
  //  if (this.getPackageName() != null) {
  //    clone.setPackageName(new String(this.getPackageName()));
  //  }
  //  clone.setDownloadId(this.getDownloadId());
  //  clone.setFileType(this.getFileType());
  //  clone.setProgress(this.getProgress());
  //  clone.versionCode = versionCode;
  //  if (this.getMd5() != null) {
  //    clone.setMd5(new String(this.getMd5()));
  //  }
  //  if (this.getFileName() != null) {
  //    clone.setFileName(new String(this.getFileName()));
  //  }
  //  if (!TextUtils.isEmpty(this.getAltLink())) {
  //    clone.setAltLink(this.getAltLink());
  //  }
  //
  //  return clone;
  //}

  @IntDef({ APK, OBB, GENERIC }) @Retention(RetentionPolicy.SOURCE) public @interface FileType {

  }
}
