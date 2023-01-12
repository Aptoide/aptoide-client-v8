package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.*
import cm.aptoide.pt.util.gherkin.coScenario
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
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
  fun `Create new apps`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the given info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
    }.build()

    m When "create app for the provided package name"
    val app = installManager.getApp(packageName)

    m Then "app have expected props"
    assertEquals(packageName, app.packageName)
    assertEquals(
      info.values.firstOrNull(),
      app.packageInfo.first()
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Cache the app for the same package`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
    }.build()

    m When "get or create app for the provided package name"
    val app = installManager.getApp(packageName)
    m And "get app for the provided package name again"
    val app2 = installManager.getApp(packageName)

    m Then "both apps are the same"
    assertSame(app, app2)
  }

  @Test
  fun `Return installed apps`() = coScenario { scope ->
    m Given "map of saved package info data"
    val infoMap = List(2) { "package${it}" }.associateWith { installedInfo(it) }
    m And "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(infoMap)
    m And "install manager builder with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
    }.build()

    m When "get all known apps"
    val apps = installManager.getInstalledApps()

    m Then "result has the same saved apps in order"
    assertEquals(2, apps.size)
    assertEquals(
      infoMap.values.toList(),
      apps.map { it.packageInfo.first() }
    )
  }

  @Test
  fun `Error returning installed apps if get all info fails`() = coScenario { scope ->
    m Given "package info repository mock with the provided info that crashes on get"
    val packageInfoRepository = PackageInfoRepositoryMock(letItCrash = true)
    m And "install manager builder with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
    }.build()

    m When "get all known apps"
    val thrown = assertThrows<RuntimeException> {
      installManager.getInstalledApps()
    }

    m Then "expected exception is thrown"
    assertEquals("Problem!", thrown.message)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return no working apps if idle`(
    comment: String,
    infoSpeed: Speed,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "install manager builder with mocks with provided speeds"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = PackageInfoRepositoryMock(speed = infoSpeed)
      this.taskInfoRepository = TaskInfoRepositoryMock(speed = taskInfoSpeed)
      this.packageDownloader = PackageDownloaderMock(speed = downloaderSpeed)
      this.packageInstaller = PackageInstallerMock(speed = installerSpeed)
    }.build()

    m When "collect apps with running tasks"
    val result = mutableListOf<App?>()
    withTimeoutOrNull(4.toLong().hours) {
      installManager.getWorkingAppInstallers().collect { result.add(it) }
    }

    m Then "apps emitted in order"
    assertEquals(listOf(null), result)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return working apps in right order`(
    comment: String,
    infoSpeed: Speed,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "map of saved package info data"
    val infoMap = List(2) { "package${it + 2}" }.associateWith { installedInfo(it) }
    m And "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(infoMap, speed = infoSpeed)
    m And "install manager builder with mocks with provided speeds"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.taskInfoRepository = TaskInfoRepositoryMock(speed = taskInfoSpeed)
      this.packageDownloader = PackageDownloaderMock(speed = downloaderSpeed)
      this.packageInstaller = PackageInstallerMock(speed = installerSpeed)
    }.build()
    m And "unknown app created"
    val app0 = installManager.getApp("package0")
    m And "uninstalled app got"
    val app1 = installManager.getApp("package1")
    m And "installed app got"
    val app2 = installManager.getApp("package2")
    m And "one more installed app got"
    val app3 = installManager.getApp("package3")
    m And "collecting working apps started"
    val result = mutableListOf<App?>()
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
    app2.install(installInfo)
    m And "one more installed app uninstall started"
    app3.uninstall()

    m When "wait for all tasks to finish"
    scope.advanceUntilIdle()

    m Then "apps emitted in order"
    assertEquals(listOf(null, app0, app1, app2, app3, null), result)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return app installers for restored tasks in right order`(
    comment: String,
    infoSpeed: Speed,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "map of saved package info data"
    val infoMap = List(2) { "package${it + 2}" }.associateWith { installedInfo(it) }
    m And "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(infoMap, speed = infoSpeed)
    m And "set of task info data"
    val taskInfoSet = setOf(
      TaskInfo(
        packageName = "package0",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 0
      ),
      TaskInfo(
        packageName = "package1",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 1
      ),
      TaskInfo(
        packageName = "package2",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 2
      ),
      TaskInfo(
        packageName = "package3",
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
        packageName = "package1",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 5
      ),
      TaskInfo(
        packageName = "package2",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 6
      ),
    )
    m And "task info repository mock with that list as map provided speed"
    val taskInfoRepository = TaskInfoRepositoryMock(taskInfoSet, speed = infoSpeed)
    m And "install manager builder with mocks with provided speeds"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.taskInfoRepository = taskInfoRepository
      this.packageDownloader = PackageDownloaderMock(speed = downloaderSpeed)
      this.packageInstaller = PackageInstallerMock(speed = installerSpeed)
    }.build()
    m And "collecting working apps started"
    val result = mutableListOf<App?>()
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
    val app1 = installManager.getApp("package1")
    m And "installed app got"
    val app2 = installManager.getApp("package2")
    m And "one more installed app got"
    val app3 = installManager.getApp("package3")

    m When "wait for tasks to finish"
    scope.advanceUntilIdle()

    m Then "apps emitted in order"
    assertEquals(listOf(null, app0, app1, app2, app3, null), result)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return app installers for restored and working apps in right order`(
    comment: String,
    infoSpeed: Speed,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "map of saved package info data"
    val infoMap = List(2) { "package${it + 2}" }.associateWith { installedInfo(it) }
    m And "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(infoMap, speed = infoSpeed)
    m And "set of task info data"
    val taskInfoSet = setOf(
      TaskInfo(
        packageName = "package0",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 0
      ),
      TaskInfo(
        packageName = "package1",
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
        packageName = "package1",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 3
      ),
    )
    m And "task info repository mock with that list as map provided speed"
    val taskInfoRepository = TaskInfoRepositoryMock(taskInfoSet, speed = infoSpeed)
    m And "install manager builder with mocks with provided speeds"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.taskInfoRepository = taskInfoRepository
      this.packageDownloader = PackageDownloaderMock(speed = downloaderSpeed)
      this.packageInstaller = PackageInstallerMock(speed = installerSpeed)
    }.build()
    m And "collect apps with running tasks"
    val result = mutableListOf<App?>()
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
    val app1 = installManager.getApp("package1")
    m And "installed app got"
    val app2 = installManager.getApp("package2")
    m And "one more installed app got"
    val app3 = installManager.getApp("package3")
    m And "10 seconds passed"
    delay(10.toLong().seconds)
    m And "installed app update started"
    app2.install(installInfo)
    m And "one more installed app uninstall started"
    app3.uninstall()

    m When "wait for tasks to finish"
    scope.advanceUntilIdle()

    m Then "app have expected props"
    assertEquals(listOf(null, app0, app1, app2, app3, null), result)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return app installers for restored and then working apps in right order`(
    comment: String,
    infoSpeed: Speed,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "map of saved package info data"
    val infoMap = List(2) { "package${it + 2}" }.associateWith { installedInfo(it) }
    m And "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(infoMap, speed = infoSpeed)
    m And "set of task info data"
    val taskInfoSet = setOf(
      TaskInfo(
        packageName = "package0",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 0
      ),
      TaskInfo(
        packageName = "package1",
        installPackageInfo = installInfo,
        type = Task.Type.INSTALL,
        timestamp = 1
      ),
    )
    m And "task info repository mock with that list as map provided speed"
    val taskInfoRepository = TaskInfoRepositoryMock(taskInfoSet, speed = infoSpeed)
    m And "install manager builder with mocks with provided speeds"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.taskInfoRepository = taskInfoRepository
      this.packageDownloader = PackageDownloaderMock(speed = downloaderSpeed)
      this.packageInstaller = PackageInstallerMock(speed = installerSpeed)
    }.build()
    m And "collect apps with running tasks"
    val result = mutableListOf<App?>()
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
    val app1 = installManager.getApp("package1")
    m And "installed app got"
    val app2 = installManager.getApp("package2")
    m And "one more installed app got"
    val app3 = installManager.getApp("package3")
    m And "45 minutes passed"
    delay(45.toLong().minutes)
    m And "installed app update started"
    app2.install(installInfo)
    m And "one more installed app uninstall started"
    app3.uninstall()

    m When "wait for tasks to finish"
    scope.advanceUntilIdle()

    m Then "app have expected props"
    assertEquals(listOf(null, app0, app1, null, app2, app3, null), result)
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

  @ParameterizedTest(name = "{0}")
  @MethodSource("speedCombinationsProvider")
  fun `Return changing apps in right order`(
    comment: String,
    infoSpeed: Speed,
    taskInfoSpeed: Speed,
    downloaderSpeed: Speed,
    installerSpeed: Speed,
  ) = coScenario { scope ->
    m Given "map of saved package info data"
    val infoMap = List(2) { "package${it + 2}" }.associateWith { installedInfo(it) }
    m And "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(infoMap, speed = infoSpeed)
    m And "install manager builder with mocks with provided speeds"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.taskInfoRepository = TaskInfoRepositoryMock(speed = taskInfoSpeed)
      this.packageDownloader = PackageDownloaderMock(speed = downloaderSpeed)
      this.packageInstaller = PackageInstallerMock(speed = installerSpeed)
    }.build()
    m And "collecting changed apps started"
    val result = mutableListOf<Pair<String, Int?>>()
    scope.launch {
      withTimeoutOrNull(2.toLong().hours) {
        installManager.getAppsChanges()
          .collect { result.add(it.packageName to it.packageInfo.first()?.hashCode()) }
      }
    }

    m When "unknown app installed"
    val package0 = "package0"
    val info0 = installedInfo(package0)
    packageInfoRepository.update(package0, info0)
    m And "installed app uninstalled"
    val package2 = "package2"
    packageInfoRepository.update(package2, null)
    m And "another unknown app installed"
    val package1 = "package1"
    val info1 = installedInfo(package1)
    packageInfoRepository.update(package1, info1)
    m And "another installed app uninstalled"
    val package3 = "package3"
    packageInfoRepository.update(package3, null)
    m And "uninstalled app installed back"
    val info4 = installedInfo(package2)
    packageInfoRepository.update(package2, info4)
    m And "another uninstalled app installed back"
    val info5 = installedInfo(package3)
    packageInfoRepository.update(package3, info5)
    m And "wait for tasks to finish"
    scope.advanceUntilIdle()

    m Then "apps emitted in order"
    assertEquals(
      listOf(
        package0 to info0.hashCode(),
        package2 to null,
        package1 to info1.hashCode(),
        package3 to null,
        package2 to info4.hashCode(),
        package3 to info5.hashCode()
      ),
      result
    )
  }

  companion object {
    @JvmStatic
    fun variousPackageAppInfoProvider(): Stream<Arguments> = savedPackageAppInfo()

    @JvmStatic
    fun speedCombinationsProvider(): List<Arguments> = List(81) {
      val infoSpeed = speeds[(it / 27) % 3]
      val taskInfoSpeed = speeds[(it / 9) % 3]
      val downloaderSpeed = speeds[(it / 3) % 3]
      val installerSpeed = speeds[it % 3]
      Arguments.arguments(
        "ai: $infoSpeed - ti: $taskInfoSpeed - pd: $downloaderSpeed - pi: $installerSpeed",
        infoSpeed,
        taskInfoSpeed,
        downloaderSpeed,
        installerSpeed,
      )
    }

    private val speeds = listOf(Speed.SLOW, Speed.NORMAL, Speed.FAST)
  }
}