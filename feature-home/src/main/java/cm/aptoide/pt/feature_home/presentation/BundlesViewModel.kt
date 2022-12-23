package cm.aptoide.pt.feature_home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_home.domain.GetHomeBundlesListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BundlesViewModel @Inject constructor(
  getHomeBundlesListUseCase: GetHomeBundlesListUseCase,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(
    BundlesViewUiState(
      bundles = emptyList(),
      isLoading = true
    )
  )

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      getHomeBundlesListUseCase.execute(onStart = { }, onCompletion = { })
        .catch { e ->
          Timber.w(e)
          viewModelState.update { it.copy(isLoading = false) }
        }
        .collect { result ->
          viewModelState.update { it.copy(bundles = result, isLoading = false) }
        }
    }
  }
}
