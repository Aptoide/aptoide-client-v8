package com.aptoide.android.aptoidegames.play_and_earn

import cm.aptoide.pt.aptoide_network.data.network.PlayAndEarnIdInterceptor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor.Chain
import okhttp3.Response
import javax.inject.Inject

class AptoidePlayAndEarnIdInterceptor @Inject constructor(
  private val playAndEarnIdProvider: PlayAndEarnIdProvider,
) : PlayAndEarnIdInterceptor {

  private companion object {
    const val ID_TIMEOUT_MS = 2_000L
  }

  override fun intercept(chain: Chain): Response {
    val originalRequest = chain.request()

    // On timeout, proceed without the guest_id rather than stalling the call.
    val guestId = runBlocking {
      withTimeoutOrNull(ID_TIMEOUT_MS) { playAndEarnIdProvider.getId() }
    }.orEmpty()

    if (guestId.isBlank()) return chain.proceed(originalRequest)

    val url = originalRequest.url.newBuilder()
      .addQueryParameter("guest_id", guestId)
      .build()

    return chain.proceed(originalRequest.newBuilder().url(url).build())
  }
}
