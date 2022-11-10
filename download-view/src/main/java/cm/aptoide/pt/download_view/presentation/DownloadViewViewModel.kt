package cm.aptoide.pt.download_view.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.download_view.domain.model.DownloadStateMapper
import cm.aptoide.pt.download_view.domain.usecase.CancelDownloadUseCase
import cm.aptoide.pt.download_view.domain.usecase.DownloadAppUseCase
import cm.aptoide.pt.download_view.domain.usecase.ObserveDownloadUseCase
import cm.aptoide.pt.download_view.domain.usecase.OpenAppUseCase
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_campaigns.CampaignsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewViewModel @Inject constructor(
  private val downloadAppUseCase: DownloadAppUseCase,
  private val observeDownloadUseCase: ObserveDownloadUseCase,
  private val downloadStateMapper: DownloadStateMapper,
  private val cancelDownloadUseCase: CancelDownloadUseCase,
  private val openAppUseCase: OpenAppUseCase,
  private val campaignsUseCase: CampaignsUseCase
) :
  ViewModel() {

  private val viewModelState = MutableStateFlow(DownloadViewViewModelState())

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  fun downloadApp(app: App, isAppViewContext: Boolean) {
    viewModelScope.launch {
      downloadAppUseCase.downloadApp(app)
      if (isAppViewContext) {
        campaignsUseCase.getCampaign(app.packageName)?.sendClickEvent()
      }
    }
  }

  fun loadDownloadState(app: App) {
    viewModelState.update { it.copy(app = app, downloadViewType = mapDownloadViewType(app)) }
    viewModelScope.launch {
      observeDownloadUseCase.getDownload(app).catch { throwable -> throwable.printStackTrace() }
        .collect { download ->
          viewModelState.update {
            it.copy(
              app = app,
              downloadViewState = downloadStateMapper.mapDownloadState(download.downloadState),
              downloadProgress = download.progress
            )
          }
        }
    }
  }

  private fun mapDownloadViewType(app: App): DownloadViewType {
    return if (app.isAppCoins) {
      DownloadViewType.APPCOINS
    } else {
      DownloadViewType.NO_APPCOINS
    }
  }

  fun cancelDownload(app: App) {
    viewModelScope.launch {
      cancelDownloadUseCase.cancelDownload(app)
    }
  }

  fun openApp(app: App) {
    openAppUseCase.openApp(app.packageName)
  }
}

private data class DownloadViewViewModelState(
  val app: App? = null,
  val downloadViewType: DownloadViewType = DownloadViewType.NO_APPCOINS,
  val downloadViewState: DownloadViewState = DownloadViewState.INSTALL,
  val downloadProgress: Int = 0
) {

  fun toUiState(): DownloadViewUiState =
    DownloadViewUiState(
      app, downloadViewType, downloadViewState, downloadProgress
    )
}
