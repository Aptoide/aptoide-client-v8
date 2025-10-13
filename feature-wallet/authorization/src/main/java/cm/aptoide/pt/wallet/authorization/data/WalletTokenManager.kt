package cm.aptoide.pt.wallet.authorization.data

import cm.aptoide.pt.wallet.authorization.domain.UserWalletData
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WalletTokenManager @Inject constructor(
  private val userWalletAuthDataStore: UserWalletAuthDataStore,
  private val walletAuthRepository: WalletAuthRepository
) {

  @Volatile
  private var cachedWalletData: UserWalletData? = null
  private val mutex = Mutex()

  suspend fun getValidAuthToken(): String? {
    val walletData = cachedWalletData ?: userWalletAuthDataStore.getCurrentWalletData()
    cachedWalletData = walletData

    return if (walletData != null && !walletData.isExpired()) {
      cachedWalletData?.authToken
    } else {
      refreshToken()
    }
  }

  private suspend fun refreshToken(): String? {
    mutex.withLock {
      val walletData = cachedWalletData ?: userWalletAuthDataStore.getCurrentWalletData()
      cachedWalletData = walletData

      if (walletData != null && !walletData.isExpired()) {
        return cachedWalletData?.authToken
      }

      val refreshToken = userWalletAuthDataStore.getCurrentRefreshToken()

      return refreshToken?.let {
        val newWalletData = walletAuthRepository.refreshUserWallet(refreshToken).getOrNull()
        cachedWalletData = newWalletData
        newWalletData?.authToken
      }
    }
  }
}
