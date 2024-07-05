package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.Constraints.NetworkType
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

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
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "wait for some progress to happen"
    task.stateAndProgress.first { it.first == Task.State.DOWNLOADING }

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
    val task = installManager.getApp(outdatedPackage).uninstall(constraints)

    m When "wait for some progress to happen"
    task.stateAndProgress.first { it.first != Task.State.PENDING }

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
    assertEquals(successfulInstallSequence, result)
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
  fun `Installation task is completed with enough free space`(
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
    assertEquals(successfulInstallSequence, result)
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
    assertEquals(successfulUninstallSequence, result)
    m And "second collected data contains only terminal state"
    assertEquals(listOf(Task.State.COMPLETED to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.COMPLETED, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("spaceConstraintsProvider")
  fun `Install task failed on download free space check`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "device storage mock has no free space available anymore"
    mocks.deviceStorageMock.availableFreeSpace = 0
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
        Task.State.OUT_OF_SPACE to -1
      ),
      result
    )
    m And "second collected data contains only failed state"
    assertEquals(listOf(Task.State.OUT_OF_SPACE to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.OUT_OF_SPACE, finalState)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("spaceConstraintsProvider")
  fun `Install task failed on installation free space check`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "outdated version app update started with given constraints"
    val task = installManager.getApp(outdatedPackage).install(installInfo, constraints)

    m When "device storage mock has no free space available anymore"
    mocks.deviceStorageMock.availableFreeSpace = 0
    m And "remember current task state"
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
        Task.State.OUT_OF_SPACE to -1
      ),
      result
    )
    m And "second collected data contains only failed state"
    assertEquals(listOf(Task.State.OUT_OF_SPACE to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.OUT_OF_SPACE, finalState)
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
    m And "package downloader mock will fail after some progress"
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

    m Then "first collected data ends with failed state after some progress of download"
    assertEquals(failedDownloadSequence, result)
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
  fun `Install task failed on download due to lack free of space`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will fail due to out of space after some progress"
    mocks.packageDownloader.progressFlow = outOfSpaceFlow
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

    m Then "first collected data ends with out of space state after some progress of download"
    assertEquals(outOfSpaceDownloadSequence, result)
    m And "second collected data contains only failed state"
    assertEquals(listOf(Task.State.OUT_OF_SPACE to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.OUT_OF_SPACE, finalState)
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
    m And "package installer mock will fail after some progress"
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
    assertEquals(failedInstallSequence, result)
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
  fun `Install task failed on installation due to lack free of space`(
    comment: String,
    constraints: Constraints,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will fail due to out of space after some progress"
    mocks.packageInstaller.progressFlow = outOfSpaceFlow
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

    m Then "first collected data ends with out of space state on install"
    assertEquals(outOfSpaceInstallSequence, result)
    m And "second collected data contains only failed state"
    assertEquals(listOf(Task.State.OUT_OF_SPACE to -1), result2)
    m And "there is no task info in the repo for the outdated app package name"
    assertNull(mocks.taskInfoRepository.get(outdatedPackage))
    m And "remembered task states are as expected"
    assertEquals(Task.State.PENDING, initialState)
    assertEquals(Task.State.OUT_OF_SPACE, finalState)
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
    m And "package installer mock will fail after some progress"
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
    m And "package downloader mock will abort after some progress"
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
    m And "package installer mock will abort after some progress"
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
    m And "package installer mock will abort after some progress"
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
  fun `Install task cancelled at once if cancelled immediately`(
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

    m Then "first collected data ends with cancelled state before download"
    assertEquals(
      listOf(
        Task.State.PENDING to -1,
        Task.State.CANCELED to -1
      ),
      result
    )
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
    task.stateAndProgress.first { it.first == Task.State.DOWNLOADING }
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
    task.stateAndProgress.first { it.first == Task.State.INSTALLING }
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
    task.stateAndProgress.first { it.first == Task.State.UNINSTALLING }
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
        NetworkConnection.State.GONE -> pendingSequence
        NetworkConnection.State.METERED -> pendingSequence
        NetworkConnection.State.UNMETERED -> successfulInstallSequence
      },
      result
    )
    m And "second collected data has the states corresponding to the network"
    assertEquals(
      when (networkState) {
        NetworkConnection.State.GONE -> pendingNetworkSequence
        NetworkConnection.State.METERED -> successfulNetworkInstallSequence
        NetworkConnection.State.UNMETERED -> completeSequence
      },
      result2
    )
  }

  companion object {

    @JvmStatic
    fun spaceConstraintsProvider(): Stream<Arguments> = constraints
      .filterNot { it.checkForFreeSpace }
      .map { Arguments.arguments(it.toString(), it) }
      .stream()

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
