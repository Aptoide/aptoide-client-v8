package cm.aptoide.pt.feature_appview.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_appview.domain.AppVersionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

class AppVersionsViewModel constructor(
  private val appVersionsUseCase: AppVersionsUseCase,
  private val packageName: String,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<AppVersionsUiState>(AppVersionsUiState.Loading)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      viewModelState.update { AppVersionsUiState.Loading }
      viewModelScope.launch {
        appVersionsUseCase.getAppVersions(packageName)
          .catch { e ->
            Timber.w(e)
            viewModelState.update {
              when (e) {
                is IOException -> AppVersionsUiState.NoConnection
                else -> AppVersionsUiState.Error
              }
            }
          }
          .collect { apps ->
            viewModelState.update {
              AppVersionsUiState.Idle(otherVersions = apps)
            }
          }
      }
    }
  }
}
