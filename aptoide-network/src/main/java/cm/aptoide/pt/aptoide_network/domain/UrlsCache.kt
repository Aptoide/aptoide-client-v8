package cm.aptoide.pt.aptoide_network.domain

import kotlinx.coroutines.channels.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlsCache @Inject constructor(
  private val initializer: UrlsCacheInitializer
) {
  private val invalidated = mutableSetOf<String>()
  private val cached = mutableMapOf<String, String>()
  private var blocker: Blocker? = null

  fun isInvalid(id: String): Boolean = invalidated.remove(id)

  fun set(id: String, url: String) = cached.set(id, url)

  suspend fun get(id: String): String? {
    blocker?.await()
    if (cached.isEmpty()) {
      Blocker().also {
        blocker = it
        try {
          cached.putAll(initializer.initialise())
        } finally {
          blocker = null
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

@JvmInline
value class Blocker(private val channel: Channel<Unit> = Channel(0)) {
  suspend fun await() = channel.receive()
  fun yield() = channel.trySend(Unit)
}
