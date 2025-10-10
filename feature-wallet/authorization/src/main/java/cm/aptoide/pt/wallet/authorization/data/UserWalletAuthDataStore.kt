package cm.aptoide.pt.wallet.authorization.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.aptoide.pt.wallet.authorization.di.WalletAuthDataStore
import cm.aptoide.pt.wallet.authorization.domain.UserWalletData
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class UserWalletAuthDataStore @Inject constructor(
  @WalletAuthDataStore private val dataStore: DataStore<Preferences>,
) {

  companion object PreferencesKeys {
    private val CURRENT_WALLET_DATA = stringPreferencesKey("current_wallet_data")
    private val CURRENT_REFRESH_TOKEN = stringPreferencesKey("current_refresh_token")
  }

  suspend fun setCurrentWalletData(walletUserData: UserWalletData) {
    dataStore.edit { preferences ->
      preferences[CURRENT_WALLET_DATA] = Gson().toJson(walletUserData)
    }
  }

  suspend fun getCurrentWalletData(): UserWalletData? {
    return dataStore.data.map { preferences ->
      preferences[CURRENT_WALLET_DATA]?.let { Gson().fromJson(it, UserWalletData::class.java) }
    }.first()
  }

  suspend fun setCurrentRefreshToken(refreshToken: String) {
    dataStore.edit { preferences ->
      preferences[CURRENT_REFRESH_TOKEN] = refreshToken
    }
  }

  suspend fun getCurrentRefreshToken(): String? {
    return dataStore.data.map { preferences ->
      preferences[CURRENT_REFRESH_TOKEN]
    }.first()
  }
}
