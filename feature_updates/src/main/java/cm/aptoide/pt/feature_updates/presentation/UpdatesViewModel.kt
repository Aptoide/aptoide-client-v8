package cm.aptoide.pt.feature_updates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_updates.domain.Updates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UpdatesViewModel @Inject constructor(
  private val updates: Updates
) :
  ViewModel() {

  private val viewModelState = MutableStateFlow<UpdatesUiState>(UpdatesUiState.Loading)

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
      viewModelState.update { UpdatesUiState.Loading }
      updates.appsUpdates
        .catch {
          Timber.w(it)
          //No Errors For now
          viewModelState.update {
            UpdatesUiState.Empty
          }
        }
        .collect { result ->
          viewModelState.update {
            if (result.isEmpty()) {
              UpdatesUiState.Empty
            } else {
              UpdatesUiState.Idle(updatesList = result)
            }
          }
        }
    }
  }
}
