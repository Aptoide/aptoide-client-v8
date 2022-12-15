package cm.aptoide.pt.feature_home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_home.data.BundlesResult
import cm.aptoide.pt.feature_home.domain.GetHomeBundlesListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
      getHomeBundlesListUseCase.execute(
        onStart = { },
        onCompletion = { },
        onError = { Timber.d(it) }
      )
        .collect { result ->
          viewModelState.update {
            when (result) {
              is BundlesResult.Success -> it.copy(bundles = result.data, isLoading = false)
              is BundlesResult.Error -> it.copy(isLoading = false)
            }
          }
        }
    }
  }
}
