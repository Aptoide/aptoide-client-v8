package com.aptoide.android.aptoidegames.play_and_earn.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.aptoide.android.aptoidegames.play_and_earn.di.PaEPreferencesDataStore
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * When the backend last refused the sign-in bonus for a reason that ends Play & Earn for the user
 * (AND-953): a restricted account, an unsupported region or device, or an outdated app. For
 * [MAX_AGE_MILLIS] after it the bonus is not offered and not asked for again; then it may be, as
 * a ban or a country can change.
 *
 * A refusal because another account on this phone already received the bonus (AND-964) does not
 * change with time: it holds until the account changes.
 *
 * Kept in the Play & Earn preferences: the time of the last timed refusal, and whether the
 * device limit holds (a key the versions before AND-964 never wrote, so their stored time keeps
 * working as before). Both are cleared when the account changes.
 */
@Singleton
class PaESignInBonusRefusal internal constructor(
  private val dataStore: DataStore<Preferences>,
  private val nowMillis: () -> Long,
) {

  @Inject
  constructor(
    @PaEPreferencesDataStore dataStore: DataStore<Preferences>,
  ) : this(dataStore = dataStore, nowMillis = { System.currentTimeMillis() })

  /** Whether the device limit or a refusal younger than [MAX_AGE_MILLIS] holds. */
  suspend fun isRefused(): Boolean {
    val preferences = dataStore.data.first()
    if (preferences[DEVICE_LIMIT] == true) return true
    val age = preferences[REFUSED_AT]?.let { nowMillis() - it } ?: return false
    return age in 0 until MAX_AGE_MILLIS
  }

  /** A refusal that may change: it holds for [MAX_AGE_MILLIS]. */
  suspend fun record() {
    dataStore.edit { it[REFUSED_AT] = nowMillis() }
  }

  /** This phone already received the bonus on another account: it holds until [clear]. */
  suspend fun recordDeviceLimit() {
    dataStore.edit { it[DEVICE_LIMIT] = true }
  }

  /** The account changed: the last one's refusal does not hold for the next. */
  suspend fun clear() {
    dataStore.edit {
      it.remove(REFUSED_AT)
      it.remove(DEVICE_LIMIT)
    }
  }

  companion object {
    val MAX_AGE_MILLIS = TimeUnit.HOURS.toMillis(24)

    private val REFUSED_AT = longPreferencesKey("sign_in_bonus_refused_at")
    private val DEVICE_LIMIT = booleanPreferencesKey("sign_in_bonus_device_limit")
  }
}
