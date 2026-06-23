package com.aptoide.android.aptoidegames.installer.autoopen

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoOpenAfterInstallPreferences @Inject constructor(
  @ApplicationContext context: Context,
) {

  private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

  fun getUserChoice(): Boolean? =
    if (preferences.contains(KEY_AUTO_OPEN_ENABLED)) {
      preferences.getBoolean(KEY_AUTO_OPEN_ENABLED, false)
    } else {
      null
    }

  fun setUserChoice(enabled: Boolean) {
    preferences.edit {
      putBoolean(KEY_AUTO_OPEN_ENABLED, enabled)
    }
  }

  private companion object {
    const val PREFERENCES_NAME = "auto_open_after_install"
    const val KEY_AUTO_OPEN_ENABLED = "auto_open_after_install_enabled"
  }
}
