package cm.aptoide.pt.feature_updates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_updates.domain.usecase.GetInstalledAppsUseCase
import cm.aptoide.pt.feature_updates.domain.usecase.OpenInstalledAppUseCase
import cm.aptoide.pt.installedapps.domain.model.InstalledApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatesViewModel @Inject constructor(
  private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
  private val openInstalledAppUseCase: OpenInstalledAppUseCase
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
    viewModelScope.launch {
      getInstalledAppsUseCase.getInstalledApps().collect { installedAppsList ->
        viewModelState.update { it.copy(installedAppsList = installedAppsList) }
      }
    }
  }

  fun onOpenInstalledApp(packageName: String) {
    openInstalledAppUseCase.openInstalledApp(packageName = packageName)
  }

  fun onUninstallApp(packageName: String) {
  }

}

private data class UpdatesViewModelState(val installedAppsList: List<InstalledApp>) {
  fun toUiState(): UpdatesUiState = UpdatesUiState(installedAppsList)
}