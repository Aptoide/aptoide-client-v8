package cm.aptoide.pt.download_view.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.download_view.domain.usecase.InstallAppUseCase
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_campaigns.CampaignsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DownloadViewViewModel constructor(
  private val app: App,
  private val installAppUseCaseInstance: InstallAppUseCase<*>,
  private val installedAppOpener: InstalledAppOpener,
  private val campaignsUseCase: CampaignsUseCase
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow(DownloadViewUiState(app = app, downloadViewType = app.getDownloadViewType()))

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      installAppUseCaseInstance.getCurrentState(app)
        .catch { throwable -> throwable.printStackTrace() }
        .collect { pair -> viewModelState.update { it.copyWith(pair) } }
    }
  }

  fun downloadApp(app: App, isAppViewContext: Boolean) {
    viewModelScope.launch {
      if (isAppViewContext) {
        campaignsUseCase.getCampaign(app.packageName)?.sendClickEvent()
      }
      installAppUseCaseInstance.install(app)
        .catch { throwable -> throwable.printStackTrace() }
        .collect { pair -> viewModelState.update { it.copyWith(pair) } }
    }
  }

  fun cancelDownload() {
    viewModelScope.launch {
      installAppUseCaseInstance.cancelInstallation(app)
    }
  }

  fun openApp() {
    installedAppOpener.openInstalledApp(app.packageName)
  }
}
