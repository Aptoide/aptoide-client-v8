package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class JobDispatcher(
  private val scope: CoroutineScope,
  private val networkConnection: NetworkConnection,
) {

  /** Currently running task */
  internal val runningTask = MutableStateFlow<RealTask?>(null)

  private val enqueueMutex = Mutex()
  private val runMutex = Mutex()

  /** Pending tasks in the order they'll be run. */
  private val pendingTasks = mutableListOf<RealTask>()

  internal val scheduledSize: Long
    get() = pendingTasks.sumOf { it.downloadSize } + (runningTask.value?.downloadSize ?: 0)

  init {
    networkConnection.setOnChangeListener {
      scope.launch {
        if (it != NetworkConnection.State.GONE) {
          promoteAndExecute()
        }
      }
    }
  }

  internal suspend fun enqueue(task: RealTask) {
    enqueueMutex.withLock {
      if (!pendingTasks.contains(task)) pendingTasks.add(task)
    }
    scope.launch {
      promoteAndExecute()
    }
  }

  private suspend fun promoteAndExecute() {
    runMutex.withLock {
      while (true) {
        val task = enqueueMutex
          .withLock {
            var task: RealTask? = null
            val iterator = pendingTasks.iterator()
            while (iterator.hasNext()) {
              task = iterator.next()
              val type = task.constraints.networkType
              val state = networkConnection.state
              if (type == Constraints.NetworkType.NOT_REQUIRED ||
                (type == Constraints.NetworkType.ANY && state != NetworkConnection.State.GONE) ||
                (type == Constraints.NetworkType.UNMETERED && state == NetworkConnection.State.UNMETERED)
              ) {
                iterator.remove()
                break
              } else {
                task = null
              }
            }
            task
          }
          .also {
            runningTask.value = it
          }
          ?: break
        task.start()
      }
    }
  }
}
