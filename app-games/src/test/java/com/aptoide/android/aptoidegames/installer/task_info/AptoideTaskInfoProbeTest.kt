package com.aptoide.android.aptoidegames.installer.task_info

import cm.aptoide.pt.install_manager.Task
import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.installer.analytics.InstallAnalytics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// AND-954: every install the install manager queues, whatever screen started it, is told to the
// Play & Earn install clicks.
@OptIn(ExperimentalCoroutinesApi::class)
internal class AptoideTaskInfoProbeTest {

  private class FakeTaskInfoRepository : TaskInfoRepository {
    val saved = mutableListOf<TaskInfo>()
    override suspend fun getAll(): Set<TaskInfo> = saved.toSet()
    override suspend fun saveJob(taskInfo: TaskInfo) {
      saved += taskInfo
    }

    override suspend fun remove(vararg taskInfo: TaskInfo) = Unit
  }

  private val repository = FakeTaskInfoRepository()
  private val queued = mutableListOf<String>()
  private val probe = AptoideTaskInfoProbe(
    taskInfoRepository = repository,
    installAnalytics = object : InstallAnalytics {},
    onInstallQueued = { queued += it },
  )

  @Test
  fun `A queued install is told with its package`() = coScenario {
    m Given "an install task of a game"
    val task = taskInfo("com.game", Task.Type.INSTALL)

    m When "the install manager queues it"
    probe.saveJob(task)

    m Then "it is saved and told"
    assertEquals(listOf(task), repository.saved)
    assertEquals(listOf("com.game"), queued)
  }

  @Test
  fun `A queued uninstall is not told`() = coScenario {
    m Given "an uninstall task of a game"
    val task = taskInfo("com.game", Task.Type.UNINSTALL)

    m When "the install manager queues it"
    probe.saveJob(task)

    m Then "it is saved and not told"
    assertEquals(listOf(task), repository.saved)
    assertEquals(emptyList<String>(), queued)
  }

  @Test
  fun `A listener that throws does not stop the task`() = coScenario {
    m Given "a listener that throws, as a dependency failing to build would"
    val throwingProbe = AptoideTaskInfoProbe(
      taskInfoRepository = repository,
      installAnalytics = object : InstallAnalytics {},
      onInstallQueued = { throw IllegalStateException("no tracker") },
    )
    val task = taskInfo("com.game", Task.Type.INSTALL)

    m When "the install manager queues an install"
    throwingProbe.saveJob(task)

    m Then "the task is saved and nothing was thrown"
    assertEquals(listOf(task), repository.saved)
  }

  private fun taskInfo(packageName: String, type: Task.Type) = TaskInfo(
    packageName = packageName,
    installPackageInfo = InstallPackageInfo(versionCode = 1),
    constraints = Constraints(
      checkForFreeSpace = true,
      networkType = Constraints.NetworkType.ANY
    ),
    type = type,
    timestamp = 1L,
  )
}
