package cm.aptoide.pt.wallet.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.aptoide.pt.wallet.datastore.di.CurrencyPreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyPreferencesDataSource @Inject constructor(
  @CurrencyPreferencesDataStore private val dataStore: DataStore<Preferences>,
) {

  companion object PreferencesKeys {
    private val CURRENCY_PREFERENCE = stringPreferencesKey("currency_preference")
    private val DEFAULT_CURRENCY = "EUR"
  }

  init {
    CoroutineScope(Dispatchers.IO).launch {
      setPreferredCurrency(DEFAULT_CURRENCY)
    }
  }

  suspend fun setPreferredCurrency(currency: String) {
    dataStore.edit { bundlePreferences ->
      bundlePreferences[CURRENCY_PREFERENCE] = currency
    }
  }

  suspend fun getPreferredCurrency(): String? {
    return dataStore.data.map { preferences ->
      preferences[CURRENCY_PREFERENCE]
    }.first() ?: DEFAULT_CURRENCY
  }

  fun observePreferredCurrency(): Flow<String?> {
    return dataStore.data.map { preferences ->
      preferences[CURRENCY_PREFERENCE]
    }
  }
}
