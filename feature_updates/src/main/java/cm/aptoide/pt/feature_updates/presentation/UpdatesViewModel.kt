package cm.aptoide.pt.feature_updates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_updates.domain.InstalledApp
import cm.aptoide.pt.feature_updates.domain.usecase.GetInstalledAppsUseCase
import cm.aptoide.pt.feature_updates.domain.usecase.UninstallAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class UpdatesViewModel @Inject constructor(
  private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
  private val installedAppOpener: InstalledAppOpener,
  private val uninstallAppUseCase: UninstallAppUseCase,
) :
  ViewModel() {

  private val viewModelState = MutableStateFlow(
    UpdatesViewModelState(emptyList())
  )

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    getInstalledAppsUseCase.getInstalledApps()
      .onEach { installedAppsList ->
        viewModelState.update { it.copy(installedAppsList = installedAppsList) }
      }
      .launchIn(viewModelScope)
  }

  fun onOpenInstalledApp(packageName: String) =
    installedAppOpener.openInstalledApp(packageName = packageName)

  fun onUninstallApp(packageName: String) =
    uninstallAppUseCase.uninstallApp(packageName = packageName)
}

private data class UpdatesViewModelState(val installedAppsList: List<InstalledApp>) {
  fun toUiState(): UpdatesUiState = UpdatesUiState(installedAppsList)
}