package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.*
import cm.aptoide.pt.util.gherkin.coScenario
import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * AS a Developer,
 * I WANT to have an easy to use install manager SDK
 * WITH interchangeable storage, download and install implementations,
 * FOR managing the apps
 */
@ExperimentalCoroutinesApi
internal class InstallManagerTest {

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Create new known and unknown apps`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock with the provided app info"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
    }.build()

    m When "create app for the provided package name"
    val app = installManager.getApp(packageName)

    m Then "app have expected props"
    assertEquals(packageName, app.packageName)
    assertEquals(
      appInfo.values.firstOrNull()?.installedVersion?.versionName,
      app.installedVersionName
    )
    assertEquals(
      appInfo.values.firstOrNull()?.installedVersion?.versionCode,
      app.installedVersionCode
    )
    assertEquals(appInfo.values.firstOrNull()?.details, app.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Cache the app for the same package`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock with the provided app info"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
    }.build()

    m When "get or create app for the provided package name"
    val app = installManager.getApp(packageName)
    m And "get app for the provided package name again"
    val app2 = installManager.getApp(packageName)

    m Then "both apps are the same"
    assertSame(app, app2)
    m And "Data saved in the app info repo unchanged"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Error creating apps if get app info fails`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock with the provided app info that crashes on get"
    val appInfoRepository = AppInfoRepositoryMock(appInfo, letItCrash = true)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
    }.build()

    m When "create or get app for the provided package name"
    val thrown = assertThrows<RuntimeException> {
      installManager.getApp(packageName)
    }

    m Then "expected exception is thrown"
    assertEquals("Problem!", thrown.message)
  }

  @Test
  fun `Return known apps`() = coScenario { scope ->
    m Given "list of saved app info data"
    val appInfoList = listOf(
      uninstalledWithoutDetails,
      uninstalledWithDetails,
      installedWithoutDetails,
      installedWithDetails
    )
    m And "task info repository mock with that list as map"
    val appInfoRepository = AppInfoRepositoryMock(appInfoList.associateBy { it.packageName })
    m And "install manager builder with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
    }.build()

    m When "get all known apps"
    val apps = installManager.getKnownApps()

    m Then "result has the same saved apps in order"
    assertEquals(4, apps.size)
    for (i in appInfoList.indices) {
      assertEquals(appInfoList[i].packageName, apps[i].packageName)
      assertEquals(
        appInfoList[i].installedVersion?.versionName,
        apps[i].installedVersionName
      )
      assertEquals(
        appInfoList[i].installedVersion?.versionCode,
        apps[i].installedVersionCode
      )
      assertEquals(appInfoList[i].details, apps[i].details)
    }
  }

  @Test
  fun `Error returning known apps if get all fails`() = coScenario { scope ->
    m Given "task info repository mock that throws an error on get all"
    val appInfoRepository = AppInfoRepositoryMock(letItCrash = true)
    m And "install manager builder with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
    }.build()

    m When "get all known apps"
    val thrown = assertThrows<RuntimeException> {
      installManager.getKnownApps()
    }

    m Then "expected exception is thrown"
    assertEquals("Problem!", thrown.message)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return no working apps if idle`(
    comment: String,
    appInfoSpeed: Speed,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager builder with mocks with provided speeds"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = AppInfoRepositoryMock(speed = appInfoSpeed)
      this.taskInfoRepository = TaskInfoRepositoryMock(speed = taskInfoSpeed)
      this.packageDownloader = PackageDownloaderMock(speed = downloaderSpeed)
      this.packageInstaller = PackageInstallerMock(speed = installerSpeed)
    }.build()

    m When "collect apps with running tasks"
    val result = mutableListOf<App<String>?>()
    withTimeoutOrNull(4.toLong().hours) {
      installManager.getWorkingAppInstallers().collect { result.add(it) }
    }

    m Then "apps emitted in order"
    assertEquals(listOf<App<String>?>(null), result)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return working apps in right order`(
    comment: String,
    appInfoSpeed: Speed,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "map of app info data"
    val appInfoMap = listOf(
      uninstalledWithoutDetails,
      uninstalledWithDetails,
      installedWithoutDetails,
      installedWithDetails
    ).associateBy { it.packageName }
    m And "app info repository mock with that list as map provided speed"
    val appInfoRepository = AppInfoRepositoryMock(appInfoMap, speed = appInfoSpeed)
    m And "install manager builder with mocks with provided speeds"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.taskInfoRepository = TaskInfoRepositoryMock(speed = taskInfoSpeed)
      this.packageDownloader = PackageDownloaderMock(speed = downloaderSpeed)
      this.packageInstaller = PackageInstallerMock(speed = installerSpeed)
    }.build()
    m And "unknown app created"
    val app0 = installManager.getApp("package0")
    m And "uninstalled app got"
    val app1 = installManager.getApp(uninstalledWithoutDetails.packageName)
    m And "installed app got"
    val app3 = installManager.getApp(installedWithoutDetails.packageName)
    m And "one more installed app got"
    val app4 = installManager.getApp(installedWithDetails.packageName)
    m And "collecting working apps started"
    val result = mutableListOf<App<String>?>()
    scope.launch {
      withTimeoutOrNull(2.toLong().hours) {
        installManager.getWorkingAppInstallers().collect { result.add(it) }
      }
    }
    m And "unknown app install started"
    app0.install(installInfo)
    m And "uninstalled app install started"
    app1.install(installInfo)
    m And "installed app update started"
    app3.install(installInfo)
    m And "one more installed app uninstall started"
    app4.uninstall()

    m When "wait for all tasks to finish"
    scope.advanceUntilIdle()

    m Then "apps emitted in order"
    assertEquals(listOf(null, app0, app1, app3, app4, null), result)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return app installers for restored tasks in right order`(
    comment: String,
    appInfoSpeed: Speed,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "map of app info data"
    val appInfoMap = listOf(
      uninstalledWithoutDetails,
      uninstalledWithDetails,
      installedWithoutDetails,
      installedWithDetails
    ).associateBy { it.packageName }
    m And "app info repository mock with that list as map provided speed"
    val appInfoRepository = AppInfoRepositoryMock(appInfoMap, speed = appInfoSpeed)
    m And "set of task info data"
    val taskInfoSet = setOf(
      TaskInfo(
        packageName = "package0",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 0
      ),
      TaskInfo(
        packageName = uninstalledWithoutDetails.packageName,
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 1
      ),
      TaskInfo(
        packageName = installedWithoutDetails.packageName,
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 2
      ),
      TaskInfo(
        packageName = installedWithDetails.packageName,
        installPackageInfo = uninstallInfo,
        type = Task.Type.UNINSTALL,
        timestamp = 3
      ),
      TaskInfo(
        packageName = "package0",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 4
      ),
      TaskInfo(
        packageName = uninstalledWithoutDetails.packageName,
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 5
      ),
      TaskInfo(
        packageName = installedWithoutDetails.packageName,
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 6
      ),
    )
    m And "task info repository mock with that list as map provided speed"
    val taskInfoRepository = TaskInfoRepositoryMock(taskInfoSet, speed = appInfoSpeed)
    m And "install manager builder with mocks with provided speeds"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.taskInfoRepository = taskInfoRepository
      this.packageDownloader = PackageDownloaderMock(speed = downloaderSpeed)
      this.packageInstaller = PackageInstallerMock(speed = installerSpeed)
    }.build()
    m And "collecting working apps started"
    val result = mutableListOf<App<String>?>()
    scope.launch {
      withTimeoutOrNull(2.toLong().hours) {
        installManager.getWorkingAppInstallers().collect { result.add(it) }
      }
    }
    m And "restore tasks"
    installManager.restore()
    m And "unknown app got"
    val app0 = installManager.getApp("package0")
    m And "uninstalled app got"
    val app1 = installManager.getApp(uninstalledWithoutDetails.packageName)
    m And "installed app got"
    val app3 = installManager.getApp(installedWithoutDetails.packageName)
    m And "one more installed app got"
    val app4 = installManager.getApp(installedWithDetails.packageName)

    m When "wait for tasks to finish"
    scope.advanceUntilIdle()

    m Then "apps emitted in order"
    assertEquals(listOf(null, app0, app1, app3, app4, null), result)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return app installers for restored and working apps in right order`(
    comment: String,
    appInfoSpeed: Speed,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "map of app info data"
    val appInfoMap = listOf(
      uninstalledWithoutDetails,
      uninstalledWithDetails,
      installedWithoutDetails,
      installedWithDetails
    ).associateBy { it.packageName }
    m And "app info repository mock with that list as map provided speed"
    val appInfoRepository = AppInfoRepositoryMock(appInfoMap, speed = appInfoSpeed)
    m And "set of task info data"
    val taskInfoSet = setOf(
      TaskInfo(
        packageName = "package0",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 0
      ),
      TaskInfo(
        packageName = uninstalledWithoutDetails.packageName,
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 1
      ),
      TaskInfo(
        packageName = "package0",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 2
      ),
      TaskInfo(
        packageName = uninstalledWithoutDetails.packageName,
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 3
      ),
    )
    m And "task info repository mock with that list as map provided speed"
    val taskInfoRepository = TaskInfoRepositoryMock(taskInfoSet, speed = appInfoSpeed)
    m And "install manager builder with mocks with provided speeds"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.taskInfoRepository = taskInfoRepository
      this.packageDownloader = PackageDownloaderMock(speed = downloaderSpeed)
      this.packageInstaller = PackageInstallerMock(speed = installerSpeed)
    }.build()
    m And "collect apps with running tasks"
    val result = mutableListOf<App<String>?>()
    scope.launch {
      withTimeoutOrNull(2.toLong().hours) {
        installManager.getWorkingAppInstallers().collect { result.add(it) }
      }
    }
    m And "restore tasks"
    installManager.restore()
    m And "unknown app got"
    val app0 = installManager.getApp("package0")
    m And "uninstalled app got"
    val app1 = installManager.getApp(uninstalledWithoutDetails.packageName)
    m And "installed app got"
    val app3 = installManager.getApp(installedWithoutDetails.packageName)
    m And "one more installed app got"
    val app4 = installManager.getApp(installedWithDetails.packageName)
    m And "10 seconds passed"
    delay(10.toLong().seconds)
    m And "installed app update started"
    app3.install(installInfo)
    m And "one more installed app uninstall started"
    app4.uninstall()

    m When "wait for tasks to finish"
    scope.advanceUntilIdle()

    m Then "app have expected props"
    assertEquals(listOf(null, app0, app1, app3, app4, null), result)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return app installers for restored and then working apps in right order`(
    comment: String,
    appInfoSpeed: Speed,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "map of app info data"
    val appInfoMap = listOf(
      uninstalledWithoutDetails,
      uninstalledWithDetails,
      installedWithoutDetails,
      installedWithDetails
    ).associateBy { it.packageName }
    m And "app info repository mock with that list as map provided speed"
    val appInfoRepository = AppInfoRepositoryMock(appInfoMap, speed = appInfoSpeed)
    m And "set of task info data"
    val taskInfoSet = setOf(
      TaskInfo(
        packageName = "package0",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 0
      ),
      TaskInfo(
        packageName = uninstalledWithoutDetails.packageName,
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 1
      ),
    )
    m And "task info repository mock with that list as map provided speed"
    val taskInfoRepository = TaskInfoRepositoryMock(taskInfoSet, speed = appInfoSpeed)
    m And "install manager builder with mocks with provided speeds"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.taskInfoRepository = taskInfoRepository
      this.packageDownloader = PackageDownloaderMock(speed = downloaderSpeed)
      this.packageInstaller = PackageInstallerMock(speed = installerSpeed)
    }.build()
    m And "collect apps with running tasks"
    val result = mutableListOf<App<String>?>()
    scope.launch {
      withTimeoutOrNull(2.toLong().hours) {
        installManager.getWorkingAppInstallers().collect { result.add(it) }
      }
    }
    m And "restore tasks"
    installManager.restore()
    m And "unknown app got"
    val app0 = installManager.getApp("package0")
    m And "uninstalled app got"
    val app1 = installManager.getApp(uninstalledWithoutDetails.packageName)
    m And "installed app got"
    val app3 = installManager.getApp(installedWithoutDetails.packageName)
    m And "one more installed app got"
    val app4 = installManager.getApp(installedWithDetails.packageName)
    m And "45 minutes passed"
    delay(45.toLong().minutes)
    m And "installed app update started"
    app3.install(installInfo)
    m And "one more installed app uninstall started"
    app4.uninstall()

    m When "wait for tasks to finish"
    scope.advanceUntilIdle()

    m Then "app have expected props"
    assertEquals(listOf(null, app0, app1, null, app3, app4, null), result)
  }

  @Test
  fun `Error restoring install manager if get all tasks fails`() = coScenario { scope ->
    m Given "task info repository mock that throws an error on get all"
    val taskInfoRepository = TaskInfoRepositoryMock(letItCrash = true)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.taskInfoRepository = taskInfoRepository
    }.build()

    m When "restore install manager"
    val thrown = assertThrows<RuntimeException> { installManager.restore() }

    m Then "expected exception is thrown"
    assertEquals("Problem!", thrown.message)
  }

  companion object {
    @JvmStatic
    fun variousPackageAppInfoProvider(): Stream<Arguments> = savedPackageAppInfo()

    @JvmStatic
    fun speedCombinationsProvider(): List<Arguments> = List(81) {
      val appInfoSpeed = speeds[(it / 27) % 3]
      val taskInfoSpeed = speeds[(it / 9) % 3]
      val downloaderSpeed = speeds[(it / 3) % 3]
      val installerSpeed = speeds[it % 3]
      Arguments.arguments(
        "ai: $appInfoSpeed - ti: $taskInfoSpeed - pd: $downloaderSpeed - pi: $installerSpeed",
        appInfoSpeed, taskInfoSpeed, downloaderSpeed, installerSpeed,
      )
    }

    private val speeds = listOf(Speed.SLOW, Speed.NORMAL, Speed.FAST)
  }
}