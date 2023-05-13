package cm.aptoide.pt.aptoide_network.domain

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlsCache @Inject constructor() {
  private val invalidated = mutableSetOf<String>()
  private val cached = mutableMapOf<String, String>()

  fun isInvalid(id: String): Boolean = invalidated.remove(id)

  fun set(id: String, url: String) = cached.set(id, url)

  fun get(id: String): String? = cached[id]

  fun invalidate(): Boolean = invalidated.addAll(cached.keys)
}