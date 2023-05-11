package cm.aptoide.pt.feature_appview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_appview.domain.AppInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class AppViewModel constructor(
  private val appInfoUseCase: AppInfoUseCase,
  private val packageName: String,
  private val adListId: String?,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<AppUiState>(AppUiState.Loading)

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
      viewModelState.update { AppUiState.Loading }
      appInfoUseCase.getAppInfo(packageName)
        .map { app ->
          app.campaigns?.adListId = adListId
          app
        }
        .catch { e ->
          Timber.w(e)
          viewModelState.update {
            when (e) {
              is IOException -> AppUiState.NoConnection
              else -> AppUiState.Error
            }
          }
        }
        .collect { app ->
          viewModelState.update {
            app.campaigns?.sendImpressionEvent()
            AppUiState.Idle(app)
          }
        }
    }
  }
}
