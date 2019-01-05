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
    if (fileDownloadCallbackList.size() > 0) {
      return overallProgress / fileDownloadCallbackList.size();
    } else {
      return overallProgress;
    }
  }

  public AppDownloadState getDownloadStatus() {
    return appDownloadState;
  }

  private AppDownloadState getAppDownloadState() {
    AppDownloadStatus.AppDownloadState previousState = null;
    for (FileDownloadCallback fileDownloadCallback : fileDownloadCallbackList) {
      if (fileDownloadCallback.getDownloadState() == AppDownloadStatus.AppDownloadState.ERROR) {
        return AppDownloadState.ERROR;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.ERROR_FILE_NOT_FOUND) {
        return AppDownloadState.ERROR_FILE_NOT_FOUND;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.ERROR_NOT_ENOUGH_SPACE) {
        return AppDownloadState.ERROR_NOT_ENOUGH_SPACE;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.WARN) {
        return AppDownloadState.WARN;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.PAUSED) {
        return AppDownloadState.PAUSED;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.INVALID_STATUS) {
        return AppDownloadState.INVALID_STATUS;
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.COMPLETED) {
        if (previousState != null
            && previousState != AppDownloadStatus.AppDownloadState.COMPLETED) {
          return AppDownloadState.PROGRESS;
        } else if (fileDownloadCallbackList.indexOf(fileDownloadCallback)
            == fileDownloadCallbackList.size() - 1) {
          return AppDownloadState.COMPLETED;
        }
      } else if (fileDownloadCallback.getDownloadState()
          == AppDownloadStatus.AppDownloadState.PENDING) {
        if (previousState != null && previousState != AppDownloadStatus.AppDownloadState.PENDING) {
          return AppDownloadState.PROGRESS;
        } else if (fileDownloadCallbackList.indexOf(fileDownloadCallback)
            == fileDownloadCallbackList.size() - 1) {
          return AppDownloadState.PENDING;
        }
      }
      previousState = fileDownloadCallback.getDownloadState();
    }
    return AppDownloadState.PROGRESS;
  }

  public void setFileDownloadCallback(FileDownloadCallback fileDownloadCallback) {
    if (!fileDownloadCallbackList.contains(fileDownloadCallback)) {
      fileDownloadCallbackList.add(fileDownloadCallback);
    } else {
      int index = fileDownloadCallbackList.indexOf(fileDownloadCallback);
      fileDownloadCallbackList.set(index, fileDownloadCallback);
    }
    refreshAppDownloadState();
  }

  private void refreshAppDownloadState() {
    appDownloadState = getAppDownloadState();
  }

  public AppDownloadState getFileDownloadStatus(String md5) {

    FileDownloadCallback fileDownloadCallback = getFileDownloadCallback(md5);
    if (fileDownloadCallback == null) {
      return AppDownloadState.PROGRESS;
    } else {
      return fileDownloadCallback.getDownloadState();
    }
  }

  public int getFileDownloadProgress(String md5) {
    FileDownloadCallback fileDownloadCallback = getFileDownloadCallback(md5);
    if (fileDownloadCallback == null) {
      return 0;
    } else {
      return fileDownloadCallback.getDownloadProgress();
    }
  }

  private FileDownloadCallback getFileDownloadCallback(String md5) {
    for (FileDownloadCallback fileDownloadCallback : fileDownloadCallbackList) {
      if (fileDownloadCallback.getMd5()
          .equals(md5)) {
        return fileDownloadCallback;
      }
    }
    return null;
  }

  public enum AppDownloadState {
    INVALID_STATUS, COMPLETED, PENDING, PAUSED, WARN, ERROR, ERROR_FILE_NOT_FOUND, ERROR_NOT_ENOUGH_SPACE, PROGRESS
  }
}
