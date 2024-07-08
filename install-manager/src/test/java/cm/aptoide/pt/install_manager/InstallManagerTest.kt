package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.*
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * AS a Developer,
 * I WANT to have an easy to use install manager SDK
 * WITH interchangeable storages, download and install implementations,
 * FOR managing the apps
 */
@ExperimentalCoroutinesApi
internal class InstallManagerTest {

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Create new apps`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)

    m When "get app for a given package name"
    val app = installManager.getApp(packageName)

    m Then "the app has the same package name"
    assertEquals(packageName, app.packageName)
    m And "the same package info as in package info repository"
    assertEquals(
      mocks.packageInfoRepository.info[packageName],
      app.packageInfo
    )
    m And "has no running task"
    assertNull(app.task)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `cache the app for the same package`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)

    m When "get app hash code for a given package name"
    val app = installManager.getApp(packageName).hashCode()
    m And "get app hash code for a given package name again"
    val sameApp = installManager.getApp(packageName).hashCode()

    m Then "both hash codes are equal"
    assertEquals(app, sameApp)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `do not cache the app for the same package on GC`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)

    m When "get app hashcode for a given package name"
    val app = installManager.getApp(packageName).hashCode()
    m And "GC run"
    System.gc()
    m And "get app hashcode for a given package name again"
    val sameApp = installManager.getApp(packageName).hashCode()

    m Then "hash codes are different"
    assertNotEquals(app, sameApp)
  }

  @Test
  fun `cache the app for the same package on GC while it has a scheduled task`() =
    coScenario { scope ->
      m Given "install manager initialised with mocks"
      val mocks = Mocks(scope)
      val installManager = InstallManager.with(mocks)

      m When "get app hash code for not installed app package name"
      val notInstalled = installManager.getApp(notInstalledPackage).hashCode()
      m And "get app hash code for outdated version app package name"
      val outdated = installManager.getApp(outdatedPackage).hashCode()
      m And "get app hash code for current version app package name"
      val current = installManager.getApp(currentPackage).hashCode()
      m And "not installed app install scheduled"
      installManager.getApp(notInstalledPackage).install(installInfo)
      m And "outdated version app update scheduled"
      installManager.getApp(outdatedPackage).install(installInfo)
      m And "current version app uninstall scheduled"
      installManager.getApp(currentPackage).uninstall()
      m And "get app hash code for not installed app package name after task scheduled"
      val notInstalledAfterScheduled = installManager.getApp(notInstalledPackage).hashCode()
      m And "get app hash code for outdated version app package name after task scheduled"
      val outdatedAfterScheduled = installManager.getApp(outdatedPackage).hashCode()
      m And "get app hash code for current version app package name after task scheduled"
      val currentAfterScheduled = installManager.getApp(currentPackage).hashCode()
      m And "GC run"
      System.gc()
      m And "get app hash code for not installed app package name after gc run"
      val notInstalledAfterGC = installManager.getApp(notInstalledPackage).hashCode()
      m And "get app hash code for outdated version app package name after gc run"
      val outdatedAfterGC = installManager.getApp(outdatedPackage).hashCode()
      m And "get app hash code for current version app package name after gc run"
      val currentAfterGC = installManager.getApp(currentPackage).hashCode()
      m And "wait until all now runnable tasks finish"
      scope.advanceUntilIdle()
      m And "get app hash code for not installed app package name after task finished"
      val notInstalledAfterFinished = installManager.getApp(notInstalledPackage).hashCode()
      m And "get app hash code for outdated version app package name after task finished"
      val outdatedAfterFinished = installManager.getApp(outdatedPackage).hashCode()
      m And "get app hash code for current version app package name after task finished"
      val currentAfterFinished = installManager.getApp(currentPackage).hashCode()
      m And "GC run again"
      System.gc()
      m And "get app hash code for not installed app package name after gc run after task finished"
      val notInstalledAfterFinishedGC = installManager.getApp(notInstalledPackage).hashCode()
      m And "get app hash code for outdated version app package name after gc run after task finished"
      val outdatedAfterFinishedGC = installManager.getApp(outdatedPackage).hashCode()
      m And "get app hash code for current version app package name after gc run after task finished"
      val currentAfterFinishedGC = installManager.getApp(currentPackage).hashCode()

      m Then "hash codes for each package name before task finished are always equal"
      assertEquals(notInstalled, notInstalledAfterScheduled)
      assertEquals(notInstalledAfterScheduled, notInstalledAfterGC)
      assertEquals(notInstalledAfterGC, notInstalledAfterFinished)
      assertEquals(outdated, outdatedAfterScheduled)
      assertEquals(outdatedAfterScheduled, outdatedAfterGC)
      assertEquals(outdatedAfterGC, outdatedAfterFinished)
      assertEquals(current, currentAfterScheduled)
      assertEquals(currentAfterScheduled, currentAfterGC)
      assertEquals(currentAfterGC, currentAfterFinished)
      m And "hash codes are different after gc run after task finished"
      assertNotEquals(notInstalledAfterFinished, notInstalledAfterFinishedGC)
      assertNotEquals(outdatedAfterFinished, outdatedAfterFinishedGC)
      assertNotEquals(currentAfterFinished, currentAfterFinishedGC)
    }

  @Test
  fun `Return installed apps`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)

    m When "get all known apps"
    val apps = installManager.installedApps

    m Then "there are 9 installed apps"
    assertEquals(9, apps.size)
    m And "and their package names are the same as in package info repository"
    assertEquals(
      mocks.packageInfoRepository.info.values.toList(),
      apps.map { it.packageInfo }
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedsAndNetworkChangesProvider")
  fun `Return no scheduled apps if idle`(
    comment: String,
    initialNetworkState: NetworkConnection.State,
    newNetworkState: NetworkConnection.State,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "network has a given state"
    mocks.networkConnection.currentState = initialNetworkState
    m And "mocks operate with given speeds"
    mocks.taskInfoRepository.setSpeed(taskInfoSpeed)
    mocks.packageDownloader.setSpeed(downloaderSpeed)
    mocks.packageInstaller.setSpeed(installerSpeed)

    m When "get currently scheduled apps"
    val scheduledInitially = installManager.scheduledApps
    m And "get currently scheduled apps after first runnable task started"
    scope.advanceTimeBy(10.seconds)
    val scheduledAfterStart = installManager.scheduledApps
    m And "wait until all runnable tasks finish"
    scope.advanceTimeBy(3.hours)
    m And "get currently scheduled apps after runnable tasks finished"
    val scheduledAfterFinish = installManager.scheduledApps
    m And "network changes to a new state"
    mocks.networkConnection.update(newNetworkState)
    m And "get currently scheduled apps after first now runnable task started"
    scope.advanceTimeBy(10.seconds)
    val scheduledAfterNetworkChang = installManager.scheduledApps
    m And "get currently scheduled apps after now runnable tasks finished"
    scope.advanceUntilIdle()
    val scheduledAfterAllFinish = installManager.scheduledApps

    m Then "there were no apps with scheduled tasks"
    assertTrue(scheduledInitially.isEmpty())
    assertTrue(scheduledAfterStart.isEmpty())
    assertTrue(scheduledAfterFinish.isEmpty())
    assertTrue(scheduledAfterNetworkChang.isEmpty())
    assertTrue(scheduledAfterAllFinish.isEmpty())
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedsAndNetworkChangesProvider")
  fun `Return scheduled tasks apps for new tasks ordered by creation time`(
    comment: String,
    initialNetworkState: NetworkConnection.State,
    newNetworkState: NetworkConnection.State,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "network has a given state"
    mocks.networkConnection.currentState = initialNetworkState
    m And "mocks operate with given speeds"
    mocks.taskInfoRepository.setSpeed(taskInfoSpeed)
    mocks.packageDownloader.setSpeed(downloaderSpeed)
    mocks.packageInstaller.setSpeed(installerSpeed)

    m When "not installed app install started"
    val notInstalledTask = installManager.getApp(notInstalledPackage).install(installInfo)
    m And "outdated version app update stated with unmetered network constraint"
    scope.advanceTimeBy(1.milliseconds)
    val outdatedTask = installManager.getApp(outdatedPackage).install(
      installPackageInfo = installInfo,
      constraints = Constraints(
        checkForFreeSpace = true,
        networkType = Constraints.NetworkType.UNMETERED
      )
    )
    m And "current version app uninstall started"
    scope.advanceTimeBy(1.milliseconds)
    val currentTask = installManager.getApp(currentPackage).uninstall()
    m And "get currently scheduled apps"
    val scheduledInitially = installManager.scheduledApps
    m And "get currently scheduled apps after first runnable task started"
    scope.advanceTimeBy(10.seconds)
    val scheduledAfterStart = installManager.scheduledApps
    m And "wait until all runnable tasks finish"
    scope.advanceTimeBy(3.hours)
    m And "get currently scheduled apps after runnable tasks finished"
    val scheduledAfterFinish = installManager.scheduledApps
    m And "network changes to a new state"
    mocks.networkConnection.update(newNetworkState)
    m And "get currently scheduled apps after first now runnable task started"
    scope.advanceTimeBy(10.seconds)
    val scheduledAfterNetworkChang = installManager.scheduledApps
    m And "get currently scheduled apps after now runnable tasks finished"
    scope.advanceUntilIdle()
    val scheduledAfterAllFinish = installManager.scheduledApps

    m Then "all apps tasks were scheduled initially"
    val newTasks = listOf(notInstalledTask, outdatedTask, currentTask)
    val allApps = newTasks.map(Task::packageName).map(installManager::getApp)
    val runnableApps = getRunnableTasks(initialNetworkState, newTasks)
      .map(installManager::getApp)
    val runnableAppsNewNetwork = getRunnableTasks(newNetworkState, newTasks)
      .map(installManager::getApp)
      .minus(runnableApps.toSet())
    assertEquals(
      allApps,
      scheduledInitially
    )
    m And "all apps tasks were scheduled except the first one after it was started"
    assertEquals(
      allApps - runnableApps.firstOrNull(),
      scheduledAfterStart
    )
    m And "with a new network apps tasks that were not runnable were scheduled"
    assertEquals(
      allApps - runnableApps.toSet(),
      scheduledAfterFinish
    )
    m And "with a new network apps tasks that were not runnable were scheduled except the first one after it was started"
    assertEquals(
      allApps - runnableApps.toSet() - runnableAppsNewNetwork.firstOrNull(),
      scheduledAfterNetworkChang
    )
    m And "after all runnable tasks finished only apps tasks that can't run left scheduled"
    assertEquals(
      allApps - runnableApps.toSet() - runnableAppsNewNetwork.toSet(),
      scheduledAfterAllFinish
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedsAndNetworkChangesProvider")
  fun `Return scheduled tasks apps for restored tasks ordered by timestamp`(
    comment: String,
    initialNetworkState: NetworkConnection.State,
    newNetworkState: NetworkConnection.State,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "network has a given state"
    mocks.networkConnection.currentState = initialNetworkState
    m And "mocks operate with given speeds"
    mocks.taskInfoRepository.setSpeed(taskInfoSpeed)
    mocks.packageDownloader.setSpeed(downloaderSpeed)
    mocks.packageInstaller.setSpeed(installerSpeed)

    m When "restore saved tasks"
    installManager.restore()
    m And "get currently scheduled apps"
    val scheduledInitially = installManager.scheduledApps
    m And "get currently scheduled apps after first scheduled runnable task started"
    scope.advanceTimeBy(10.seconds)
    val scheduledAfterStart = installManager.scheduledApps
    m And "wait until all runnable tasks finish"
    scope.advanceTimeBy(3.hours)
    m And "get currently scheduled apps after runnable tasks finished"
    val scheduledAfterFinish = installManager.scheduledApps
    m And "network changes to a new state"
    mocks.networkConnection.update(newNetworkState)
    m And "get currently scheduled apps after first scheduled now runnable task started"
    scope.advanceTimeBy(10.seconds)
    val scheduledAfterNetworkChang = installManager.scheduledApps
    m And "get currently scheduled apps after now runnable tasks finished"
    scope.advanceUntilIdle()
    val scheduledAfterAllFinish = installManager.scheduledApps

    m Then "all apps tasks were scheduled initially"
    val restoredApps = savedTasksInfo.map(TaskInfo::packageName).map(installManager::getApp)
    val restoredRunnableApps = getRunnableSavedTasksPackages(initialNetworkState)
      .map(installManager::getApp)
    val restoredRunnableAppsNewNetwork = getRunnableSavedTasksPackages(newNetworkState)
      .map(installManager::getApp)
      .minus(restoredRunnableApps.toSet())
    assertEquals(
      restoredApps,
      scheduledInitially
    )
    m And "all apps tasks were scheduled except the first one after it was started"
    assertEquals(
      restoredApps - restoredRunnableApps.firstOrNull(),
      scheduledAfterStart
    )
    m And "with a new network apps tasks that were not runnable were scheduled"
    assertEquals(
      restoredApps - restoredRunnableApps.toSet(),
      scheduledAfterFinish
    )
    m And "with a new network apps tasks that were not runnable were scheduled except the first one after it was started"
    assertEquals(
      restoredApps - restoredRunnableApps.toSet() - restoredRunnableAppsNewNetwork.firstOrNull(),
      scheduledAfterNetworkChang
    )
    m And "after all runnable tasks finished only apps tasks that can't run left scheduled"
    assertEquals(
      restoredApps - restoredRunnableApps.toSet() - restoredRunnableAppsNewNetwork.toSet(),
      scheduledAfterAllFinish
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedsAndNetworkChangesProvider")
  fun `Return scheduled tasks apps for restored tasks ordered by timestamp and for new tasks ordered by creation time`(
    comment: String,
    initialNetworkState: NetworkConnection.State,
    newNetworkState: NetworkConnection.State,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "network has a given state"
    mocks.networkConnection.currentState = initialNetworkState
    m And "mocks operate with given speeds"
    mocks.taskInfoRepository.setSpeed(taskInfoSpeed)
    mocks.packageDownloader.setSpeed(downloaderSpeed)
    mocks.packageInstaller.setSpeed(installerSpeed)

    m When "restore saved tasks"
    installManager.restore()
    m And "get currently scheduled apps"
    val scheduledInitially = installManager.scheduledApps
    m And "wait until some task starts"
    scope.advanceTimeBy(10.seconds)
    m And "not installed app install started"
    val notInstalledTask = installManager.getApp(notInstalledPackage).install(installInfo)
    m And "outdated version app update stated with unmetered network constraint"
    scope.advanceTimeBy(1.milliseconds)
    val outdatedTask = installManager.getApp(outdatedPackage).install(
      installPackageInfo = installInfo,
      constraints = Constraints(
        checkForFreeSpace = true,
        networkType = Constraints.NetworkType.UNMETERED
      )
    )
    m And "current version app uninstall started"
    scope.advanceTimeBy(1.milliseconds)
    val currentTask = installManager.getApp(currentPackage).uninstall()
    m And "get currently scheduled apps after first scheduled runnable task started"
    scope.advanceTimeBy(10.seconds)
    val scheduledAfterStart = installManager.scheduledApps
    m And "wait until all runnable tasks finish"
    scope.advanceTimeBy(4.hours)
    m And "get currently scheduled apps after runnable tasks finished"
    val scheduledAfterFinish = installManager.scheduledApps
    m And "network changes to a new state"
    mocks.networkConnection.update(newNetworkState)
    m And "get currently scheduled apps after first scheduled now runnable task started"
    scope.advanceTimeBy(10.seconds)
    val scheduledAfterNetworkChang = installManager.scheduledApps
    m And "get currently scheduled apps after now runnable tasks finished"
    scope.advanceUntilIdle()
    val scheduledAfterAllFinish = installManager.scheduledApps

    m Then "all apps tasks were scheduled initially except the first running one"
    val newTasks = listOf(notInstalledTask, outdatedTask, currentTask)
    val restoredApps = savedTasksInfo.map(TaskInfo::packageName).map(installManager::getApp)
    val otherApps = newTasks.map(Task::packageName).map(installManager::getApp)
    val restoredRunnableApps = getRunnableSavedTasksPackages(initialNetworkState)
      .map(installManager::getApp)
    val restoredRunnableAppsNewNetwork = getRunnableSavedTasksPackages(newNetworkState)
      .map(installManager::getApp)
      .minus(restoredRunnableApps.toSet())
    val runnableApps = getRunnableTasks(initialNetworkState, newTasks)
      .map(installManager::getApp)
    val runnableAppsNewNetwork = getRunnableTasks(newNetworkState, newTasks)
      .map(installManager::getApp)
      .minus(runnableApps.toSet())
    assertEquals(
      restoredApps + otherApps - otherApps,
      scheduledInitially
    )
    m And "all apps tasks were scheduled except the first one after it was started"
    assertEquals(
      restoredApps + otherApps - restoredRunnableApps.firstOrNull(),
      scheduledAfterStart
    )
    m And "with a new network apps tasks that were not runnable were scheduled"
    assertEquals(
      restoredApps + otherApps - restoredRunnableApps.toSet() - runnableApps.toSet(),
      scheduledAfterFinish
    )
    m And "with a new network apps tasks that were not runnable were scheduled except the first one after it was started"
    assertEquals(
      restoredApps + otherApps - restoredRunnableApps.toSet() - runnableApps.toSet() -
        restoredRunnableAppsNewNetwork.firstOrNull(),
      scheduledAfterNetworkChang
    )
    m And "after all runnable tasks finished only apps tasks that can't run left scheduled"
    assertEquals(
      restoredApps + otherApps - restoredRunnableApps.toSet() - runnableApps.toSet() -
        restoredRunnableAppsNewNetwork.toSet() - runnableAppsNewNetwork.toSet(),
      scheduledAfterAllFinish
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedsAndNetworkChangesProvider")
  fun `Return scheduled tasks apps for restored tasks ordered by timestamp and then for new tasks ordered by creation time`(
    comment: String,
    initialNetworkState: NetworkConnection.State,
    newNetworkState: NetworkConnection.State,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "network has a given state"
    mocks.networkConnection.currentState = initialNetworkState
    m And "mocks operate with given speeds"
    mocks.taskInfoRepository.setSpeed(taskInfoSpeed)
    mocks.packageDownloader.setSpeed(downloaderSpeed)
    mocks.packageInstaller.setSpeed(installerSpeed)

    m When "restore saved tasks"
    installManager.restore()
    m And "get currently scheduled apps"
    val scheduledInitially = installManager.scheduledApps
    m And "wait until all restored runnable tasks finish"
    scope.advanceTimeBy(3.hours)
    m And "not installed app install started"
    val notInstalledTask = installManager.getApp(notInstalledPackage).install(installInfo)
    m And "outdated version app update stated with unmetered network constraint"
    scope.advanceTimeBy(1.milliseconds)
    val outdatedTask = installManager.getApp(outdatedPackage).install(
      installPackageInfo = installInfo,
      constraints = Constraints(
        checkForFreeSpace = true,
        networkType = Constraints.NetworkType.UNMETERED
      )
    )
    m And "current version app uninstall started"
    scope.advanceTimeBy(1.milliseconds)
    val currentTask = installManager.getApp(currentPackage).uninstall()
    m And "get currently scheduled apps after first scheduled runnable task started"
    scope.advanceTimeBy(10.seconds)
    val scheduledAfterStart = installManager.scheduledApps
    m And "wait until all runnable tasks finish"
    scope.advanceTimeBy(4.hours)
    m And "get currently scheduled apps after runnable tasks finished"
    val scheduledAfterFinish = installManager.scheduledApps
    m And "network changes to a new state"
    mocks.networkConnection.update(newNetworkState)
    m And "get currently scheduled apps after first scheduled now runnable task started"
    scope.advanceTimeBy(10.seconds)
    val scheduledAfterNetworkChang = installManager.scheduledApps
    m And "get currently scheduled apps after now runnable tasks finished"
    scope.advanceUntilIdle()
    val scheduledAfterAllFinish = installManager.scheduledApps

    m Then "restored apps tasks were scheduled initially except the first running one"
    val newTasks = listOf(notInstalledTask, outdatedTask, currentTask)
    val restoredApps = savedTasksInfo.map(TaskInfo::packageName).map(installManager::getApp)
    val otherApps = newTasks.map(Task::packageName).map(installManager::getApp)
    val restoredRunnableApps = getRunnableSavedTasksPackages(initialNetworkState)
      .map(installManager::getApp)
    val restoredRunnableAppsNewNetwork = getRunnableSavedTasksPackages(newNetworkState)
      .map(installManager::getApp)
      .minus(restoredRunnableApps.toSet())
    val runnableApps = getRunnableTasks(initialNetworkState, newTasks)
      .map(installManager::getApp)
    val runnableAppsNewNetwork = getRunnableTasks(newNetworkState, newTasks)
      .map(installManager::getApp)
      .minus(runnableApps.toSet())
    assertEquals(
      restoredApps - otherApps.toSet(),
      scheduledInitially
    )
    m And "all apps tasks were scheduled except all restored runnable ones and first other one after it was started"
    assertEquals(
      restoredApps + otherApps - restoredRunnableApps.toSet() - runnableApps.firstOrNull(),
      scheduledAfterStart
    )
    m And "with a new network apps tasks that were not runnable were scheduled"
    assertEquals(
      restoredApps + otherApps - restoredRunnableApps.toSet() - runnableApps.toSet(),
      scheduledAfterFinish
    )
    m And "with a new network apps tasks that were not runnable were scheduled except the first one after it was started"
    assertEquals(
      restoredApps + otherApps - restoredRunnableApps.toSet() - runnableApps.toSet() -
        restoredRunnableAppsNewNetwork.firstOrNull(),
      scheduledAfterNetworkChang
    )
    m And "after all runnable tasks finished only apps tasks that can't run left scheduled"
    assertEquals(
      restoredApps + otherApps - restoredRunnableApps.toSet() - runnableApps.toSet() -
        restoredRunnableAppsNewNetwork.toSet() - runnableAppsNewNetwork.toSet(),
      scheduledAfterAllFinish
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedsAndNetworkChangesProvider")
  fun `Return no running apps if idle`(
    comment: String,
    initialNetworkState: NetworkConnection.State,
    newNetworkState: NetworkConnection.State,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "network has a given state"
    mocks.networkConnection.currentState = initialNetworkState
    m And "mocks operate with given speeds"
    mocks.taskInfoRepository.setSpeed(taskInfoSpeed)
    mocks.packageDownloader.setSpeed(downloaderSpeed)
    mocks.packageInstaller.setSpeed(installerSpeed)
    m And "not installed app got"
    val notInstalled = installManager.getApp(notInstalledPackage)
    m And "outdated version installed app got"
    val outdated = installManager.getApp(outdatedPackage)
    m And "current version installed app got"
    val current = installManager.getApp(currentPackage)
    m And "newer version installed app got"
    val newer = installManager.getApp(newerPackage)

    m When "collecting apps with running tasks asynchronously started"
    val result = installManager.workingAppInstallers.collectAsync(scope)
    m And "wait until all runnable tasks finish"
    scope.advanceTimeBy(3.hours)
    m And "network changes to a new state"
    mocks.networkConnection.update(newNetworkState)
    m And "wait until all now runnable tasks finish"
    scope.advanceUntilIdle()

    m Then "nothing was collected"
    assertEquals(listOf(null), result)
    m And "all those apps had no tasks"
    listOf(notInstalled.task, outdated.task, current.task, newer.task)
      .forEach { assertNull(it) }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedsAndNetworkChangesProvider")
  fun `Return running apps for new tasks ordered by creation time`(
    comment: String,
    initialNetworkState: NetworkConnection.State,
    newNetworkState: NetworkConnection.State,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "network has a given state"
    mocks.networkConnection.currentState = initialNetworkState
    m And "mocks operate with given speeds"
    mocks.taskInfoRepository.setSpeed(taskInfoSpeed)
    mocks.packageDownloader.setSpeed(downloaderSpeed)
    mocks.packageInstaller.setSpeed(installerSpeed)
    m And "collecting working apps asynchronously started"
    val result = installManager.workingAppInstallers.collectAsync(scope)

    m When "not installed app install started"
    val notInstalledTask = installManager.getApp(notInstalledPackage).install(installInfo)
    m And "outdated version app update stated with unmetered network constraint"
    val outdatedTask = installManager.getApp(outdatedPackage).install(
      installPackageInfo = installInfo,
      constraints = Constraints(
        checkForFreeSpace = true,
        networkType = Constraints.NetworkType.UNMETERED
      )
    )
    m And "current version app uninstall started"
    val currentTask = installManager.getApp(currentPackage).uninstall()
    m And "wait until all runnable tasks finish"
    scope.advanceTimeBy(3.hours)
    m And "network changes to a new state"
    mocks.networkConnection.update(newNetworkState)
    m And "wait until all now runnable tasks finish"
    scope.advanceUntilIdle()

    m Then "apps with working new tasks ordered by creation time for a given initial network state"
    m And "then apps with working new tasks ordered by creation time for a given changed network state collected"
    val newTasks = listOf(notInstalledTask, outdatedTask, currentTask)
    val runnableApps = getRunnableTasks(initialNetworkState, newTasks)
      .map(installManager::getApp)
    val runnableAppsNewNetwork = getRunnableTasks(newNetworkState, newTasks)
      .map(installManager::getApp)
      .minus(runnableApps.toSet())
      .takeUnless(List<App>::isEmpty)
    assertEquals(
      listOf(null) + runnableApps + null +
        (runnableAppsNewNetwork?.plus(null) ?: emptyList()),
      result
    )
    m And "all new tasks were not null"
    newTasks.forEach {
      assertNotNull(it)
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedsAndNetworkChangesProvider")
  fun `Return running apps for restored tasks ordered by timestamp`(
    comment: String,
    initialNetworkState: NetworkConnection.State,
    newNetworkState: NetworkConnection.State,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "network has a given state"
    mocks.networkConnection.currentState = initialNetworkState
    m And "mocks operate with given speeds"
    mocks.taskInfoRepository.setSpeed(taskInfoSpeed)
    mocks.packageDownloader.setSpeed(downloaderSpeed)
    mocks.packageInstaller.setSpeed(installerSpeed)
    m And "collecting working apps asynchronously started"
    val result = installManager.workingAppInstallers.collectAsync(scope)

    m When "restore saved tasks"
    installManager.restore()
    m And "remember tasks from apps for all package names in saved task info"
    val restoredTasks = savedTasksInfo.map { installManager.getApp(it.packageName).task }
    m And "wait until all runnable tasks finish"
    scope.advanceTimeBy(3.hours)
    m And "network changes to a new state"
    mocks.networkConnection.update(newNetworkState)
    m And "wait until all now runnable tasks finish"
    scope.advanceUntilIdle()

    m Then "apps with running restored tasks ordered by timestamps for a given initial network state"
    m And "then apps with running restored tasks ordered by timestamps for a given changed network state collected"
    val restoredRunnableApps = getRunnableSavedTasksPackages(initialNetworkState)
      .map(installManager::getApp)
    val restoredRunnableAppsNewNetwork = getRunnableSavedTasksPackages(newNetworkState)
      .map(installManager::getApp)
      .minus(restoredRunnableApps.toSet())
      .takeUnless(List<App>::isEmpty)
    assertEquals(
      listOf(null) + restoredRunnableApps + null +
        (restoredRunnableAppsNewNetwork?.plus(null) ?: emptyList()),
      result
    )
    m And "all remembered tasks were not null"
    restoredTasks.forEach {
      assertNotNull(it)
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedsAndNetworkChangesProvider")
  fun `Return running apps for restored tasks ordered by timestamp and for new tasks ordered by creation time`(
    comment: String,
    initialNetworkState: NetworkConnection.State,
    newNetworkState: NetworkConnection.State,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "network has a given state"
    mocks.networkConnection.currentState = initialNetworkState
    m And "mocks operate with given speeds"
    mocks.taskInfoRepository.setSpeed(taskInfoSpeed)
    mocks.packageDownloader.setSpeed(downloaderSpeed)
    mocks.packageInstaller.setSpeed(installerSpeed)
    m And "collecting working apps asynchronously started"
    val result = installManager.workingAppInstallers.collectAsync(scope)

    m When "restore saved tasks"
    installManager.restore()
    m And "remember tasks from apps for all package names in saved task info"
    val restoredTasks = savedTasksInfo.map { installManager.getApp(it.packageName).task }
    m And "wait until some task starts"
    scope.advanceTimeBy(10.seconds)
    m And "not installed app install started"
    val notInstalledTask = installManager.getApp(notInstalledPackage).install(installInfo)
    m And "outdated version app update stated with unmetered network constraint"
    val outdatedTask = installManager.getApp(outdatedPackage).install(
      installPackageInfo = installInfo,
      constraints = Constraints(
        checkForFreeSpace = true,
        networkType = Constraints.NetworkType.UNMETERED
      )
    )
    m And "current version app uninstall started"
    val currentTask = installManager.getApp(currentPackage).uninstall()
    m And "wait until all runnable tasks finish"
    scope.advanceTimeBy(4.hours)
    m And "network changes to a new state"
    mocks.networkConnection.update(newNetworkState)
    m And "wait until all now runnable tasks finish"
    scope.advanceUntilIdle()

    m Then "apps with running restored tasks ordered by timestamps for a given initial network state"
    m And "then apps with working new tasks ordered by creation time for a given initial network state"
    m And "then after a pause apps with running restored tasks ordered by timestamps for a given changed network state"
    m And "then apps with working new tasks ordered by creation time for a given changed network state collected"
    val newTasks = listOf(notInstalledTask, outdatedTask, currentTask)
    val restoredRunnableApps = getRunnableSavedTasksPackages(initialNetworkState)
      .map(installManager::getApp)
    val restoredRunnableAppsNewNetwork = getRunnableSavedTasksPackages(newNetworkState)
      .map(installManager::getApp)
      .minus(restoredRunnableApps.toSet())
      .takeUnless(List<App>::isEmpty)
    val runnableApps = getRunnableTasks(initialNetworkState, newTasks)
      .map(installManager::getApp)
    val runnableAppsNewNetwork = getRunnableTasks(newNetworkState, newTasks)
      .map(installManager::getApp)
      .minus(runnableApps.toSet())
      .takeUnless(List<App>::isEmpty)
    assertEquals(
      listOf(null) + restoredRunnableApps + runnableApps + null +
        (restoredRunnableAppsNewNetwork ?: emptyList()) +
        (runnableAppsNewNetwork?.plus(null) ?: emptyList()),
      result
    )
    m And "all remembered tasks were not null"
    restoredTasks.forEach {
      assertNotNull(it)
    }
    newTasks.forEach {
      assertNotNull(it)
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedsAndNetworkChangesProvider")
  fun `Return running apps for restored tasks order and then for new tasks ordered by creation time`(
    comment: String,
    initialNetworkState: NetworkConnection.State,
    newNetworkState: NetworkConnection.State,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "network has a given state"
    mocks.networkConnection.currentState = initialNetworkState
    m And "mocks operate with given speeds"
    mocks.taskInfoRepository.setSpeed(taskInfoSpeed)
    mocks.packageDownloader.setSpeed(downloaderSpeed)
    mocks.packageInstaller.setSpeed(installerSpeed)
    m And "collecting working apps asynchronously started"
    val result = installManager.workingAppInstallers.collectAsync(scope)

    m When "restore saved tasks"
    installManager.restore()
    m And "remember tasks from apps for all package names in saved task info"
    val restoredTasks = savedTasksInfo.map { installManager.getApp(it.packageName).task }
    m And "wait until all restored runnable tasks finish"
    scope.advanceTimeBy(3.hours)
    m And "not installed app install started"
    val notInstalledTask = installManager.getApp(notInstalledPackage).install(installInfo)
    m And "outdated version app update stated with unmetered network constraint"
    val outdatedTask = installManager.getApp(outdatedPackage).install(
      installPackageInfo = installInfo,
      constraints = Constraints(
        checkForFreeSpace = true,
        networkType = Constraints.NetworkType.UNMETERED
      )
    )
    m And "current version app uninstall started"
    val currentTask = installManager.getApp(currentPackage).uninstall()
    m And "wait until all new runnable tasks finish"
    scope.advanceTimeBy(3.hours)
    m And "network changes to a new state"
    mocks.networkConnection.update(newNetworkState)
    m And "wait until all now runnable tasks finish"
    scope.advanceUntilIdle()

    m Then "apps with running restored tasks ordered by timestamps for a given initial network state"
    m And "then after a pause apps with working new tasks ordered by creation time for a given initial network state"
    m And "then after a pause apps with running restored tasks ordered by timestamps for a given changed network state"
    m And "then apps with working new tasks ordered by creation time for a given changed network state collected"
    val newTasks = listOf(notInstalledTask, outdatedTask, currentTask)
    val restoredRunnableApps = getRunnableSavedTasksPackages(initialNetworkState)
      .map(installManager::getApp)
    val restoredRunnableAppsNewNetwork = getRunnableSavedTasksPackages(newNetworkState)
      .map(installManager::getApp)
      .minus(restoredRunnableApps.toSet())
      .takeUnless(List<App>::isEmpty)
    val runnableApps = getRunnableTasks(initialNetworkState, newTasks)
      .map(installManager::getApp)
    val runnableAppsNewNetwork = getRunnableTasks(newNetworkState, newTasks)
      .map(installManager::getApp)
      .minus(runnableApps.toSet())
      .takeUnless(List<App>::isEmpty)
    assertEquals(
      listOf(null) + restoredRunnableApps + null + runnableApps + null +
        (restoredRunnableAppsNewNetwork ?: emptyList()) +
        (runnableAppsNewNetwork?.plus(null) ?: emptyList()),
      result
    )
    m And "all remembered tasks were not null"
    restoredTasks.forEach {
      assertNotNull(it)
    }
    newTasks.forEach {
      assertNotNull(it)
    }
  }

  @Test
  fun `Error restoring install manager if get all tasks fails`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "task info repository mocks will fail"
    mocks.taskInfoRepository.shouldFail = true

    m When "install manager restore called"
    val thrown = assertThrows<RuntimeException> { installManager.restore() }

    m Then "an exception is thrown"
    assertEquals("Problem!", thrown.message)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return apps changes`(
    comment: String,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "mocks operate with given speeds"
    mocks.taskInfoRepository.setSpeed(taskInfoSpeed)
    mocks.packageDownloader.setSpeed(downloaderSpeed)
    mocks.packageInstaller.setSpeed(installerSpeed)
    m And "collecting apps changes asynchronously started"
    val result = installManager.appsChanges.collectAsync(scope)
    m And "outdated version installed app got"
    val outdated = installManager.getApp(outdatedPackage)
    m And "newer version installed app got"
    val newer = installManager.getApp(newerPackage)
    m And "not installed app got"
    val notInstalled = installManager.getApp(notInstalledPackage)
    m And "current version installed app got"
    val current = installManager.getApp(currentPackage)

    m When "current version app package info added to the repository mock"
    mocks.packageInfoRepository.update(currentPackage, installedInfo(currentPackage))
    m And "outdated version app package info removed from the repository mock"
    mocks.packageInfoRepository.update(outdatedPackage, null)
    m And "not installed app package info added to the repository mock"
    mocks.packageInfoRepository.update(notInstalledPackage, installedInfo(notInstalledPackage))
    m And "newer version app package info removed from the repository mock"
    mocks.packageInfoRepository.update(newerPackage, null)
    m And "outdated version app package info added to the repository mock"
    mocks.packageInfoRepository.update(outdatedPackage, installedInfo(outdatedPackage, 2))
    m And "newer version app package info added to the repository mock"
    mocks.packageInfoRepository.update(newerPackage, installedInfo(newerPackage))
    m And "wait until all coroutines finish"
    scope.advanceUntilIdle()

    m Then "changed apps collected in the same order"
    assertEquals(listOf(current, outdated, notInstalled, newer, outdated, newer), result)
  }

  @Test
  fun `Return current missing free space`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "free space checker mock will report there will be -1536 of free space missing"
    mocks.deviceStorageMock.availableFreeSpace = -1536
    m And "results collector initialised"
    val results = mutableListOf<Long>()

    m When "collect missing space"
    results.add(installManager.getMissingFreeSpaceFor(installInfo))
    m And "free space checker mock will report there will be 0 of free space missing"
    mocks.deviceStorageMock.availableFreeSpace = 0
    m And "collect missing space again"
    results.add(installManager.getMissingFreeSpaceFor(installInfo))
    m And "free space checker mock will report there will be 1536 of free space missing"
    mocks.deviceStorageMock.availableFreeSpace = 1536
    m And "collect missing space one more time"
    results.add(installManager.getMissingFreeSpaceFor(installInfo))

    m Then "the created task is of install type"
    assertEquals(listOf(1786L, 250L, -1286L), results.toList())
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return missing free space on apps tasks scheduling excluding uninstalls and cancelled tasks`(
    comment: String,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "free space checker mock will report there will be 536 of free space missing"
    mocks.deviceStorageMock.availableFreeSpace = 536
    m And "results collector initialised"
    val results = mutableListOf<Long>()

    m When "collect missing space"
    results.add(installManager.getMissingFreeSpaceFor(installInfo))
    m And "not installed app install scheduled"
    val installTask = installManager.getApp(notInstalledPackage).install(
      installPackageInfo = installInfo,
      constraints = Constraints(
        checkForFreeSpace = true,
        networkType = Constraints.NetworkType.ANY
      )
    )
    m And "collect missing space after install task enqueued"
    scope.advanceTimeBy(2.seconds)
    results.add(installManager.getMissingFreeSpaceFor(installInfo))
    m And "outdated app update scheduled"
    val updateTask = installManager.getApp(outdatedPackage).install(
      installPackageInfo = installInfo,
      constraints = Constraints(
        checkForFreeSpace = false,
        networkType = Constraints.NetworkType.UNMETERED
      )
    )
    m And "collect missing space after update task enqueued"
    scope.advanceTimeBy(2.seconds)
    results.add(installManager.getMissingFreeSpaceFor(installInfo))
    m And "current app uninstall scheduled"
    installManager.getApp(currentPackage).uninstall()
    m And "collect missing space after uninstall task enqueued"
    scope.advanceTimeBy(2.seconds)
    results.add(installManager.getMissingFreeSpaceFor(installInfo))
    m And "outdated app update cancelled"
    updateTask.cancel()
    m And "collect missing space after update task cancelled"
    scope.advanceTimeBy(2.seconds)
    results.add(installManager.getMissingFreeSpaceFor(installInfo))
    m And "outdated app uninstall scheduled"
    installManager.getApp(outdatedPackage).uninstall()
    m And "collect missing space after outdated app uninstall task enqueued"
    scope.advanceTimeBy(2.seconds)
    results.add(installManager.getMissingFreeSpaceFor(installInfo))
    m And "not installed app install cancelled"
    installTask.cancel()
    m And "collect missing space after install task cancelled"
    scope.advanceUntilIdle()
    results.add(installManager.getMissingFreeSpaceFor(installInfo))

    m Then "the created task is of install type"
    assertEquals(listOf(-286L, -36, 214, 214, -36, -36, -286), results.toList())
  }

  companion object {

    private val speeds = Speed.values().toList().run {
      val combinationsCount = size * size * size
      List(combinationsCount) {
        val taskInfoSpeed = get((it / (size * size)) % size)
        val downloaderSpeed = get((it / size) % size)
        val installerSpeed = get(it % size)
        Triple(taskInfoSpeed, downloaderSpeed, installerSpeed)
      }
    }

    private val networkChanges = NetworkConnection.State.values()
      .map { firstState ->
        NetworkConnection.State.values().map { secondState ->
          firstState to secondState
        }
      }
      .flatten()
      .distinct()

    @JvmStatic
    fun variousPackageAppInfoProvider(): Stream<Arguments> = Stream.of(
      Arguments.arguments("Not installed package", notInstalledPackage),
      Arguments.arguments("Outdated package installed", outdatedPackage),
      Arguments.arguments("Current package installed", currentPackage),
      Arguments.arguments("Newer package installed", newerPackage),
    )

    @JvmStatic
    fun speedCombinationsProvider(): Stream<Arguments> = speeds
      .map { (taskInfoSpeed, downloaderSpeed, installerSpeed) ->
        Arguments.arguments(
          "ti: $taskInfoSpeed - pd: $downloaderSpeed - pi: $installerSpeed",
          taskInfoSpeed,
          downloaderSpeed,
          installerSpeed
        )
      }
      .stream()

    @JvmStatic
    fun speedsAndNetworkChangesProvider(): Stream<Arguments> = speeds
      .map { (taskInfoSpeed, downloaderSpeed, installerSpeed) ->
        networkChanges.map { networkState ->
          Arguments.arguments(
            "ti: $taskInfoSpeed - pd: $downloaderSpeed - pi: $installerSpeed" +
              " || " +
              "nis: ${networkState.first} -> ns: ${networkState.second}",
            networkState.first,
            networkState.second,
            taskInfoSpeed,
            downloaderSpeed,
            installerSpeed,
          )
        }
      }
      .flatten()
      .stream()
  }
}
