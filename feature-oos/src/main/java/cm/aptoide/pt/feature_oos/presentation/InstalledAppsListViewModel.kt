package cm.aptoide.pt.feature_oos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_oos.domain.InstalledAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstalledAppsListViewModel @Inject constructor(
  private val installedAppsUseCase: InstalledAppsUseCase
) : ViewModel() {

  private val viewModelState = MutableStateFlow<InstalledAppsUiState>(InstalledAppsUiState.Loading)

  val installedAppsState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      val installedApps = installedAppsUseCase.getInstalledApps()
      viewModelState.update { InstalledAppsUiState.Idle(installedApps) }
    }
  }
}
