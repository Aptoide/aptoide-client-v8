package com.aptoide.android.aptoidegames.gamegenie.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameGenieSharedPreferencesRepository @Inject constructor(
  @ApplicationContext context: Context
) : GameGenieLocalRepository {

  companion object {
    private const val PREFS_NAME = "game_genie_prefs"
    private const val KEY_HAS_CLICKED_OVERLAY_BUTTON = "has_clicked_overlay_button"
    private const val KEY_SCREENSHOT_PATH = "screenshot_path"
    private const val KEY_SCREENSHOT_TIMESTAMP = "screenshot_timestamp"
  }

  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  override suspend fun hasClickedOverlayButton(): Boolean = withContext(Dispatchers.IO) {
    prefs.getBoolean(KEY_HAS_CLICKED_OVERLAY_BUTTON, false)
  }

  override suspend fun setClickedOverlayButton(clicked: Boolean) = withContext(Dispatchers.IO) {
    prefs.edit { putBoolean(KEY_HAS_CLICKED_OVERLAY_BUTTON, clicked) }
  }

  override suspend fun getScreenshotPath(): String? = withContext(Dispatchers.IO) {
    prefs.getString(KEY_SCREENSHOT_PATH, null)
  }

  override suspend fun getScreenshotTimestamp(): Long = withContext(Dispatchers.IO) {
    prefs.getLong(KEY_SCREENSHOT_TIMESTAMP, 0L)
  }

  override suspend fun saveScreenshot(path: String, timestamp: Long) = withContext(Dispatchers.IO) {
    prefs.edit {
      putString(KEY_SCREENSHOT_PATH, path)
      putLong(KEY_SCREENSHOT_TIMESTAMP, timestamp)
    }
  }

  override suspend fun clearScreenshot() = withContext(Dispatchers.IO) {
    prefs.edit {
      remove(KEY_SCREENSHOT_PATH)
      remove(KEY_SCREENSHOT_TIMESTAMP)
    }
  }
}
