package cm.aptoide.pt.feature_home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_home.domain.BundlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BundlesViewModel @Inject constructor(
  private val bundlesUseCase: BundlesUseCase
) : ViewModel() {

  private val viewModelState = MutableStateFlow(
    BundlesViewUiState(
      bundles = emptyList(),
      type = BundlesViewUiStateType.LOADING
    )
  )

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    reload(loadingState = BundlesViewUiStateType.LOADING)
  }

  private fun reload(
    bypassCache: Boolean = false,
    loadingState: BundlesViewUiStateType,
  ) {
    viewModelScope.launch {
      viewModelState.update { it.copy(type = loadingState) }
      try {
        val result = bundlesUseCase.getHomeBundles(bypassCache = bypassCache)
        viewModelState.update { it.copy(bundles = result, type = BundlesViewUiStateType.IDLE) }
      } catch (e: Throwable) {
        Timber.w(e)
        viewModelState.update {
          it.copy(
            type = when (e) {
              is IOException -> BundlesViewUiStateType.NO_CONNECTION
              else -> BundlesViewUiStateType.ERROR
            }
          )
        }
      }
    }
  }

  fun loadFreshHomeBundles() {
    reload(
      bypassCache = true,
      loadingState = BundlesViewUiStateType.RELOADING,
    )
  }
}
