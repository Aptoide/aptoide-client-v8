/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.downloads_database.data.database.model;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "download") public class DownloadEntity {

  @Ignore public static final int ACTION_INSTALL = 0;
  @Ignore public static final int ACTION_UPDATE = 1;
  @Ignore public static final int ACTION_DOWNGRADE = 2;
  @Ignore public static final String DOWNLOAD_ID = "appId";
  @Ignore public static final String MD5 = "md5";
  @Ignore public static final int INVALID_STATUS = 0;
  @Ignore public static final int COMPLETED = 1;
  @Ignore public static final int PENDING = 4;
  @Ignore public static final int PROGRESS = 5;
  @Ignore public static final int PAUSED = 6;
  @Ignore public static final int WARN = 7;
  @Ignore public static final int ERROR = 9;
  @Ignore public static final int FILE_MISSING = 10;
  @Ignore public static final int IN_QUEUE = 13;
  @Ignore public static final int WAITING_TO_MOVE_FILES = 14;
  @Ignore public static final int VERIFYING_FILE_INTEGRITY = 15;

  //errors
  @Ignore public static final int NO_ERROR = 0;
  @Ignore public static final int NOT_ENOUGH_SPACE_ERROR = 2;
  @Ignore public static final int GENERIC_ERROR = 1;
  public static String TAG = DownloadEntity.class.getSimpleName();
  List<FileToDownload> filesToDownload;
  @DownloadState int overallDownloadStatus = 0;
  @IntRange(from = 0, to = 100) int overallProgress = 0;
  @PrimaryKey @NonNull private String md5;
  private String appName;
  private String Icon;
  private long timeStamp;
  private int downloadSpeed;
  private String packageName;
  private int versionCode;
  private int action;
  private String versionName;
  private boolean hasAppc;
  private long size;
  private String storeName;
  private String trustedBadge;
  @DownloadError private int downloadError;
  private String attributionId;

  public DownloadEntity() {
  }

  public @DownloadError int getDownloadError() {
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

  public List<FileToDownload> getFilesToDownload() {
    return filesToDownload;
  }

  public void setFilesToDownload(List<FileToDownload> filesToDownload) {
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

  public boolean hasAppc() {
    return hasAppc;
  }

  public void setHasAppc(boolean hasAppc) {
    this.hasAppc = hasAppc;
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
    result = 31 * result + (getVersionName() != null ? getVersionName().hashCode() : 0);
    result = 31 * result + getDownloadError();
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DownloadEntity download = (DownloadEntity) o;

    if (getOverallDownloadStatus() != download.getOverallDownloadStatus()) return false;
    if (getOverallProgress() != download.getOverallProgress()) return false;
    if (getTimeStamp() != download.getTimeStamp()) return false;
    if (getDownloadSpeed() != download.getDownloadSpeed()) return false;
    if (getVersionCode() != download.getVersionCode()) return false;
    if (getAction() != download.getAction()) return false;
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

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public boolean hasSplits() {
    for (FileToDownload roomFileToDownload : filesToDownload) {
      if (roomFileToDownload.getFileType() == FileToDownload.SPLIT) {
        return true;
      }
    }
    return false;
  }

  public boolean hasObbs() {
    for (FileToDownload roomFileToDownload : filesToDownload) {
      if (roomFileToDownload.getFileType() == FileToDownload.OBB) {
        return true;
      }
    }
    return false;
  }

  public List<FileToDownload> getSplits() {
    List<FileToDownload> splitsList = new ArrayList<>();
    for (FileToDownload fileToDownload : filesToDownload) {
      if (fileToDownload.getFileType() == FileToDownload.SPLIT) {
        splitsList.add(fileToDownload);
      }
    }
    return splitsList;
  }

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public String getTrustedBadge() {
    return trustedBadge;
  }

  public void setTrustedBadge(String trustedBadge) {
    this.trustedBadge = trustedBadge;
  }

  public String getAttributionId() {
    return attributionId;
  }

  public void setAttributionId(String oemId) {
    attributionId = oemId;
  }

  @IntDef({
      INVALID_STATUS, COMPLETED, PENDING, PROGRESS, PAUSED, WARN, ERROR, FILE_MISSING, IN_QUEUE,
      WAITING_TO_MOVE_FILES, VERIFYING_FILE_INTEGRITY
  })

  @Retention(RetentionPolicy.SOURCE)

  public @interface DownloadState {

  }

  @Retention(RetentionPolicy.SOURCE) @IntDef({ GENERIC_ERROR, NOT_ENOUGH_SPACE_ERROR, NO_ERROR })
  public @interface DownloadError {
  }
}
