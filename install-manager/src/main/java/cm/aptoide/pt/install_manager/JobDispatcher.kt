package cm.aptoide.pt.install_manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.ArrayDeque

internal class JobDispatcher(private val scope: CoroutineScope) {

  /** Currently running task */
  internal val runningTask = MutableStateFlow<Task?>(null)

  private val enqueueMutex = Mutex()
  private val runMutex = Mutex()

  /** Pending tasks in the order they'll be run. */
  private val pendingTasks = ArrayDeque<RealTask>()

  internal suspend fun enqueue(task: RealTask) {
    enqueueMutex.withLock {
      pendingTasks.add(task)
    }
    scope.launch {
      promoteAndExecute()
    }
  }

  private suspend fun promoteAndExecute() {
    runMutex.withLock {
      while (true) {
        val task = enqueueMutex.withLock {
          pendingTasks.pollFirst().also {
            runningTask.emit(it)
          }
        } ?: break
        task.start()
      }
    }
  }
}

