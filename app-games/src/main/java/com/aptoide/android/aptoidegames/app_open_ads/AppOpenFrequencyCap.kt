package com.aptoide.android.aptoidegames.app_open_ads

import android.content.Context
import androidx.core.content.edit
import kotlin.time.Duration

class AppOpenFrequencyCap(context: Context) {

  private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

  fun canShow(now: Long, window: Duration): Boolean {
    val lastShownTs = preferences.getLong(KEY_LAST_SHOWN_TS, 0L)
    if (lastShownTs <= 0L) {
      return true
    }

    return now - lastShownTs >= window.inWholeMilliseconds
  }

  fun recordImpression(now: Long) {
    preferences.edit {
      putLong(KEY_LAST_SHOWN_TS, now)
    }
  }

  private companion object {
    const val PREFERENCES_NAME = "appopen_frequency_cap"
    const val KEY_LAST_SHOWN_TS = "last_shown_ts"
  }
}
