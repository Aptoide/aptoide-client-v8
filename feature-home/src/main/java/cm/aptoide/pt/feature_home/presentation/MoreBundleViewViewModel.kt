package cm.aptoide.pt.feature_home.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_home.domain.GetMoreAppsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
      bundleIdentifier?.let { tag ->
        getMoreAppsListUseCase.getMoreBundle(tag)
          .catch { throwable -> throwable.printStackTrace() }
          .collect { bundle ->
            viewModelState.update {
              it.copy(
                appList = bundle.first,
                isLoading = false
              )
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