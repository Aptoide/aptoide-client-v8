package com.aptoide.android.aptoidegames.developer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AGDeveloperOptionsViewModel @Inject constructor(
  private val agDeveloperPreferencesRepository: AGDeveloperPreferencesRepository
) : ViewModel() {

  private val viewModelState = MutableStateFlow<Boolean>(false)

  val areAGDeveloperOptionsEnabled = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      agDeveloperPreferencesRepository.areAGDeveloperOptionsEnabled()
        .catch { throwable -> throwable.printStackTrace() }
        .collect { devOptionsEnabled ->
          viewModelState.update { devOptionsEnabled }
        }
    }
  }

  fun setAGDeveloperOptionsState(enabled: Boolean) {
    viewModelScope.launch {
      agDeveloperPreferencesRepository.setAGDeveloperOptionsState(enabled)
    }
  }
}
