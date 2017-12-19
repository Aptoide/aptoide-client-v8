/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.database.realm;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Download extends RealmObject {

  public static final int ACTION_INSTALL = 0;
  public static final int ACTION_UPDATE = 1;
  public static final int ACTION_DOWNGRADE = 2;
  public static final String DOWNLOAD_ID = "appId";
  public static final String MD5 = "md5";
  public static final int INVALID_STATUS = 0;
  public static final int COMPLETED = 1;
  public static final int BLOCK_COMPLETE = 2;
  public static final int CONNECTED = 3;
  public static final int PENDING = 4;
  public static final int PROGRESS = 5;
  public static final int PAUSED = 6;
  public static final int WARN = 7;
  public static final int STARTED = 8;
  public static final int ERROR = 9;
  public static final int FILE_MISSING = 10;
  public static final int RETRY = 11;
  public static final int NOT_DOWNLOADED = 12;
  public static final int IN_QUEUE = 13;
  //errors
  public static final int NO_ERROR = 0;
  public static final int GENERIC_ERROR = 1;
  public static final int NOT_ENOUGH_SPACE_ERROR = 2;
  public static String TAG = Download.class.getSimpleName();
  RealmList<FileToDownload> filesToDownload;
  @DownloadState int overallDownloadStatus = 0;
  @IntRange(from = 0, to = 100) int overallProgress = 0;
  @PrimaryKey private String md5;
  private String appName;
  private String Icon;
  private long timeStamp;
  private int downloadSpeed;
  private String packageName;
  private int versionCode;
  private int action;
  private boolean scheduled;
  private String versionName;
  @Download.DownloadError private int downloadError;

  public Download() {
  }

  public @Download.DownloadError int getDownloadError() {
    return downloadError;
  }

  public void setDownloadError(int downloadError) {
    this.downloadError = downloadError;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public RealmList<FileToDownload> getFilesToDownload() {
    return filesToDownload;
  }

  public void setFilesToDownload(RealmList<FileToDownload> filesToDownload) {
    this.filesToDownload = filesToDownload;
  }

  public @DownloadState int getOverallDownloadStatus() {
    return overallDownloadStatus;
  }

  public void setOverallDownloadStatus(@DownloadState int overallDownloadStatus) {
    this.overallDownloadStatus = overallDownloadStatus;
  }

  public int getOverallProgress() {
    return overallProgress;
  }

  public void setOverallProgress(int overallProgress) {
    this.overallProgress = overallProgress;
  }

  public String getIcon() {
    return Icon;
  }

  public void setIcon(String icon) {
    Icon = icon;
  }

  public int getDownloadSpeed() {
    return downloadSpeed;
  }

  public void setDownloadSpeed(int speed) {
    this.downloadSpeed = speed;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }

  public boolean isScheduled() {
    return scheduled;
  }

  public void setScheduled(boolean scheduled) {
    this.scheduled = scheduled;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public String getVersionName() {
    return versionName;
  }

  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }

  @Override public int hashCode() {
    int result = getOverallDownloadStatus();
    result = 31 * result + getOverallProgress();
    result = 31 * result + getMd5().hashCode();
    result = 31 * result + (getAppName() != null ? getAppName().hashCode() : 0);
    result = 31 * result + (getIcon() != null ? getIcon().hashCode() : 0);
    result = 31 * result + (int) (getTimeStamp() ^ (getTimeStamp() >>> 32));
    result = 31 * result + getDownloadSpeed();
    result = 31 * result + (getPackageName() != null ? getPackageName().hashCode() : 0);
    result = 31 * result + getVersionCode();
    result = 31 * result + getAction();
    result = 31 * result + (isScheduled() ? 1 : 0);
    result = 31 * result + (getVersionName() != null ? getVersionName().hashCode() : 0);
    result = 31 * result + getDownloadError();
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Download download = (Download) o;

    if (getOverallDownloadStatus() != download.getOverallDownloadStatus()) return false;
    if (getOverallProgress() != download.getOverallProgress()) return false;
    if (getTimeStamp() != download.getTimeStamp()) return false;
    if (getDownloadSpeed() != download.getDownloadSpeed()) return false;
    if (getVersionCode() != download.getVersionCode()) return false;
    if (getAction() != download.getAction()) return false;
    if (isScheduled() != download.isScheduled()) return false;
    if (getDownloadError() != download.getDownloadError()) return false;
    if (!getMd5().equals(download.getMd5())) return false;
    if (getAppName() != null ? !getAppName().equals(download.getAppName())
        : download.getAppName() != null) {
      return false;
    }
    if (getIcon() != null ? !getIcon().equals(download.getIcon()) : download.getIcon() != null) {
      return false;
    }
    if (getPackageName() != null ? !getPackageName().equals(download.getPackageName())
        : download.getPackageName() != null) {
      return false;
    }
    return getVersionName() != null ? getVersionName().equals(download.getVersionName())
        : download.getVersionName() == null;
  }

  @IntDef({
      INVALID_STATUS, COMPLETED, BLOCK_COMPLETE, CONNECTED, PENDING, PROGRESS, PAUSED, WARN,
      STARTED, ERROR, FILE_MISSING, RETRY, NOT_DOWNLOADED, IN_QUEUE
  })

  @Retention(RetentionPolicy.SOURCE)

  public @interface DownloadState {

  }

  @Retention(RetentionPolicy.SOURCE) @IntDef({ GENERIC_ERROR, NOT_ENOUGH_SPACE_ERROR, NO_ERROR })
  public @interface DownloadError {
  }
}
