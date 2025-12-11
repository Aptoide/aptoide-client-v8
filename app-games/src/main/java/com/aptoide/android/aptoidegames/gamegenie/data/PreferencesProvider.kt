package com.aptoide.android.aptoidegames.gamegenie.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesInjectionsProvider @Inject constructor(
  val repository: GameGenieLocalRepository,
) : ViewModel()

@Composable
fun rememberGameGeniePreferences(): GameGenieLocalRepository = runPreviewable(
  preview = {
    object : GameGenieLocalRepository {
      override suspend fun hasClickedOverlayButton(): Boolean = false
      override suspend fun setClickedOverlayButton(clicked: Boolean) {}
      override suspend fun getScreenshotPath(): String? = null
      override suspend fun getScreenshotTimestamp(): Long = 0L
      override suspend fun saveScreenshot(path: String, timestamp: Long) {}
      override suspend fun clearScreenshot() {}
    }
  },
  real = {
    val preferencesProvider = hiltViewModel<PreferencesInjectionsProvider>()

    val gameGeniePreferences by remember(key1 = preferencesProvider.repository) {
      derivedStateOf {
        preferencesProvider.repository
      }
    }
    gameGeniePreferences
  }
)
