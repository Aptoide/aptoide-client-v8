package cm.aptoide.pt.aptoide_network.domain

import cm.aptoide.pt.extensions.SuspendLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlsCache @Inject constructor(
  private val initializer: UrlsCacheInitializer,
) {
  private val invalidated = mutableSetOf<String>()
  private val cached = mutableMapOf<String, String>()
  private var lock: SuspendLock? = null

  fun isInvalid(id: String): Boolean = invalidated.remove(id)

  fun set(id: String, url: String) = cached.set(id, url)

  suspend fun get(id: String): String? {
    lock?.await()
    if (cached.isEmpty()) {
      SuspendLock().also {
        lock = it
        try {
          cached.putAll(initializer.initialise())
        } finally {
          lock = null
          it.yield()
        }
      }
    }
    return cached[id]
  }

  fun invalidate(): Boolean = invalidated.addAll(cached.keys)
}

interface UrlsCacheInitializer {
  suspend fun initialise(): Map<String, String>
}
