package cm.aptoide.pt.extensions

import kotlinx.coroutines.channels.Channel

open class SuspendValue<T>(private val channel: Channel<T> = Channel(0)) {
  suspend fun await(): T = channel.receive()
  fun yield(value: T) = channel.trySend(value)
}
