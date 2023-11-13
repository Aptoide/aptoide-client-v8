package cm.aptoide.pt.install_manager

import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * AS a Developer,
 * I WANT to have an easy to use app install/uninstall task
 * WITH interchangeable storage, download and install implementations,
 * FOR listening to the task state updates and cancelling task
 */
@ExperimentalCoroutinesApi
internal class TasksTest {

  @Test
  fun `Install task info saved`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "outdated version app update started"
    installManager.getApp(outdatedPackage).install(installInfo)

    m When "wait for some progress to happen"
    scope.runCurrent()

    m Then "a new install task info saved to the repo"
    mocks.taskInfoRepository.get(outdatedPackage)!!.run {
      assertEquals(installInfo, installPackageInfo)
      assertEquals(outdatedPackage, packageName)
      assertEquals(Task.Type.INSTALL, type)
    }
  }

  @Test
  fun `Uninstall task info saved`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "outdated version app uninstallation started"
    installManager.getApp(outdatedPackage).uninstall()

    m When "wait for some progress to happen"
    scope.runCurrent()

    m Then "a new uninstall task info saved to the repo"
    mocks.taskInfoRepository.get(outdatedPackage)!!.run {
      assertEquals(uninstallInfo, installPackageInfo)
      assertEquals(outdatedPackage, packageName)
      assertEquals(Task.Type.UNINSTALL, type)
    }
  }

  @Test
  fun `Installation task is completed`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "outdated version app update started"
    val task = installManager.getApp(outdatedPackage).install(installInfo)

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
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
    m And "second collected data contains only terminal state"
    assertEquals(listOf(Task.State.COMPLETED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }

  @Test
  fun `Uninstallation task is completed`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "outdated version app uninstallation started"
    val task = installManager.getApp(outdatedPackage).uninstall()

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
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
    m And "second collected data contains only terminal state"
    assertEquals(listOf(Task.State.COMPLETED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }

  @Test
  fun `Install task failed on download`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will fail after 25%"
    mocks.packageDownloader.progressFlow = failingFlow
    m And "outdated version app update started"
    val task = installManager.getApp(outdatedPackage).install(installInfo)

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data ends with failed state after 25% of download"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.DOWNLOADING to 0,
        Task.State.DOWNLOADING to 25,
        Task.State.FAILED to -1
      ),
      result
    )
    m And "second collected data contains only failed state"
    assertEquals(listOf(Task.State.FAILED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }

  @Test
  fun `Install task failed on installation`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will fail after 25%"
    mocks.packageInstaller.progressFlow = failingFlow
    m And "outdated version app update started"
    val task = installManager.getApp(outdatedPackage).install(installInfo)

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data ends with failed state after 25% of install"
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
    m And "second collected data contains only failed state"
    assertEquals(listOf(Task.State.FAILED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }

  @Test
  fun `Uninstall task failed on uninstallation`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will fail after 25%"
    mocks.packageInstaller.progressFlow = failingFlow
    m And "outdated version app uninstall started"
    val task = installManager.getApp(outdatedPackage).uninstall()

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data ends with failed state after 25% of uninstall"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.UNINSTALLING to 0,
        Task.State.UNINSTALLING to 25,
        Task.State.FAILED to -1
      ),
      result
    )
    m And "second collected data contains only failed state"
    assertEquals(listOf(Task.State.FAILED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }

  @Test
  fun `Install task aborted on download`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will abort after 25%"
    mocks.packageDownloader.progressFlow = abortingFlow
    m And "outdated version app update started"
    val task = installManager.getApp(outdatedPackage).install(installInfo)

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data ends with aborted state after after 25% download"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.DOWNLOADING to 0,
        Task.State.DOWNLOADING to 25,
        Task.State.ABORTED to -1
      ),
      result
    )
    m And "second collected data contains only aborted state"
    assertEquals(listOf(Task.State.ABORTED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }

  @Test
  fun `Install task aborted on installation`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will abort after 25%"
    mocks.packageInstaller.progressFlow = abortingFlow
    m And "outdated version app update started"
    val task = installManager.getApp(outdatedPackage).install(installInfo)

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data ends with aborted state after after 25% install"
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
        Task.State.ABORTED to -1
      ),
      result
    )
    m And "second collected data contains only aborted state"
    assertEquals(listOf(Task.State.ABORTED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }

  @Test
  fun `Install task aborted on uninstallation`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will abort after 25%"
    mocks.packageInstaller.progressFlow = abortingFlow
    m And "outdated version app uninstall started"
    val task = installManager.getApp(outdatedPackage).uninstall()

    m When "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data ends with aborted state after after 25% uninstall"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.UNINSTALLING to 0,
        Task.State.UNINSTALLING to 25,
        Task.State.ABORTED to -1
      ),
      result
    )
    m And "second collected data contains only aborted state"
    assertEquals(listOf(Task.State.ABORTED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }

  @Test
  fun `Install task cancelled on download if cancelled immediately`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will cancel after 25%"
    mocks.packageDownloader.progressFlow = cancellingFlow
    m And "outdated version app update started"
    val task = installManager.getApp(outdatedPackage).install(installInfo)

    m When "collect the task state and progress"
    var result = emptyList<Pair<Task.State, Int>>()
    scope.launch {
      result = task.stateAndProgress.toList()
    }
    m And "call the task cancel"
    task.cancel()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data ends with cancelled state after 25% of download"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.DOWNLOADING to 0,
        Task.State.DOWNLOADING to 25,
        Task.State.CANCELED to -1
      ),
      result
    )
    m And "second collected data contains only cancelled state"
    assertEquals(listOf(Task.State.CANCELED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }

  @Test
  fun `Install task cancelled on download`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will cancel after 25%"
    mocks.packageDownloader.progressFlow = cancellingFlow
    m And "outdated version app update started"
    val task = installManager.getApp(outdatedPackage).install(installInfo)

    m When "collect the task state and progress"
    var result = emptyList<Pair<Task.State, Int>>()
    scope.launch {
      result = task.stateAndProgress.toList()
    }
    m And "wait until download starts"
    scope.advanceUntilIdle()
    m And "call the task cancel"
    task.cancel()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data ends with cancelled state after 25% of download"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.DOWNLOADING to 0,
        Task.State.DOWNLOADING to 25,
        Task.State.CANCELED to -1
      ),
      result
    )
    m And "second collected data contains only cancelled state"
    assertEquals(listOf(Task.State.CANCELED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }

  @Test
  fun `Install task cancelled on installation`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will cancel after 25%"
    mocks.packageInstaller.progressFlow = cancellingFlow
    m And "outdated version app update started"
    val task = installManager.getApp(outdatedPackage).install(installInfo)

    m When "collect the task state and progress"
    var result = emptyList<Pair<Task.State, Int>>()
    scope.launch {
      result = task.stateAndProgress.toList()
    }
    m And "wait until installation starts"
    scope.advanceUntilIdle()
    m And "call the task cancel"
    task.cancel()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data ends with cancelled state after 25% of install"
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
    m And "second collected data contains only cancelled state"
    assertEquals(listOf(Task.State.CANCELED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }

  @Test
  fun `Install task cancelled on uninstallation`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will cancel after 25%"
    mocks.packageInstaller.progressFlow = cancellingFlow
    m And "outdated version app uninstall started"
    val task = installManager.getApp(outdatedPackage).uninstall()

    m When "collect the task state and progress"
    var result = emptyList<Pair<Task.State, Int>>()
    scope.launch {
      result = task.stateAndProgress.toList()
    }
    m And "wait until uninstallation starts"
    scope.advanceUntilIdle()
    m And "call the task cancel"
    task.cancel()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()

    m Then "first collected data ends with cancelled state after 25% of uninstall"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.UNINSTALLING to 0,
        Task.State.UNINSTALLING to 25,
        Task.State.CANCELED to -1
      ),
      result
    )
    m And "second collected data contains only cancelled state"
    assertEquals(listOf(Task.State.CANCELED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
  }
}
