package cm.aptoide.pt.feature_apps.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class AppViewModel(
  private val appMetaUseCase: AppMetaUseCase,
  private val source: String,
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
      try {
        val app = appMetaUseCase.getMetaInfoBySource(source)
        app.campaigns?.adListId = adListId
        app.campaigns?.sendImpressionEvent()
        viewModelState.update { AppUiState.Idle(app) }
      } catch (e: Throwable) {
        Timber.w(e)
        viewModelState.update {
          when (e) {
            is IOException -> AppUiState.NoConnection
            else -> AppUiState.Error
          }
        }
      }
    }
  }
}
