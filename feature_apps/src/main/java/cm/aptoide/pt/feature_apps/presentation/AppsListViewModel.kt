package cm.aptoide.pt.feature_apps.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.domain.AppsListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class AppsListViewModel(
  private val source: String,
  private val appsListUseCase: AppsListUseCase,
) :
  ViewModel() {
  private val viewModelState = MutableStateFlow<AppsListUiState>(AppsListUiState.Loading)

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
      viewModelState.update { AppsListUiState.Loading }
      try {
        val result = appsListUseCase.getAppsList(source)
        viewModelState.update {
          if (result.isEmpty()) {
            AppsListUiState.Empty
          } else {
            AppsListUiState.Idle(apps = result)
          }
        }
      } catch (t: Throwable) {
        Timber.w(t)
        viewModelState.update {
          when (t) {
            is IOException -> AppsListUiState.NoConnection
            else -> AppsListUiState.Error
          }
        }
      }
    }
  }
}
