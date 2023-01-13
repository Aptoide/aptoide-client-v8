package cm.aptoide.pt.feature_home.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_home.domain.GetMoreAppsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreBundleViewViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val getMoreAppsListUseCase: GetMoreAppsListUseCase
) : ViewModel() {

  private val bundleIdentifier: String? = savedStateHandle.get("bundleIdentifier")
  private val viewModelState = MutableStateFlow(MoreBundleViewViewModelState())

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    viewModelScope.launch {
      bundleIdentifier?.let {
        getMoreAppsListUseCase.getMoreBundle(bundleIdentifier)
          .catch { throwable -> throwable.printStackTrace() }
          .collect { bundle ->
            viewModelState.update {
              it.copy(bundle.first, false)
            }
          }
      }
    }
  }
}


private data class MoreBundleViewViewModelState(
  val appList: List<App> = emptyList(),
  val isLoading: Boolean = false,
) {

  fun toUiState(): MoreBundleViewUiState =
    MoreBundleViewUiState(
      appList,
      isLoading
    )
}