package cm.aptoide.pt.download_view.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.aptoide_installer.InstallManager
import cm.aptoide.pt.feature_apps.data.App
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewViewModel @Inject constructor(var installManager: InstallManager) : ViewModel() {

  private val viewModelState = MutableStateFlow(DownloadViewViewModelState())

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  fun downloadApp(app: App) {
    viewModelScope.launch {
      installManager.download(app.packageName)
    }
  }
}

private data class DownloadViewViewModelState(
  val app: App? = null,
  val downloadViewType: DownloadViewType = DownloadViewType.NO_APPCOINS,
  val downloadViewState: DownloadViewState = DownloadViewState.INSTALL
) {

  fun toUiState(): DownloadViewUiState =
    DownloadViewUiState(
      app, downloadViewType, downloadViewState
    )

}