package cm.aptoide.pt.feature_home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_home.domain.BundlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
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
    reload()
  }

  private fun reload(
    bypassCache: Boolean = false,
    onStart: () -> Unit = { },
    onCompletion: () -> Unit = { },
  ) {
    viewModelScope.launch {
      viewModelState.update { it.copy(type = BundlesViewUiStateType.LOADING) }
      bundlesUseCase.getHomeBundles(bypassCache = bypassCache)
        .onStart { onStart() }
        .onCompletion { onCompletion() }
        .catch { e ->
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
        .collect { result ->
          viewModelState.update { it.copy(bundles = result, type = BundlesViewUiStateType.IDLE) }
        }
    }
  }

  fun loadFreshHomeBundles() {
    reload(
      bypassCache = true,
      onStart = { viewModelState.update { it.copy(type = BundlesViewUiStateType.RELOADING) } },
    )
  }
}
