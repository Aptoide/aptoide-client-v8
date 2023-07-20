package cm.aptoide.pt.feature_home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.aptoide_network.domain.UrlsCache
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

class BundlesViewModel (
  private val urlsCache: UrlsCache,
  private val bundlesUseCase: BundlesUseCase,
  private val context: String?,
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

  private fun reload(loadingState: BundlesViewUiStateType) {
    viewModelScope.launch {
      viewModelState.update { it.copy(type = loadingState) }
      try {
        val result = bundlesUseCase.getHomeBundles(context = context)
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
    urlsCache.invalidate()
    reload(loadingState = BundlesViewUiStateType.RELOADING)
  }
}
