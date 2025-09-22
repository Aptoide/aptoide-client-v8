package cm.aptoide.pt.wallet.authorization.data

import android.content.Context
import cm.aptoide.pt.wallet.authorization.data.model.UserAuthData
import cm.aptoide.pt.wallet.authorization.data.model.UserAuthorizationResponse
import cm.aptoide.pt.wallet.authorization.domain.UserWalletData
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WalletAuthRepositoryImpl @Inject constructor(
  @ApplicationContext private val context: Context,
  private val userAuthApi: UserAuthApi,
  private val dispatcher: CoroutineDispatcher,
  private val walletCoreDataSource: WalletCoreDataSource
) : WalletAuthRepository {

  override suspend fun authorizeGoogleUser(token: String) = withContext(dispatcher) {
    try {
      val userAuthData = UserAuthData(
        code = token,
        domain = context.packageName
      )

      val authResponse = userAuthApi.authorizeUser(userAuthData)
      walletCoreDataSource.setCurrentWalletAddress(authResponse.data.address)
      walletCoreDataSource.setCurrentAuthToken(authResponse.data.authToken)
      Result.success(authResponse.toDomainModel())
    } catch (e: Throwable) {
      e.printStackTrace()
      Result.failure(e)
    }
  }

  override suspend fun clearAuthorizationData() {
    walletCoreDataSource.clearWalletData()
  }
}

private fun UserAuthorizationResponse.toDomainModel() = UserWalletData(
  address = data.address,
  authToken = data.authToken,
  email = data.email
)
