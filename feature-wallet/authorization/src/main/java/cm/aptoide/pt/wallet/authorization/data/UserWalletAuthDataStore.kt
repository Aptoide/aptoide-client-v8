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
  private val secureTokenStorage: SecureTokenStorage,
) {

  companion object PreferencesKeys {
    private val CURRENT_WALLET_DATA = stringPreferencesKey("current_wallet_data")
    private val ENCRYPTED_REFRESH_TOKEN = stringPreferencesKey("encrypted_refresh_token")
    private val REFRESH_TOKEN_IV = stringPreferencesKey("refresh_token_iv")
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
    try {
      val encryptedData = secureTokenStorage.encryptToken(refreshToken)

      dataStore.edit { preferences ->
        preferences[ENCRYPTED_REFRESH_TOKEN] = encryptedData.encryptedContent
        preferences[REFRESH_TOKEN_IV] = encryptedData.iv
      }
    } catch (e: Exception) {
      e.printStackTrace()
      throw SecurityException("Failed to save refresh token", e)
    }
  }

  suspend fun getCurrentRefreshToken(): String? {
    return try {
      val encryptedContent = dataStore.data.map { preferences ->
        preferences[ENCRYPTED_REFRESH_TOKEN]
      }.first() ?: return null

      val iv = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_IV]
      }.first() ?: return null

      val encryptedData = EncryptedData(encryptedContent, iv)
      secureTokenStorage.decryptToken(encryptedData)
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  suspend fun clear() {
    try {
      dataStore.edit { preferences ->
        preferences.clear()
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}
