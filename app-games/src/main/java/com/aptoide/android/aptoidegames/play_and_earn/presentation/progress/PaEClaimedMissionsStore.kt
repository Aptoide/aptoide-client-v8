package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Which completed missions the player has already tapped Claim on. Kept on the device only: the
 * server credits a mission when it completes, so Claim is the on-screen collection of a reward
 * the wallet already holds, and forgetting it (a reinstall) only shows the button again.
 */
@Singleton
class PaEClaimedMissionsStore @Inject constructor(
  @ApplicationContext context: Context,
) {
  private val preferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

  fun claimed(): Set<String> = preferences.getStringSet(KEY, emptySet()).orEmpty().toSet()

  fun claim(id: String): Set<String> {
    val updated = claimed() + id
    preferences.edit().putStringSet(KEY, updated).apply()
    return updated
  }

  private companion object {
    const val FILE = "pae_claimed_missions"
    const val KEY = "claimed"
  }
}
