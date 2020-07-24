package cm.aptoide.pt.download.view


data class DownloadStatusModel(val action: Action, val progress: Int,
                               val downloadState: DownloadState) {


  fun isDownloading(): Boolean {
    return downloadState == DownloadState.ACTIVE || downloadState == DownloadState.PAUSE || downloadState == DownloadState.INDETERMINATE
  }

  fun isDownloadingOrInstalling(): Boolean {
    return isDownloading() || downloadState == DownloadState.INSTALLING
  }

  fun hasError(): Boolean {
    return downloadState == DownloadState.ERROR || downloadState == DownloadState.NOT_ENOUGH_STORAGE_ERROR
  }

  fun isDownloadable(): Boolean {
    return action == Action.INSTALL || action == Action.UPDATE || action == Action.DOWNGRADE
  }

  enum class Error {
    NETWORK, GENERIC
  }

  enum class Action {
    UPDATE, INSTALL, DOWNGRADE, OPEN, MIGRATE
  }

  enum class DownloadState {
    ACTIVE, PAUSE, COMPLETE, INDETERMINATE, ERROR, NOT_ENOUGH_STORAGE_ERROR, INSTALLING
  }
}