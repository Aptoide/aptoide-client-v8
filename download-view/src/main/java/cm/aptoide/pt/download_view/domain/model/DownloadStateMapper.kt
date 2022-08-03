package cm.aptoide.pt.download_view.domain.model

import cm.aptoide.pt.aptoide_installer.model.DownloadState
import cm.aptoide.pt.download_view.presentation.DownloadViewState
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class DownloadStateMapper @Inject constructor() {

  fun mapDownloadState(downloadState: DownloadState): DownloadViewState {
    return when (downloadState) {
      DownloadState.INSTALL -> {
        DownloadViewState.INSTALL
      }
      DownloadState.PROCESSING -> {
        DownloadViewState.PROCESSING
      }
      DownloadState.DOWNLOADING -> {
        DownloadViewState.DOWNLOADING
      }
      DownloadState.INSTALLING -> {
        DownloadViewState.INSTALLING
      }
      DownloadState.INSTALLED -> {
        DownloadViewState.INSTALLED
      }
      DownloadState.ERROR -> {
        DownloadViewState.ERROR
      }
      DownloadState.READY_TO_INSTALL -> {
        DownloadViewState.READY_TO_INSTALL
      }
    }
  }
}