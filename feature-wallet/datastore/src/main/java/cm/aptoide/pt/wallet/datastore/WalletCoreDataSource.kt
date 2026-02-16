package cm.aptoide.pt.wallet.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.aptoide.pt.wallet.datastore.di.WalletCoreDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletCoreDataSource @Inject constructor(
  @WalletCoreDataStore private val dataStore: DataStore<Preferences>,
) {

  companion object PreferencesKeys {
    private val CURRENT_WALLET_ADDRESS = stringPreferencesKey("current_wallet_address")
  }

  suspend fun setCurrentWalletAddress(address: String) {
    dataStore.edit { preferences ->
      preferences[CURRENT_WALLET_ADDRESS] = address
    }
  }

  suspend fun getCurrentWalletAddress(): String? {
    return dataStore.data.map { preferences ->
      preferences[CURRENT_WALLET_ADDRESS]
    }.first()
  }

  fun observeCurrentWalletAddress(): Flow<String?> {
    return dataStore.data.map { preferences ->
      preferences[CURRENT_WALLET_ADDRESS]
    }
  }

  suspend fun clearWalletData() {
    dataStore.edit { preferences ->
      preferences.remove(CURRENT_WALLET_ADDRESS)
    }
  }
}
