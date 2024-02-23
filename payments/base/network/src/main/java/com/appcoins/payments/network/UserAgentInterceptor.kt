package com.appcoins.payments.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class UserAgentInterceptor @Inject constructor(private val provider: GetUserAgent) :
  Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val userAgent = provider()
    val newRequest = chain.request().newBuilder().header("User-Agent", userAgent).build()
    return chain.proceed(newRequest)
  }
}

interface GetUserAgent {
  operator fun invoke(): String
}
