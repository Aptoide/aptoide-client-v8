package cm.aptoide.pt.extensions

import kotlinx.coroutines.channels.Channel

class SuspendLock : SuspendValue<Unit>() {
  fun yield() = super.yield(Unit)
}

open class SuspendValue<T>(private val channel: Channel<T> = Channel(0)) {
  suspend fun await(): T = channel.receive()
  fun yield(value: T) = channel.trySend(value)
}
