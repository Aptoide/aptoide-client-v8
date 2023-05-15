package cm.aptoide.pt.feature_home.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_home.domain.BundlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MoreBundleViewViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val bundlesUseCase: BundlesUseCase
) : ViewModel() {

  private val bundleTag: String? = savedStateHandle["tag"]
  private val viewModelState = MutableStateFlow(MoreBundleViewViewModelState())

  val uiState = viewModelState.map { it.toUiState() }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value.toUiState()
    )

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      viewModelState.update { it.copy(type = MoreBundleViewUiStateType.LOADING) }
      bundleTag?.let { tag ->
        try {
          val apps = bundlesUseCase.getMoreBundle(tag)
          viewModelState.update {
            it.copy(
              appList = apps,
              type = MoreBundleViewUiStateType.IDLE
            )
          }
        } catch (throwable: Throwable) {
          Timber.w(throwable)
          viewModelState.update {
            it.copy(
              type = when (throwable) {
                is IOException -> MoreBundleViewUiStateType.NO_CONNECTION
                else -> MoreBundleViewUiStateType.ERROR
              }
            )
          }
        }
      }
    }
  }
}

private data class MoreBundleViewViewModelState(
  val appList: List<App> = emptyList(),
  val type: MoreBundleViewUiStateType = MoreBundleViewUiStateType.IDLE,
) {
  fun toUiState(): MoreBundleViewUiState =
    MoreBundleViewUiState(
      appList = appList,
      type = type
    )
}
