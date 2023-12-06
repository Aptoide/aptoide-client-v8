package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.*
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.time.Duration.Companion.minutes
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

    m Then "there are 3 installed apps"
    assertEquals(3, apps.size)
    m And "and their package names are the same as in package info repository"
    assertEquals(
      mocks.packageInfoRepository.info.values.toList(),
      apps.map { it.packageInfo }
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return no apps if idle`(
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
    m And "get all those apps current tasks"
    val notInstalledTask = notInstalled.task
    val outdatedTask = outdated.task
    val currentTask = current.task
    val newerTask = newer.task

    m Then "nothing was collected"
    assertEquals(listOf(null), result)
    m And "all those apps had no tasks"
    assertNull(notInstalledTask)
    assertNull(outdatedTask)
    assertNull(currentTask)
    assertNull(newerTask)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return apps for working tasks`(
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
    m And "not installed app got"
    val notInstalled = installManager.getApp(notInstalledPackage)
    m And "outdated version installed app got"
    val outdated = installManager.getApp(outdatedPackage)
    m And "current version installed app got"
    val current = installManager.getApp(currentPackage)
    m And "newer version installed app got"
    installManager.getApp(newerPackage)
    m And "collecting working apps asynchronously started"
    val result = installManager.workingAppInstallers.collectAsync(scope)

    m When "not installed app install started"
    notInstalled.install(installInfo)
    m And "outdated version app update started"
    outdated.install(installInfo)
    m And "current version app uninstall started"
    current.uninstall()
    m And "get all those apps current tasks"
    val notInstalledTask = notInstalled.task
    val outdatedTask = outdated.task
    val currentTask = current.task
    m And "wait until all tasks finish"
    scope.advanceUntilIdle()

    m Then "working tasks apps collected in the same order"
    assertEquals(listOf(null, notInstalled, outdated, current, null), result)
    m And "all those apps had tasks"
    assertNotNull(notInstalledTask)
    assertNotNull(outdatedTask)
    assertNotNull(currentTask)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return apps for restored tasks`(
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
    m And "collecting working apps asynchronously started"
    val result = installManager.workingAppInstallers.collectAsync(scope)
    m And "outdated version installed app got"
    installManager.getApp(outdatedPackage)
    m And "newer version installed app got"
    val newer = installManager.getApp(newerPackage)
    m And "not installed app got"
    val notInstalled = installManager.getApp(notInstalledPackage)
    m And "current version installed app got"
    val current = installManager.getApp(currentPackage)

    m When "restore saved tasks"
    installManager.restore()
    m And "get all those apps current tasks"
    val newerTask = newer.task
    val notInstalledTask = notInstalled.task
    val currentTask = current.task
    m And "wait until all tasks finish"
    scope.advanceUntilIdle()

    m Then "restored tasks apps collected in the order of timestamp in task info repository"
    assertEquals(listOf(null, newer, notInstalled, current, null), result)
    m And "all those apps had tasks"
    assertNotNull(newerTask)
    assertNotNull(notInstalledTask)
    assertNotNull(currentTask)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return apps for restored and working tasks`(
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
    m And "collecting working apps asynchronously started"
    val result = installManager.workingAppInstallers.collectAsync(scope)
    m And "newer version installed app got"
    val newer = installManager.getApp(newerPackage)
    m And "not installed app got"
    val notInstalled = installManager.getApp(notInstalledPackage)
    m And "current version installed app got"
    val current = installManager.getApp(currentPackage)
    m And "outdated version installed app got"
    val outdated = installManager.getApp(outdatedPackage)

    m When "restore saved tasks"
    installManager.restore()
    m And "get all those apps current tasks"
    val newerTask = newer.task
    val notInstalledTask = notInstalled.task
    val currentTask = current.task
    m And "wait for 10 seconds"
    delay(10.seconds)
    m And "outdated version app update started"
    outdated.install(installInfo)
    m And "get that app current task"
    val outdatedTask = outdated.task
    m And "wait until all tasks finish"
    scope.advanceUntilIdle()

    m Then "restored tasks apps collected first and then the working ones"
    assertEquals(listOf(null, newer, notInstalled, current, outdated, null), result)
    m And "all those apps had tasks"
    assertNotNull(newerTask)
    assertNotNull(notInstalledTask)
    assertNotNull(currentTask)
    assertNotNull(outdatedTask)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return apps for restored and then working tasks`(
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
    m And "collecting working apps asynchronously started"
    val result = installManager.workingAppInstallers.collectAsync(scope)
    m And "newer version installed app got"
    val newer = installManager.getApp(newerPackage)
    m And "not installed app got"
    val notInstalled = installManager.getApp(notInstalledPackage)
    m And "current version installed app got"
    val current = installManager.getApp(currentPackage)
    m And "outdated version installed app got"
    val outdated = installManager.getApp(outdatedPackage)

    m When "restore saved tasks"
    installManager.restore()
    m And "get restored apps current tasks"
    val newerTask = newer.task
    val notInstalledTask = notInstalled.task
    val currentTask = current.task
    m And "wait for 45 minutes"
    delay(45.minutes)
    m And "outdated version app update started"
    outdated.install(installInfo)
    m And "current version app install started"
    current.install(installInfo)
    m And "get those apps current tasks"
    val outdatedTask = outdated.task
    val currentTask2 = current.task
    m And "wait until all tasks finish"
    scope.advanceUntilIdle()

    m Then "restored tasks apps collected first and then after pause the working ones"
    assertEquals(listOf(null, newer, notInstalled, current, null, outdated, current, null), result)
    m And "all those apps had tasks"
    assertNotNull(newerTask)
    assertNotNull(notInstalledTask)
    assertNotNull(currentTask)
    assertNotNull(outdatedTask)
    assertNotNull(currentTask2)
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
    @JvmStatic
    fun variousPackageAppInfoProvider(): Stream<Arguments> = Stream.of(
      Arguments.arguments("Not installed package", notInstalledPackage),
      Arguments.arguments("Outdated package installed", outdatedPackage),
      Arguments.arguments("Current package installed", currentPackage),
      Arguments.arguments("Newer package installed", newerPackage),
    )

    @JvmStatic
    fun speedCombinationsProvider(): List<Arguments> = List(27) {
      val taskInfoSpeed = speeds[(it / 9) % 3]
      val downloaderSpeed = speeds[(it / 3) % 3]
      val installerSpeed = speeds[it % 3]
      Arguments.arguments(
        "ti: $taskInfoSpeed - pd: $downloaderSpeed - pi: $installerSpeed",
        taskInfoSpeed,
        downloaderSpeed,
        installerSpeed,
      )
    }

    private val speeds = Speed.values().toList()
  }
}
