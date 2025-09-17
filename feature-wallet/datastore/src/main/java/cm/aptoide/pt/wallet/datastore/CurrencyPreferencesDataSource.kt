package cm.aptoide.pt.wallet.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.aptoide.pt.wallet.datastore.di.CurrencyPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyPreferencesDataSource @Inject constructor(
  @CurrencyPreferencesDataStore private val dataStore: DataStore<Preferences>,
) {

  companion object PreferencesKeys {
    private val CURRENCY_PREFERENCE = stringPreferencesKey("currency_preference")
  }

  suspend fun setPreferredCurrency(currency: String) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[CURRENCY_PREFERENCE] = currency
    }
  }

  suspend fun getPreferredCurrency(): String? {
    return dataStore.data.map { preferences ->
      preferences[CURRENCY_PREFERENCE]
    }.first()
  }

  fun observePreferredCurrency(): Flow<String?> {
    return dataStore.data.map { preferences ->
      preferences[CURRENCY_PREFERENCE]
    }
  }
}
