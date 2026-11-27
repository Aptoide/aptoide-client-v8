package com.aptoide.android.aptoidegames.play_and_earn.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aptoide.android.aptoidegames.play_and_earn.di.PaEPreferencesDataStore
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The one redeem whose answer is not final yet, and its request id (AND-952). The backend
 * answers a repeated id once, so a redeem sent again with its first id is never redeemed twice,
 * even when its answer was lost and the user left the screen or the app died.
 *
 * Kept in the Play & Earn preferences: the id, when it was made, and a SHA-256 of the email and
 * reward it belongs to (the email itself is not stored). Cleared when the account changes.
 */
@Singleton
class PaEPendingRedeems internal constructor(
  private val dataStore: DataStore<Preferences>,
  private val nowMillis: () -> Long,
  private val newRequestId: () -> String,
) {

  @Inject
  constructor(
    @PaEPreferencesDataStore dataStore: DataStore<Preferences>,
  ) : this(
    dataStore = dataStore,
    nowMillis = { System.currentTimeMillis() },
    newRequestId = { UUID.randomUUID().toString() },
  )

  /**
   * The pending id when it belongs to the same redeem and is younger than [MAX_AGE_MILLIS];
   * otherwise a new one, which becomes the pending redeem.
   */
  suspend fun idFor(email: String, rewardType: PaERewardType): String {
    val redeemKey = redeemKeyOf(email, rewardType)
    val now = nowMillis()
    var id = ""
    dataStore.edit { preferences ->
      val age = preferences[CREATED_AT]?.let { now - it }
      id = preferences[REQUEST_ID]
        ?.takeIf { preferences[REDEEM_KEY] == redeemKey && age in 0 until MAX_AGE_MILLIS }
        ?: newRequestId().also {
          preferences[REQUEST_ID] = it
          preferences[REDEEM_KEY] = redeemKey
          preferences[CREATED_AT] = now
        }
    }
    return id
  }

  /** The redeem with [requestId] is final; a newer pending redeem is left alone. */
  suspend fun clear(requestId: String) {
    dataStore.edit { preferences ->
      if (preferences[REQUEST_ID] == requestId) preferences.removePending()
    }
  }

  /** The account changed: its pending redeem does not belong to the next one. */
  suspend fun clearAll() {
    dataStore.edit { it.removePending() }
  }

  private fun MutablePreferences.removePending() {
    remove(REQUEST_ID)
    remove(REDEEM_KEY)
    remove(CREATED_AT)
  }

  companion object {
    /**
     * How long a retry replays the pending redeem. Soon after a lost answer, sending it again
     * must not redeem twice; the same redeem much later is a new one, not yesterday's answer.
     */
    val MAX_AGE_MILLIS = TimeUnit.MINUTES.toMillis(10)

    private val REQUEST_ID = stringPreferencesKey("pending_redeem_request_id")
    private val REDEEM_KEY = stringPreferencesKey("pending_redeem_key")
    private val CREATED_AT = longPreferencesKey("pending_redeem_created_at")

    private fun redeemKeyOf(email: String, rewardType: PaERewardType): String =
      MessageDigest.getInstance("SHA-256")
        .digest("${email.trim().lowercase()}|${rewardType.name}".toByteArray())
        .joinToString("") { "%02x".format(it) }
  }
}
