package cm.aptoide.pt.network

import cm.aptoide.pt.aptoide_network.data.network.QLogicInterceptor
import cm.aptoide.pt.aptoide_network.q.QManager
import cm.aptoide.pt.settings.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject

class AptoideQLogicInterceptor @Inject constructor(
  private val userPreferencesRepository: UserPreferencesRepository,
  private val qManager: QManager
) : QLogicInterceptor {

  override fun buildQValue(): String? {
    var hwSpecsFilter: Boolean
    runBlocking {
      hwSpecsFilter = userPreferencesRepository.isShowCompatibleApps().first() ?: false
    }
    return qManager.getFilters(hwSpecsFilter)
  }

  override fun intercept(chain: Chain): Response {
    val originalRequest = chain.request()
    val newUrl = originalRequest.url.newBuilder()
    buildQValue()?.let {
      newUrl.addQueryParameter("q", buildQValue())
    }
    val newRequest = originalRequest.newBuilder().url(newUrl.build()).build()
    return chain.proceed(newRequest)
  }
}
