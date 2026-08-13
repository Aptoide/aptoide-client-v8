package com.aptoide.android.aptoidegames.attribution

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttributionSessionPreferences @Inject constructor(
  @ApplicationContext context: Context,
) {

  private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

  fun hasResolvedAttribution(): Boolean =
    preferences.getBoolean(KEY_HAS_RESOLVED_ATTRIBUTION, false)

  fun markAttributionResolved() {
    preferences.edit {
      putBoolean(KEY_HAS_RESOLVED_ATTRIBUTION, true)
    }
  }

  private companion object {
    const val PREFERENCES_NAME = "attribution_session"
    const val KEY_HAS_RESOLVED_ATTRIBUTION = "has_resolved_attribution"
  }
}
