package cm.aptoide.pt.wallet.authorization.data

import cm.aptoide.pt.wallet.authorization.domain.UserWalletData

interface WalletAuthRepository {

  suspend fun authorizeGoogleUser(token: String): Result<UserWalletData>

  suspend fun refreshUserWallet(refreshToken: String): Result<UserWalletData>

  suspend fun saveSession(address: String, authToken: String, refreshToken: String)

  /** The current wallet session's refresh token, or null when signed out. */
  suspend fun getRefreshToken(): String?

  suspend fun clearAuthorizationData()
}
