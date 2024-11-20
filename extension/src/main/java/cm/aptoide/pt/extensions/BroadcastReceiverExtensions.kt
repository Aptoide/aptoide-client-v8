package cm.aptoide.pt.extensions

import android.content.BroadcastReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun BroadcastReceiver.goAsync(
  context: CoroutineContext = EmptyCoroutineContext,
  block: suspend CoroutineScope.() -> Unit,
) {
  val pendingResult = goAsync()
  CoroutineScope(SupervisorJob()).launch(context) {
    try {
      block()
    } finally {
      pendingResult.finish()
    }
  }
}
