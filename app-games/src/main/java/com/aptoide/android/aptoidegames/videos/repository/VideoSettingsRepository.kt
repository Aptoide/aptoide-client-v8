package com.dti.hub.videos.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoSettingsRepository @Inject constructor() {

  private val shouldVideosMute = MutableStateFlow(true)

  suspend fun setShouldVideosMute(shouldMute: Boolean) {
    shouldVideosMute.emit(shouldMute)
  }

  fun shouldVideosMute(): Flow<Boolean> = shouldVideosMute
}
