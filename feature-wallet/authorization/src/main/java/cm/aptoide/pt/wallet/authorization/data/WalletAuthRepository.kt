package cm.aptoide.pt.wallet.authorization.data

import cm.aptoide.pt.wallet.authorization.domain.UserWalletData

interface WalletAuthRepository {

  suspend fun authorizeGoogleUser(token: String): Result<UserWalletData>

  suspend fun refreshUserWallet(refreshToken: String): Result<UserWalletData>

  suspend fun saveSession(address: String, authToken: String, refreshToken: String)

  suspend fun clearAuthorizationData()
}
