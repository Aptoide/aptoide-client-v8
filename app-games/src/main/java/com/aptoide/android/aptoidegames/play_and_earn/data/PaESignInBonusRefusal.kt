package com.aptoide.android.aptoidegames.play_and_earn.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.aptoide.android.aptoidegames.play_and_earn.di.PaEPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * When the backend last refused the sign-in bonus for a reason that ends Play & Earn for the user
 * (AND-953): a restricted account, an unsupported region or device, or an outdated app. For
 * [MAX_AGE_MILLIS] after it the bonus is not offered and not asked for again; then it may be, as
 * a ban or a country can change.
 *
 * Kept in the Play & Earn preferences: only the time of the refusal. Cleared when the account
 * changes.
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

  /** When the bonus was last refused, or null when it was not or the record was cleared. */
  val refusedAt: Flow<Long?> = dataStore.data.map { it[REFUSED_AT] }.distinctUntilChanged()

  /** Whether a refusal younger than [MAX_AGE_MILLIS] holds. */
  suspend fun isRefused(): Boolean {
    val age = refusedAt.first()?.let { nowMillis() - it } ?: return false
    return age in 0 until MAX_AGE_MILLIS
  }

  suspend fun record() {
    dataStore.edit { it[REFUSED_AT] = nowMillis() }
  }

  /** The account changed: the last one's refusal does not hold for the next. */
  suspend fun clear() {
    dataStore.edit { it.remove(REFUSED_AT) }
  }

  companion object {
    val MAX_AGE_MILLIS = TimeUnit.HOURS.toMillis(24)

    private val REFUSED_AT = longPreferencesKey("sign_in_bonus_refused_at")
  }
}
