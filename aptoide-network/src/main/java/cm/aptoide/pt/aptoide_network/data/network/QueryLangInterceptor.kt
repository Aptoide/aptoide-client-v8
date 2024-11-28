package cm.aptoide.pt.aptoide_network.data.network

import okhttp3.Interceptor

interface QueryLangInterceptor : Interceptor {
  fun buildLang(): String
}
