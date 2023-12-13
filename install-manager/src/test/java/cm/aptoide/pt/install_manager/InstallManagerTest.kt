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
  fun `Cache the app for the same package`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)

    m When "get app for a given package name"
    val app = installManager.getApp(packageName)
    m And "get app for a given package name again"
    val sameApp = installManager.getApp(packageName)

    m Then "both apps are the same"
    assertSame(app, sameApp)
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
      installInfo, Constraints(
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
      installInfo, Constraints(
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
      installInfo, Constraints(
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
