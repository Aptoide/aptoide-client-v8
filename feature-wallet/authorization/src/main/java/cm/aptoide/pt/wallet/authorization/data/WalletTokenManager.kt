package cm.aptoide.pt.wallet.authorization.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WalletTokenManager @Inject constructor(
  private val userWalletAuthDataStore: UserWalletAuthDataStore,
  private val walletAuthRepository: WalletAuthRepository
) {
  private val mutex = Mutex()

  suspend fun getValidAuthToken(): String? {
    val walletData = userWalletAuthDataStore.getCurrentWalletData()

    return if (walletData != null && !walletData.isExpired()) {
      walletData.authToken
    } else {
      refreshToken()
    }
  }

  private suspend fun refreshToken(): String? {
    mutex.withLock {
      val walletData = userWalletAuthDataStore.getCurrentWalletData()

      if (walletData != null && !walletData.isExpired()) {
        return walletData.authToken
      }

      val refreshToken = userWalletAuthDataStore.getCurrentRefreshToken()

      return refreshToken?.let {
        val newWalletData = walletAuthRepository.refreshUserWallet(refreshToken).getOrNull()
        newWalletData?.authToken
      }
    }
  }
}
