package cm.aptoide.pt.aptoide_installer.model

import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import cm.aptoide.pt.installedapps.domain.model.InstalledAppState

class DownloadStateMapper {
  fun mapDownloadState(
    downloadStatus: Int,
    installedAppState: InstalledAppState
  ): DownloadState {
    return when (installedAppState) {
      InstalledAppState.INSTALLED -> {
        DownloadState.INSTALLED
      }
      InstalledAppState.INSTALLING -> {
        DownloadState.INSTALLING
      }
      InstalledAppState.NOT_INSTALLED -> {
        DownloadState.INSTALL
      }
      else -> {
        mapDownloadingStatus(downloadStatus)
      }
    }
  }

  private fun mapDownloadingStatus(downloadStatus: Int): DownloadState {
    when (downloadStatus) {
      DownloadEntity.INVALID_STATUS -> {
        return DownloadState.PROCESSING
      }
      DownloadEntity.COMPLETED -> {
        return DownloadState.READY_TO_INSTALL
      }
      DownloadEntity.PENDING -> {
        return DownloadState.PROCESSING
      }
      DownloadEntity.PROGRESS -> {
        return DownloadState.DOWNLOADING
      }
      DownloadEntity.WARN -> {
        return DownloadState.DOWNLOADING
      }
      DownloadEntity.ERROR -> {
        return DownloadState.ERROR
      }
      DownloadEntity.FILE_MISSING -> {
        return DownloadState.ERROR
      }
      DownloadEntity.IN_QUEUE -> {
        return DownloadState.PROCESSING
      }
      DownloadEntity.WAITING_TO_MOVE_FILES -> {
        return DownloadState.PROCESSING
      }
      DownloadEntity.VERIFYING_FILE_INTEGRITY -> {
        return DownloadState.PROCESSING
      }
      else -> {
        return DownloadState.ERROR
      }
    }
  }
}