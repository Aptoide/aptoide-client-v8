package com.aptoide.android.aptoidegames.updates.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.updates.repository.UpdatesPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatesPreferencesViewModel @Inject constructor(
  private val updatesPreferencesRepository: UpdatesPreferencesRepository
) : ViewModel() {

  private val viewModelState = MutableStateFlow(true)

  val shouldAutoUpdateGames = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      updatesPreferencesRepository.shouldAutoUpdateGames()
        .catch { throwable -> throwable.printStackTrace() }
        .collect { shouldAutoUpdateGames ->
          viewModelState.update { shouldAutoUpdateGames }
        }
    }
  }

  fun setAutoUpdateGames(shouldAutoUpdateGames: Boolean) {
    viewModelScope.launch {
      updatesPreferencesRepository.setAutoUpdateGames(shouldAutoUpdateGames)
    }
  }
}
