package cm.aptoide.pt.feature_home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_home.domain.GetHomeBundlesListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BundlesViewModel @Inject constructor(
  private val getHomeBundlesListUseCase: GetHomeBundlesListUseCase,
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

  fun reload() {
    viewModelScope.launch {
      viewModelState.update { it.copy(type = BundlesViewUiStateType.LOADING) }
      getHomeBundlesListUseCase.execute(onStart = { }, onCompletion = { })
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
}
