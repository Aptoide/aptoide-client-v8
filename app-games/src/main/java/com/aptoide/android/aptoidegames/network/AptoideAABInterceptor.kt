package com.aptoide.android.aptoidegames.network

import cm.aptoide.pt.aptoide_network.data.network.AABInterceptor
import com.aptoide.android.aptoidegames.Platform.shouldUseLegacyInstaller
import okhttp3.Interceptor
import okhttp3.Response

class AptoideAABInterceptor : AABInterceptor {

  val useAab: Boolean = !shouldUseLegacyInstaller

  override fun shouldUseAAB(): Boolean = useAab

  override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()
    val newUrl = originalRequest.url.newBuilder()

    newUrl.addQueryParameter("aab", shouldUseAAB().toString())

    val newRequest = originalRequest.newBuilder().url(newUrl.build()).build()
    return chain.proceed(newRequest)
  }
}
