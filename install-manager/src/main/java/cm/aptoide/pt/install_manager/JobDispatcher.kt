package cm.aptoide.pt.install_manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.ArrayDeque

internal class JobDispatcher(private val scope: CoroutineScope) {

  /** Currently running task */
  internal val runningJob = MutableStateFlow<Task?>(null)

  private val enqueueMutex = Mutex()
  private val runMutex = Mutex()

  /** Pending tasks jobs in the order they'll be run. */
  private val pendingJobs = ArrayDeque<Pair<Task, (suspend () -> Unit)>>()

  internal suspend fun enqueue(
    task: Task,
    job: suspend () -> Unit,
  ) {
    enqueueMutex.withLock {
      pendingJobs.add(task to job)
    }
    scope.launch {
      promoteAndExecute()
    }
  }

  private suspend fun promoteAndExecute() {
    runMutex.withLock {
      while (true) {
        val job = enqueueMutex.withLock {
          pendingJobs.pollFirst().also {
            runningJob.emit(it?.first)
          }
        } ?: break
        job.second.invoke()
      }
    }
  }
}

