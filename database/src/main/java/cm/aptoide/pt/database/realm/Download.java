/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.database.realm;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import cm.aptoide.pt.database.R;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import lombok.EqualsAndHashCode;

public @EqualsAndHashCode class Download extends RealmObject {

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

  public String getStatusName(Context context) {
    String toReturn;
    switch (overallDownloadStatus) {
      case COMPLETED:
        toReturn = context.getString(R.string.download_completed);
        break;
      case PAUSED:
        toReturn = context.getString(R.string.download_paused);
        break;
      case PROGRESS:
        toReturn = context.getString(R.string.download_progress);
        break;
      case PENDING:
      case IN_QUEUE:
        toReturn = context.getString(R.string.download_queue);
        break;
      case INVALID_STATUS:
        toReturn =
            ""; //this state only appears while download manager doesn't get the download(before the AptoideDownloadManager#startDownload
        // method runs)
        break;
      case WARN:
      case BLOCK_COMPLETE:
      case CONNECTED:
      case RETRY:
      case STARTED:
      case NOT_DOWNLOADED:
      case ERROR:
      case FILE_MISSING:
      default:
        toReturn = getErrorMessage(context);
    }
    return toReturn;
  }

  @NonNull private String getErrorMessage(Context context) {
    String toReturn;
    if (downloadError == NOT_ENOUGH_SPACE_ERROR) {
      toReturn = context.getString(R.string.out_of_space_error);
    } else {
      toReturn = context.getString(R.string.simple_unstable_internet_connection);
    }
    return toReturn;
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

  @IntDef({
      INVALID_STATUS, COMPLETED, BLOCK_COMPLETE, CONNECTED, PENDING, PROGRESS, PAUSED, WARN,
      STARTED, ERROR, FILE_MISSING, RETRY, NOT_DOWNLOADED, IN_QUEUE
  })

  @Retention(RetentionPolicy.SOURCE)

  public @interface DownloadState {

  }

  @Retention(RetentionPolicy.SOURCE) @IntDef({ GENERIC_ERROR, NOT_ENOUGH_SPACE_ERROR })
  public @interface DownloadError {
  }
}
