package cm.aptoide.pt.downloadmanager;

import java.util.List;

/**
 * Created by filipegoncalves on 8/28/18.
 */

public class AppDownloadStatus {

  private String md5;
  private List<FileDownloadCallback> fileDownloadCallbackList;
  private AppDownloadState appDownloadState;

  public AppDownloadStatus(String md5, List<FileDownloadCallback> fileDownloadCallbackList,
      AppDownloadState appDownloadState) {
    this.md5 = md5;
    this.fileDownloadCallbackList = fileDownloadCallbackList;
    this.appDownloadState = appDownloadState;
  }

  public String getMd5() {
    return md5;
  }

  public int getOverallProgress() {
    int overallProgress = 0;
    for (FileDownloadCallback fileDownloadCallback : fileDownloadCallbackList) {
      overallProgress += fileDownloadCallback.getDownloadProgress();
    }
    return overallProgress;
  }

  public AppDownloadState getDownloadStatus() {
    return appDownloadState;
  }

  public void setAppDownloadState(AppDownloadState appDownloadState) {
    this.appDownloadState = appDownloadState;
  }

  public void setFileDownloadCallback(FileDownloadCallback fileDownloadCallback) {
    if (!fileDownloadCallbackList.contains(fileDownloadCallback)) {
      fileDownloadCallbackList.add(fileDownloadCallback);
    } else {
      int index = fileDownloadCallbackList.indexOf(fileDownloadCallback);
      fileDownloadCallbackList.set(index, fileDownloadCallback);
    }
  }

  public enum AppDownloadState {
    INVALID_STATUS, COMPLETED, PENDING, PAUSED, WARN, ERROR, PROGRESS
  }
}
