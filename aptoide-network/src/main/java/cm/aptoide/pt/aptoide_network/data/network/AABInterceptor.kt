package cm.aptoide.pt.aptoide_network.data.network

import okhttp3.Interceptor

interface AABInterceptor : Interceptor {
  fun shouldUseAAB(): Boolean = true
}
