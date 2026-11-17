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

  fun claim(id: String): Set<String> = claimAll(setOf(id))

  /**
   * Run once, the first time the missions are seen: whatever is already completed then was paid
   * before there was anything to claim, so it starts as claimed (see `claimsToBaseline`).
   */
  fun baselineOnce(ids: () -> Set<String>): Set<String> {
    if (preferences.getBoolean(BASELINED, false)) return claimed()
    val updated = claimAll(ids())
    preferences.edit().putBoolean(BASELINED, true).apply()
    return updated
  }

  private fun claimAll(ids: Set<String>): Set<String> {
    val updated = claimed() + ids
    preferences.edit().putStringSet(KEY, updated).apply()
    return updated
  }

  private companion object {
    const val FILE = "pae_claimed_missions"
    const val KEY = "claimed"
    const val BASELINED = "baselined"
  }
}
