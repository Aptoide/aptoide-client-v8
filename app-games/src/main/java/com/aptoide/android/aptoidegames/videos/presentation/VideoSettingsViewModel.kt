package com.dti.hub.videos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dti.hub.videos.repository.VideoSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoSettingsViewModel @Inject constructor(
  private val videoSettingsRepository: VideoSettingsRepository,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(false)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      videoSettingsRepository.shouldVideosMute()
        .catch { throwable -> throwable.printStackTrace() }
        .collect { shouldMute -> viewModelState.update { shouldMute } }
    }
  }

  fun setShouldMute(shouldMute: Boolean) {
    viewModelScope.launch {
      videoSettingsRepository.setShouldVideosMute(shouldMute)
    }
  }
}
