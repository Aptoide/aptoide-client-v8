package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.logger.Logger;
import java.util.List;

/**
 * Created by filipegoncalves on 8/28/18.
 */

public class AppDownloadStatus {

  private static final int PROGRESS_MAX_VALUE = 100;
  private String md5;
  private List<FileDownloadCallback> fileDownloadCallbackList;
  private AppDownloadState appDownloadState;
  private long downloadSize;

  public AppDownloadStatus(String md5, List<FileDownloadCallback> fileDownloadCallbackList,
      AppDownloadState appDownloadState, long downloadSize) {
    this.md5 = md5;
    this.fileDownloadCallbackList = fileDownloadCallbackList;
    this.appDownloadState = appDownloadState;
    this.downloadSize = downloadSize;
  }

  public String getMd5() {
    return md5;
  }

  public int getOverallProgress() {
    if (downloadSize == 0) {
      return calculateProgressByFileNumber(getOverallProgressAsPercentage());
    } else {
      return calculateProgressByFileSize(getOverallProgressAsBytes());
    }
  }

  public boolean isAppDownloadOver() {
    return appDownloadState == AppDownloadState.COMPLETED
        || appDownloadState == AppDownloadState.PAUSED
        || appDownloadState == AppDownloadState.ERROR
        || appDownloadState == AppDownloadState.ERROR_FILE_NOT_FOUND
        || appDownloadState == AppDownloadState.ERROR_NOT_ENOUGH_SPACE
        || appDownloadState == AppDownloadState.ERROR_MD5_DOES_NOT_MATCH
        || appDownloadState == AppDownloadState.WAITING_TO_MOVE_FILES
        || appDownloadState == AppDownloadState.VERIFYING_FILE_INTEGRITY;
  }

  public DownloadSpeed getAverageDownloadSpeed() {
    long averageApkDownloadSpeed = getAverageDownloadSpeedByFileType(DownloadAppFile.FileType.APK);
    long averageObbDownloadSpeed = getAverageDownloadSpeedByFileType(DownloadAppFile.FileType.OBB);
    long averageSplitDownloadSpeed =
        getAverageDownloadSpeedByFileType(DownloadAppFile.FileType.SPLIT);
    return new DownloadSpeed(averageApkDownloadSpeed, averageObbDownloadSpeed,
        averageSplitDownloadSpeed);
  }

  private long getAverageDownloadSpeedByFileType(DownloadAppFile.FileType fileType) {
    int totalAverageSpeed = 0;
    int numberOfFilesByType = 0;
    int numberOfFilesStarted = 1;
    for (FileDownloadCallback fileDownloadCallback : fileDownloadCallbackList) {
      if (fileDownloadCallback.getFileType() == fileType.getType()) {
        numberOfFilesByType++;
        totalAverageSpeed += fileDownloadCallback.getDownloadSpeed();
        if (fileDownloadCallback.getDownloadState() != AppDownloadState.PENDING
            && fileDownloadCallback.getDownloadState() != AppDownloadState.WARN) {
          numberOfFilesStarted++;
        }
      }
    }
    if (numberOfFilesByType <= 0) {
      return -1;
    } else if (totalAverageSpeed > 0) {
      return totalAverageSpeed / numberOfFilesStarted;
    } else {
      return 0;
    }
  }

  private long getOverallProgressAsBytes() {
    long overallProgress = 0;
    for (FileDownloadCallback fileDownloadCallback : fileDownloadCallbackList) {
      overallProgress += fileDownloadCallback.getDownloadProgress()
          .getDownloadedBytes();
    }
    return overallProgress;
  }

  private int getOverallProgressAsPercentage() {
    int overallProgress = 0;
    for (FileDownloadCallback fileDownloadCallback : fileDownloadCallbackList) {
      int percentageOfTotalProgress = getFileDownloadProgressAsPercentage(fileDownloadCallback);

      overallProgress += percentageOfTotalProgress;
    }
    return overallProgress;
  }

  private int getFileDownloadProgressAsPercentage(FileDownloadCallback fileDownloadCallback) {
    return (int) Math.floor((double) fileDownloadCallback.getDownloadProgress()
        .getDownloadedBytes() / fileDownloadCallback.getDownloadProgress()
        .getTotalFileBytes() * PROGRESS_MAX_VALUE);
  }

  private int calculateProgressByFileSize(long overallProgress) {
    double result = (double) overallProgress / downloadSize;
    return (int) (result * 100);
  }

  private int calculateProgressByFileNumber(int overallProgress) {
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
      if (fileDownloadCallback.getDownloadState() != null) {
        switch (fileDownloadCallback.getDownloadState()) {
          case ERROR:
            return AppDownloadState.ERROR;
          case ERROR_MD5_DOES_NOT_MATCH:
            return AppDownloadState.ERROR_MD5_DOES_NOT_MATCH;
          case ERROR_FILE_NOT_FOUND:
            return AppDownloadState.ERROR_FILE_NOT_FOUND;
          case ERROR_NOT_ENOUGH_SPACE:
            return AppDownloadState.ERROR_NOT_ENOUGH_SPACE;
          case WARN:
            return AppDownloadState.WARN;
          case PAUSED:
            return AppDownloadState.PAUSED;
          case INVALID_STATUS:
            return AppDownloadState.INVALID_STATUS;
          case VERIFYING_FILE_INTEGRITY:
            if (previousState != null && (previousState
                != AppDownloadStatus.AppDownloadState.VERIFYING_FILE_INTEGRITY
                && previousState != AppDownloadState.COMPLETED)) {
              return AppDownloadState.PROGRESS;
            } else if (fileDownloadCallbackList.indexOf(fileDownloadCallback)
                == fileDownloadCallbackList.size() - 1) {
              return AppDownloadState.VERIFYING_FILE_INTEGRITY;
            }
            break;
          case COMPLETED:
            if (previousState != null
                && previousState != AppDownloadStatus.AppDownloadState.COMPLETED) {
              return AppDownloadState.PROGRESS;
            } else if (fileDownloadCallbackList.indexOf(fileDownloadCallback)
                == fileDownloadCallbackList.size() - 1) {
              Logger.getInstance()
                  .d("AppDownloadState", "emitting APPDOWNLOADSTATE completed " + md5);
              return AppDownloadState.COMPLETED;
            }
            break;
          case PENDING:
            if (previousState != null
                && previousState != AppDownloadStatus.AppDownloadState.PENDING) {
              return AppDownloadState.PROGRESS;
            } else if (fileDownloadCallbackList.indexOf(fileDownloadCallback)
                == fileDownloadCallbackList.size() - 1) {
              return AppDownloadState.PENDING;
            }
            break;
        }
        previousState = fileDownloadCallback.getDownloadState();
      }
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
      return getFileDownloadProgressAsPercentage(fileDownloadCallback);
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
    INVALID_STATUS, COMPLETED, PENDING, PAUSED, WARN, ERROR, ERROR_FILE_NOT_FOUND, ERROR_NOT_ENOUGH_SPACE, ERROR_MD5_DOES_NOT_MATCH, PROGRESS, WAITING_TO_MOVE_FILES, VERIFYING_FILE_INTEGRITY
  }
}
