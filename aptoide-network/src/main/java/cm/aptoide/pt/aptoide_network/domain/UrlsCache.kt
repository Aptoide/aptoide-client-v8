package cm.aptoide.pt.aptoide_network.domain

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlsCache @Inject constructor(
  private val initializer: UrlsCacheInitializer,
) {
  private val invalidated = mutableSetOf<String>()
  private val cached = mutableMapOf<String, String>()
  private val mutex = Mutex()

  fun isInvalid(id: String): Boolean = invalidated.remove(id)

  suspend fun putAll(map: Map<String, String>) = mutex.withLock { cached.putAll(map) }

  suspend fun get(id: String): String? = mutex.withLock {
    if (cached.isEmpty()) {
      cached.putAll(initializer.initialise())
    }
    cached[id]
  }

  fun invalidate(): Boolean = invalidated.addAll(cached.keys)
}

interface UrlsCacheInitializer {
  suspend fun initialise(): Map<String, String>
}
