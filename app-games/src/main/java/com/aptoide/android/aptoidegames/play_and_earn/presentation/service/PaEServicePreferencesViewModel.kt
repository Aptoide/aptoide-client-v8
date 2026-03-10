package com.aptoide.android.aptoidegames.play_and_earn.presentation.service

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.play_and_earn.PlayAndEarnManager
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaEServicePreferencesViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val paEPreferencesRepository: PaEPreferencesRepository,
  private val playAndEarnManager: PlayAndEarnManager,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(true)

  val isPaEServiceEnabled = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      paEPreferencesRepository.isPaEServiceEnabled()
        .catch { throwable -> throwable.printStackTrace() }
        .collect { enabled ->
          viewModelState.update { enabled }
        }
    }
  }

  fun setPaEServiceEnabled(enabled: Boolean) {
    viewModelScope.launch {
      paEPreferencesRepository.setPaEServiceEnabled(enabled)
      if (enabled) {
        if (playAndEarnManager.shouldShowPlayAndEarn()) {
          PaEForegroundService.start(context)
        }
      } else {
        PaEForegroundService.stop(context)
      }
    }
  }
}
