package com.aptoide.android.aptoidegames.apkfy

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApkfySessionPreferences @Inject constructor(
  @ApplicationContext context: Context,
) {

  private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

  val wasApkfyResolvedAtStartup: Boolean = hasResolvedApkfySession()

  fun hasResolvedApkfySession(): Boolean =
    preferences.getBoolean(KEY_HAS_RESOLVED_APKFY_SESSION, false) ||
      preferences.getBoolean(LEGACY_KEY_HAS_SHOWN_APKFY, false)

  fun markApkfySessionResolved() {
    preferences.edit {
      putBoolean(KEY_HAS_RESOLVED_APKFY_SESSION, true)
    }
  }

  private companion object {
    const val PREFERENCES_NAME = "apkfy_session"
    const val KEY_HAS_RESOLVED_APKFY_SESSION = "has_resolved_apkfy_session"
    const val LEGACY_KEY_HAS_SHOWN_APKFY = "has_shown_apkfy"
  }
}
