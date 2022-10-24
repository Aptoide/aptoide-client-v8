package cm.aptoide.pt.install_manager

import cm.aptoide.pt.util.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * AS a Developer,
 * I WANT to have an easy to use app install/uninstall task
 * WITH interchangeable storage, download and install implementations,
 * FOR listening and cancelling the running tasks
 */
@ExperimentalCoroutinesApi
internal class TasksTest {

  @Test
  fun `Install task saved`() = coScenario { scope ->
    m Given "task info repository mock without saved data"
    val taskInfoRepository = TaskInfoRepositoryMock()
    m And "timestamp holder"
    var timeStamp = -1L
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.taskInfoRepository = taskInfoRepository
      clock = Clock { scope.currentTime.also { timeStamp = it } }
    }.build()
    m And "installation for the provided package name started"
    installManager.getApp("package").install(installInfo)

    m When "wait for some progress to happen"
    scope.runCurrent()

    m Then "task info saved to the repo"
    assertEquals(1, taskInfoRepository.info.size)
    assertEquals(installInfo, taskInfoRepository.info.first().installPackageInfo)
    assertEquals("package", taskInfoRepository.info.first().packageName)
    assertEquals(Task.Type.INSTALL, taskInfoRepository.info.first().type)
    assertEquals(timeStamp, taskInfoRepository.info.first().timestamp)
  }

  @Test
  fun `Uninstall task saved`() = coScenario { scope ->
    m Given "app info repository mock with the given app info"
    val appInfoRepository = AppInfoRepositoryMock(mapOf("package4" to installedWithDetails))
    m And "task info repository mock without saved data"
    val taskInfoRepository = TaskInfoRepositoryMock()
    m And "package installer mock that has package to uninstall"
    val packageInstaller = PackageInstallerMock(setOf("package4"))
    m And "timestamp holder"
    var timeStamp = -1L
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.taskInfoRepository = taskInfoRepository
      this.packageInstaller = packageInstaller
      clock = Clock { scope.currentTime.also { timeStamp = it } }
    }.build()
    m And "uninstallation for the provided package name started"
    installManager.getApp("package4").uninstall()

    m When "wait for some progress to happen"
    scope.runCurrent()

    m Then "task info saved to the repo"
    assertEquals(1, taskInfoRepository.info.size)
    assertEquals(uninstallInfo, taskInfoRepository.info.first().installPackageInfo)
    assertEquals("package4", taskInfoRepository.info.first().packageName)
    assertEquals(Task.Type.UNINSTALL, taskInfoRepository.info.first().type)
    assertEquals(timeStamp, taskInfoRepository.info.first().timestamp)
  }

  @Test
  fun `Return install completed and task info removed`() = coScenario { scope ->
    m Given "task info repository mock without saved data"
    val taskInfoRepository = TaskInfoRepositoryMock()
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.taskInfoRepository = taskInfoRepository
    }.build()
    m And "installation for the provided package name started"
    val task = installManager.getApp("package").install(installInfo)

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait for all coroutines to finish"
    scope.advanceUntilIdle()
    m And "collect the task state and progress again"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data has all the states"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.DOWNLOADING to 0,
        Task.State.DOWNLOADING to 25,
        Task.State.DOWNLOADING to 50,
        Task.State.DOWNLOADING to 75,
        Task.State.READY_TO_INSTALL to -1,
        Task.State.INSTALLING to 0,
        Task.State.INSTALLING to 25,
        Task.State.INSTALLING to 50,
        Task.State.INSTALLING to 75,
        Task.State.COMPLETED to -1
      ),
      result
    )
    m And "second collected data contains only last state"
    assertEquals(listOf(Task.State.COMPLETED to -1), result2)
    m And "task removed from the repo"
    assertTrue(taskInfoRepository.info.isEmpty())
  }

  @Test
  fun `Return uninstall completed and task info removed`() = coScenario { scope ->
    m Given "app info repository mock with the given app info"
    val appInfoRepository = AppInfoRepositoryMock(mapOf("package4" to installedWithDetails))
    m And "task info repository mock without saved data"
    val taskInfoRepository = TaskInfoRepositoryMock()
    m And "package installer mock that has a package to uninstall"
    val packageInstaller = PackageInstallerMock(setOf("package4"))
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.taskInfoRepository = taskInfoRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "uninstallation for the provided package name started"
    val task = installManager.getApp("package4").uninstall()

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait for all coroutines to finish"
    scope.advanceUntilIdle()
    m And "collect the task state and progress again"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data has all the states"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.UNINSTALLING to 0,
        Task.State.UNINSTALLING to 25,
        Task.State.UNINSTALLING to 50,
        Task.State.UNINSTALLING to 75,
        Task.State.COMPLETED to -1
      ),
      result
    )
    m And "second collected data contains only last state"
    assertEquals(listOf(Task.State.COMPLETED to -1), result2)
    m And "task removed from the repo"
    assertTrue(taskInfoRepository.info.isEmpty())
  }

  @Test
  fun `Return error if download failed and don't save task info`() = coScenario { scope ->
    m Given "task info repository mock without saved data"
    val taskInfoRepository = TaskInfoRepositoryMock()
    m And "package downloader mock that throws an error on download"
    val packageDownloader = PackageDownloaderMock(letItCrash = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.taskInfoRepository = taskInfoRepository
      this.packageDownloader = packageDownloader
    }.build()
    m And "installation for the provided package name started"
    val task = installManager.getApp("package").install(installInfo)

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait for all coroutines to finish"
    scope.advanceUntilIdle()
    m And "collect the task state and progress again"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data has all the states"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.DOWNLOADING to 0,
        Task.State.DOWNLOADING to 25,
        Task.State.FAILED to -1
      ),
      result
    )
    m And "second collected data contains only last state"
    assertEquals(listOf(Task.State.FAILED to -1), result2)
    m And "task removed from the repo"
    assertTrue(taskInfoRepository.info.isEmpty())
  }

  @Test
  fun `Return error if install failed and don't save task info`() = coScenario { scope ->
    m Given "task info repository mock without saved data"
    val taskInfoRepository = TaskInfoRepositoryMock()
    m And "package installer mock that throws an error on install"
    val packageInstaller = PackageInstallerMock(letItCrash = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.taskInfoRepository = taskInfoRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "installation for the provided package name started"
    val task = installManager.getApp("package").install(installInfo)

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait for all coroutines to finish"
    scope.advanceUntilIdle()
    m And "collect the task state and progress again"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data has all the states"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.DOWNLOADING to 0,
        Task.State.DOWNLOADING to 25,
        Task.State.DOWNLOADING to 50,
        Task.State.DOWNLOADING to 75,
        Task.State.READY_TO_INSTALL to -1,
        Task.State.INSTALLING to 0,
        Task.State.INSTALLING to 25,
        Task.State.FAILED to -1
      ),
      result
    )
    m And "second collected data contains only last state"
    assertEquals(listOf(Task.State.FAILED to -1), result2)
    m And "task removed from the repo"
    assertTrue(taskInfoRepository.info.isEmpty())
  }

  @Test
  fun `Return error if uninstall failed and don't save task info`() = coScenario { scope ->
    m Given "app info repository mock with the given app info"
    val appInfoRepository = AppInfoRepositoryMock(mapOf("package4" to installedWithDetails))
    m And "task info repository mock without saved data"
    val taskInfoRepository = TaskInfoRepositoryMock()
    m And "package installer mock that throws an error on install"
    val packageInstaller = PackageInstallerMock(setOf("package4"), letItCrash = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.taskInfoRepository = taskInfoRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "uninstallation for the provided package name started"
    val task = installManager.getApp("package4").uninstall()

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait for all coroutines to finish"
    scope.advanceUntilIdle()
    m And "collect the task state and progress again"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data has all the states"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.UNINSTALLING to 0,
        Task.State.UNINSTALLING to 25,
        Task.State.FAILED to -1
      ),
      result
    )
    m And "second collected data contains only last state"
    assertEquals(listOf(Task.State.FAILED to -1), result2)
    m And "task removed from the repo"
    assertTrue(taskInfoRepository.info.isEmpty())
  }

  @Test
  fun `Return cancellation if download cancelled and don't save task info`() = coScenario { scope ->
    m Given "task info repository mock without saved data"
    val taskInfoRepository = TaskInfoRepositoryMock()
    m And "package downloader mock that will wait for cancellation"
    val packageDownloader = PackageDownloaderMock(waitForCancel = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.taskInfoRepository = taskInfoRepository
      this.packageDownloader = packageDownloader
    }.build()
    m And "installation for the provided package name started"
    val task = installManager.getApp("package").install(installInfo)
    m And "collect the task state and progress"
    var result = emptyList<Pair<Task.State, Int>>()
    scope.launch {
      result = task.stateAndProgress.toList()
    }
    m And "wait for download starts"
    scope.advanceUntilIdle()

    m When "cancel the task"
    task.cancel()
    m And "wait for all coroutines to finish"
    scope.advanceUntilIdle()
    m And "collect the task state and progress again"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data has all the states"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.DOWNLOADING to 0,
        Task.State.DOWNLOADING to 25,
        Task.State.CANCELED to -1
      ),
      result
    )
    m And "second collected data contains only last state"
    assertEquals(listOf(Task.State.CANCELED to -1), result2)
    m And "task removed from the repo"
    assertTrue(taskInfoRepository.info.isEmpty())
  }

  @Test
  fun `Return cancellation if install cancelled and don't save task info`() = coScenario { scope ->
    m Given "task info repository mock without saved data"
    val taskInfoRepository = TaskInfoRepositoryMock()
    m And "package installer mock that will wait for cancellation"
    val packageInstaller = PackageInstallerMock(waitForCancel = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.taskInfoRepository = taskInfoRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "installation for the provided package name started"
    val task = installManager.getApp("package").install(installInfo)
    m And "collect the task state and progress"
    var result = emptyList<Pair<Task.State, Int>>()
    scope.launch {
      result = task.stateAndProgress.toList()
    }
    m And "wait for install starts"
    scope.advanceUntilIdle()

    m When "cancel the task"
    task.cancel()
    m And "wait for all coroutines to finish"
    scope.advanceUntilIdle()
    m And "collect the task state and progress again"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data has all the states"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.DOWNLOADING to 0,
        Task.State.DOWNLOADING to 25,
        Task.State.DOWNLOADING to 50,
        Task.State.DOWNLOADING to 75,
        Task.State.READY_TO_INSTALL to -1,
        Task.State.INSTALLING to 0,
        Task.State.INSTALLING to 25,
        Task.State.CANCELED to -1
      ),
      result
    )
    m And "second collected data contains only last state"
    assertEquals(listOf(Task.State.CANCELED to -1), result2)
    m And "task removed from the repo"
    assertTrue(taskInfoRepository.info.isEmpty())
  }

  @Test
  fun `Return cancellation if uninstall cancelled and don't save task info`() =
    coScenario { scope ->
      m Given "app info repository mock with the given app info"
      val appInfoRepository = AppInfoRepositoryMock(mapOf("package4" to installedWithDetails))
      m And "task info repository mock without saved data"
      val taskInfoRepository = TaskInfoRepositoryMock()
      m And "package installer mock that has package to uninstall that will wait for cancellation"
      val packageInstaller = PackageInstallerMock(setOf("package4"), waitForCancel = true)
      m And "install manager initialised with those mocks"
      val installManager = createBuilderWithMocks(scope).apply {
        this.appInfoRepository = appInfoRepository
        this.taskInfoRepository = taskInfoRepository
        this.packageInstaller = packageInstaller
      }.build()
      m And "uninstallation for the provided package name started"
      val task = installManager.getApp("package4").uninstall()
      m And "collect the task state and progress"
      var result = emptyList<Pair<Task.State, Int>>()
      scope.launch {
        result = task.stateAndProgress.toList()
      }
      m And "wait for uninstall starts"
      scope.advanceUntilIdle()

      m When "cancel the task"
      task.cancel()
      m And "wait for all coroutines to finish"
      scope.advanceUntilIdle()
      m And "collect the task state and progress again"
      val result2 = task.stateAndProgress.toList()

      m Then "first collected data has all the states"
      assertEquals(
        listOf(
          Task.State.PENDING to -1,
          Task.State.UNINSTALLING to 0,
          Task.State.UNINSTALLING to 25,
          Task.State.CANCELED to -1
        ),
        result
      )
      m And "second collected data contains only last state"
      assertEquals(listOf(Task.State.CANCELED to -1), result2)
      m And "task removed from the repo"
      assertTrue(taskInfoRepository.info.isEmpty())
    }
}