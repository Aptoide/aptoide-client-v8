package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.Constraints.NetworkType
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.time.Duration.Companion.seconds

/**
 * AS a Developer,
 * I WANT to have an easy to use app install/uninstall task
 * WITH interchangeable storage, download and install implementations,
 * FOR listening to the task state updates and cancelling task
 */
@ExperimentalCoroutinesApi
internal class TasksTest {

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Install task info saved`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "outdated version app update started with given constraints"
    installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "wait for some progress to happen"
    scope.advanceTimeBy(5.seconds)

    m Then "a new install task info saved to the repo"
    mocks.taskInfoRepository.get(outdatedPackage)!!.run {
      assertEquals(installInfo, installPackageInfo)
      assertEquals(outdatedPackage, packageName)
      assertEquals(Task.Type.INSTALL, type)
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Uninstall task info saved`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "outdated version app uninstallation started with given constraints"
    installManager.getApp(outdatedPackage).uninstall(constraints)

    m When "wait for some progress to happen"
    scope.advanceTimeBy(5.seconds)

    m Then "a new uninstall task info saved to the repo"
    mocks.taskInfoRepository.get(outdatedPackage)!!.run {
      assertEquals(uninstallInfo, installPackageInfo)
      assertEquals(outdatedPackage, packageName)
      assertEquals(Task.Type.UNINSTALL, type)
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Installation task is completed`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data has all the states till success"
    assertEquals(successfulInstall, result)
    m And "second collected data contains only terminal state"
    assertEquals(listOf(Task.State.COMPLETED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.COMPLETED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Installation task is completed with negative missing space`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "free space checker mock reports there is -1536 of free space missing"
    mocks.freeSpaceChecker.missingSpace = -1536
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data has all the states till success"
    assertEquals(successfulInstall, result)
    m And "second collected data contains only terminal state"
    assertEquals(listOf(Task.State.COMPLETED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.COMPLETED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Uninstallation task is completed`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "outdated version app uninstallation started with given constraints"
    val task = installManager.getApp(outdatedPackage).uninstall(constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data has all the states till success"
    assertEquals(successfulUninstall, result)
    m And "second collected data contains only terminal state"
    assertEquals(listOf(Task.State.COMPLETED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.COMPLETED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Install task failed on free space check`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will wait for cancellation after 25%"
    mocks.packageDownloader.progressFlow = cancellingFlow
    m And "free space checker mock reports there is 1536 of free space missing"
    mocks.freeSpaceChecker.missingSpace = 1536
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data ends with failed state before download starts"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.FAILED to -1
      ),
      result
    )
    m And "second collected data contains only failed state"
    assertEquals(listOf(Task.State.FAILED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.FAILED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Install task failed on download`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will fail after 25%"
    mocks.packageDownloader.progressFlow = failingFlow
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data ends with failed state after 25% of download"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.DOWNLOADING to -1,
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
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.FAILED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Install task failed on installation`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will fail after 25%"
    mocks.packageInstaller.progressFlow = failingFlow
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data ends with failed state on install"
    assertEquals(failedInstall, result)
    m And "second collected data contains only failed state"
    assertEquals(listOf(Task.State.FAILED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.FAILED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Uninstall task failed on uninstallation`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will fail after 25%"
    mocks.packageInstaller.progressFlow = failingFlow
    m And "outdated version app uninstall started with given constraints"
    val task = installManager.getApp(outdatedPackage).uninstall(constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data ends with failed state on uninstall"
    assertEquals(failedUninstall, result)
    m And "second collected data contains only failed state"
    assertEquals(listOf(Task.State.FAILED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.FAILED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Install task aborted on download`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will abort after 25%"
    mocks.packageDownloader.progressFlow = abortingFlow
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data ends with aborted state on download"
    assertEquals(abortedDownload, result)
    m And "second collected data contains only aborted state"
    assertEquals(listOf(Task.State.ABORTED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.ABORTED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Install task aborted on installation`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will abort after 25%"
    mocks.packageInstaller.progressFlow = abortingFlow
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data ends with aborted state on install"
    assertEquals(abortedInstall, result)
    m And "second collected data contains only aborted state"
    assertEquals(listOf(Task.State.ABORTED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.ABORTED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Uninstall task aborted on uninstallation`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will abort after 25%"
    mocks.packageInstaller.progressFlow = abortingFlow
    m And "outdated version app uninstall started with given constraints"
    val task = installManager.getApp(outdatedPackage).uninstall(constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
    val result = task.stateAndProgress.toList()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.toList()
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data ends with aborted state on uninstall"
    assertEquals(abortedUninstall, result)
    m And "second collected data contains only aborted state"
    assertEquals(listOf(Task.State.ABORTED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.ABORTED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Install task cancelled on download if cancelled immediately`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will wait for cancellation after 25%"
    mocks.packageDownloader.progressFlow = cancellingFlow
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
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
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data ends with cancelled state after on download"
    assertEquals(canceledDownload, result)
    m And "second collected data contains only cancelled state"
    assertEquals(listOf(Task.State.CANCELED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.CANCELED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Install task cancelled on download`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will wait for cancellation after 25%"
    mocks.packageDownloader.progressFlow = cancellingFlow
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
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
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data ends with cancelled state after on download"
    assertEquals(canceledDownload, result)
    m And "second collected data contains only cancelled state"
    assertEquals(listOf(Task.State.CANCELED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.CANCELED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Install task cancelled on installation`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will wait for cancellation after 25%"
    mocks.packageInstaller.progressFlow = cancellingFlow
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
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
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data ends with cancelled state on install"
    assertEquals(canceledInstall, result)
    m And "second collected data contains only cancelled state"
    assertEquals(listOf(Task.State.CANCELED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.CANCELED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("constraintsProvider")
  fun `Uninstall task cancelled on uninstallation`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will wait for cancellation after 25%"
    mocks.packageInstaller.progressFlow = cancellingFlow
    m And "outdated version app uninstall started with given constraints"
    val task = installManager.getApp(outdatedPackage).uninstall(constraints)

    m When "remember current task state"
    val initialState = task.state
    m And "collect the task state and progress"
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
    m And "remember final task state"
    val finalState = task.state

    m Then "first collected data ends with cancelled state on uninstall"
    assertEquals(canceledUninstall, result)
    m And "second collected data contains only cancelled state"
    assertEquals(listOf(Task.State.CANCELED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.CANCELED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("networkStateAndConstraintsProvider")
  fun `Installation task is completed on when allowed to download on metered`(
    comment: String,
    networkState: NetworkConnection.State,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "network has a given state"
    mocks.networkConnection.currentState = networkState
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "collect the task state and progress"
    val result = task.stateAndProgress.collectAsync(scope)
    m And "wait until the task finishes if it can"
    scope.advanceUntilIdle()
    m And "allow task to download on metered network"
    task.allowDownloadOnMetered()
    m And "collect the task state and progress after completion"
    val result2 = task.stateAndProgress.collectAsync(scope)
    m And "wait until the task finishes if it can now"
    scope.advanceUntilIdle()

    m Then "first collected data has the states corresponding to the network"
    assertEquals(
      when (networkState) {
        NetworkConnection.State.GONE -> listOf(Task.State.PENDING to -1)
        NetworkConnection.State.METERED -> listOf(Task.State.PENDING to -1)
        NetworkConnection.State.UNMETERED -> successfulInstall
      },
      result
    )
    m And "second collected data has the states corresponding to the network"
    assertEquals(
      when (networkState) {
        NetworkConnection.State.GONE -> listOf(Task.State.PENDING to -2)
        NetworkConnection.State.METERED -> listOf(Task.State.PENDING to -2) + successfulInstall.drop(1)
        NetworkConnection.State.UNMETERED -> listOf(Task.State.COMPLETED to -1)
      },
      result2
    )
  }

  companion object {
    private val downloadBeginning = listOf(
      Task.State.PENDING to -1,
      Task.State.DOWNLOADING to -1,
      Task.State.DOWNLOADING to 0,
      Task.State.DOWNLOADING to 25,
    )

    private val installBeginning = listOf(
      Task.State.DOWNLOADING to 50,
      Task.State.DOWNLOADING to 75,
      Task.State.READY_TO_INSTALL to -1,
      Task.State.INSTALLING to 0,
      Task.State.INSTALLING to 25,
    )

    private val successfulInstall = downloadBeginning + installBeginning + listOf(
      Task.State.INSTALLING to 50,
      Task.State.INSTALLING to 75,
      Task.State.COMPLETED to -1
    )

    private val uninstallBeginning = listOf(
      Task.State.PENDING to -1,
      Task.State.UNINSTALLING to -1,
      Task.State.UNINSTALLING to 0,
      Task.State.UNINSTALLING to 25,
    )

    private val successfulUninstall = uninstallBeginning + listOf(
      Task.State.UNINSTALLING to 50,
      Task.State.UNINSTALLING to 75,
      Task.State.COMPLETED to -1
    )

    private val failedInstall = downloadBeginning + installBeginning + (Task.State.FAILED to -1)

    private val failedUninstall = uninstallBeginning + (Task.State.FAILED to -1)

    private val abortedDownload = downloadBeginning + (Task.State.ABORTED to -1)

    private val abortedInstall = downloadBeginning + installBeginning + (Task.State.ABORTED to -1)

    private val abortedUninstall = uninstallBeginning + (Task.State.ABORTED to -1)

    private val canceledDownload = downloadBeginning + (Task.State.CANCELED to -1)

    private val canceledInstall = downloadBeginning + installBeginning + (Task.State.CANCELED to -1)

    private val canceledUninstall = uninstallBeginning + (Task.State.CANCELED to -1)

    @JvmStatic
    fun constraintsProvider(): Stream<Arguments> = constraints
      .map { Arguments.arguments(it.toString(), it) }
      .stream()

    @JvmStatic
    fun networkStateAndConstraintsProvider(): Stream<Arguments> = NetworkConnection.State.values()
      .map { state ->
        constraints.filter { it.networkType == NetworkType.UNMETERED }
          .map { constraints ->
            Arguments.arguments(
              "checkFS = ${constraints.checkForFreeSpace} | network = $state",
              state,
              constraints
            )
          }
      }
      .flatten()
      .stream()
  }
}
