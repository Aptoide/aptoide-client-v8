package cm.aptoide.pt.wallet.authorization.data

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletAuthInterceptor @Inject internal constructor(
  private val walletTokenManager: WalletTokenManager,
) : Interceptor {

  private companion object {
    const val MAX_RETRIES = 2
    const val UNAUTHORIZED = 401
  }

  override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()

    if (originalRequest.headers.names().contains("Authorization"))
      return chain.proceed(originalRequest)

    val token = runBlocking { walletTokenManager.getValidAuthToken() }

    val authorizedRequest: Request = originalRequest.newBuilder()
      .apply { token?.let { header("Authorization", "Bearer $it") } }
      .build()

    return chain.proceed(authorizedRequest)
  }

  private fun makeRequest(
    chain: Interceptor.Chain,
    originalRequest: Request,
    retry: Int = 1
  ): Response {
    val token = runBlocking { walletTokenManager.getValidAuthToken() }

    val authorizedRequest: Request = originalRequest.newBuilder()
      .apply { token?.let { header("Authorization", "Bearer $it") } }
      .build()

    val response = chain.proceed(authorizedRequest)

    if (response.code == UNAUTHORIZED && retry <= MAX_RETRIES) {
      response.close()
      return makeRequest(chain, originalRequest, retry + 1)
    }

    return response
  }
}
