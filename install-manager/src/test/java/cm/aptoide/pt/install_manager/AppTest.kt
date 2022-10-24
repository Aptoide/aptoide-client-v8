package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.*
import cm.aptoide.pt.util.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 * AS a Developer,
 * I WANT to have an easy to use app installer
 * WITH interchangeable storages, download and install implementations,
 * FOR managing an app info and install/uninstall tasks
 */
@ExperimentalCoroutinesApi
internal class AppTest {

  @ParameterizedTest(name = "{0}")
  @MethodSource("unknownPackageAppInfoProvider")
  fun `Don't update the unknown app details on get`(
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

    m When "create an app for the provided package name with new details"
    installManager.getApp(packageName, "new details")

    m Then "data is not saved in the repo"
    assertTrue(appInfoRepository.info.isEmpty())
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("unknownPackageAppInfoProvider")
  fun `Don't update the unknown app details on set`(
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
    m And "app for the provided package name created"
    val app = installManager.getApp(packageName)

    m When "set new details to the app"
    app.setDetails("new details")

    m Then "data is not saved in the repo"
    assertTrue(appInfoRepository.info.isEmpty())
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("knownPackageAppInfoProvider")
  fun `Update the app details on get`(
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

    m When "get app for the provided package name with new details"
    installManager.getApp(packageName, "new details")

    m Then "new details saved in the repo"
    assertEquals(
      appInfo.mapValues {
        AppInfo(it.value.packageName, it.value.installedVersion, "new details")
      },
      appInfoRepository.info
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("knownPackageAppInfoProvider")
  fun `Update the app details on set`(
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
    m And "app for the provided package name got"
    val app = installManager.getApp(packageName)

    m When "set new details to the app"
    app.setDetails("new details")

    m Then "new details saved in the repo"
    assertEquals(
      appInfo.mapValues {
        AppInfo(it.value.packageName, it.value.installedVersion, "new details")
      },
      appInfoRepository.info
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Cache the task for the same app`(
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
    m And "app for the provided package name got"
    val app = installManager.getApp(packageName)

    m When "create install task"
    val task = app.install(installInfo)
    m And "get the task for the app again"
    val task2 = app.getTask()
    m And "get the task for the app again 4 seconds later"
    scope.advanceTimeBy(4_000)
    val task3 = app.getTask()

    m Then "all tasks are the same"
    assertSame(task, task2)
    assertSame(task2, task3)
    m And "app info repo unchanged"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("uninstalledPackageAppInfoProvider")
  fun `Error calling uninstall for not installed apps`(
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

    m When "create uninstall task for provided package name"
    val uninstallThrown = assertThrows<IllegalStateException> {
      installManager.getApp(packageName).uninstall()
    }

    m Then "expected exception is thrown"
    assertEquals("$packageName not installed", uninstallThrown.message)
    m And "nothing changed in the repo"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Error calling install during installation`(
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
    m And "app for the provided package name got or created"
    val app = installManager.getApp(packageName)
    m And "installation task started"
    app.install(installInfo)

    m When "create installation task again"
    val installThrown = assertThrows<IllegalStateException> {
      app.install(installInfo)
    }

    m Then "expected exception is thrown"
    assertEquals("another task is already queued", installThrown.message)
    m And "nothing changed in the repo"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installedPackageAppInfoProvider")
  fun `Error calling install during uninstallation`(
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
    m And "app for the provided package name got or created"
    val app = installManager.getApp(packageName)
    m And "uninstallation task started"
    app.uninstall()

    m When "create installation task"
    val installThrown = assertThrows<IllegalStateException> {
      app.install(installInfo)
    }

    m Then "expected exception is thrown"
    assertEquals("another task is already queued", installThrown.message)
    m And "nothing changed in the repo"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Error calling uninstall during installation`(
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
    m And "app for the provided package name got or created"
    val app = installManager.getApp(packageName)
    m And "installation task started"
    app.install(installInfo)

    m When "create uninstallation task"
    val uninstallThrown = assertThrows<IllegalStateException> {
      app.uninstall()
    }

    m Then "expected exception is thrown"
    assertEquals("another task is already queued", uninstallThrown.message)
    m And "nothing changed in the repo"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installedPackageAppInfoProvider")
  fun `Error calling uninstall during uninstallation`(
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
    m And "app for the provided package name got or created"
    val app = installManager.getApp(packageName)
    m And "uninstallation task started"
    app.uninstall()

    m When "create uninstallation task again"
    val uninstallThrown = assertThrows<IllegalStateException> {
      app.uninstall()
    }

    m Then "expected exception is thrown"
    assertEquals("another task is already queued", uninstallThrown.message)
    m And "nothing changed in the repo"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Save or update app details if install successful`(
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
    m And "installation task for the provided package name started"
    installManager.getApp(packageName).install(installInfo)

    m When "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app info saved or updated in the repo"
    assertEquals(
      mapOf(
        packageName to AppInfo(
          packageName = packageName,
          installedVersion = installInfo.version,
          details = appInfo.values.firstOrNull()?.details
        )
      ),
      appInfoRepository.info
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installedPackageAppInfoProvider")
  fun `Remove app version if uninstall successful`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock without known apps"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "package installer mock with installed provided package name"
    val packageInstaller = PackageInstallerMock(setOf(packageName))
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "uninstallation task for the provided package name started"
    installManager.getApp(packageName).uninstall()

    m When "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app version info removed from the repo"
    assertEquals(
      mapOf(
        packageName to AppInfo(
          packageName = packageName,
          installedVersion = null,
          details = appInfo.values.first().details
        )
      ),
      appInfoRepository.info
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Don't save new app details if download fails`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock with the provided app info"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "package downloader mock that throws an error on download"
    val packageDownloader = PackageDownloaderMock(letItCrash = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.packageDownloader = packageDownloader
    }.build()
    m And "installation task for the provided package name started"
    installManager.getApp(packageName).install(installInfo)

    m When "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app info repo unchanged"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Don't save new app details if install fails`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock with the provided app info"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "package installer mock that throws an error on install"
    val packageInstaller = PackageInstallerMock(letItCrash = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "installation task for the provided package name started"
    installManager.getApp(packageName).install(installInfo)

    m When "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app info repo unchanged"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installedPackageAppInfoProvider")
  fun `Don't change app details if uninstall failed`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock without known apps"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "package installer mock with installed provided package name and throws an error on uninstall"
    val packageInstaller = PackageInstallerMock(setOf(packageName), letItCrash = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "uninstallation task for the provided package name started"
    installManager.getApp(packageName).uninstall()

    m When "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app info repo unchanged"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Don't save new app details if download cancelled`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock with the provided app info"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "package downloader mock that waits for cancellation"
    val packageDownloader = PackageDownloaderMock(waitForCancel = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.packageDownloader = packageDownloader
    }.build()
    m And "installation task for the provided package name created"
    val task = installManager.getApp(packageName).install(installInfo)
    m And "download progressed to some point"
    scope.advanceUntilIdle()

    m When "cancel the task"
    task.cancel()
    m And "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app info repo unchanged"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Don't save new app details if install cancelled`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock with the provided app info"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "package installer mock that waits for cancellation"
    val packageInstaller = PackageInstallerMock(waitForCancel = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "installation task for the provided package name created"
    val task = installManager.getApp(packageName).install(installInfo)
    m And "install progressed to some point"
    scope.advanceUntilIdle()

    m When "cancel the task"
    task.cancel()
    m And "wait for all coroutines to finish"
    scope.advanceUntilIdle()


    m Then "app info repo unchanged"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installedPackageAppInfoProvider")
  fun `Don't change app details if uninstall cancelled`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock without known apps"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "package installer mock installed provided package name that waits for cancellation"
    val packageInstaller = PackageInstallerMock(setOf(packageName), waitForCancel = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "uninstallation task for the provided package name created"
    val task = installManager.getApp(packageName).uninstall()
    m And "uninstall progressed to some point"
    scope.advanceUntilIdle()

    m When "cancel the task"
    task.cancel()
    m And "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app info repo unchanged"
    assertEquals(appInfo, appInfoRepository.info)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("knownPackageAppInfoProvider")
  fun `Remove app details from repo on remove`(
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
    m And "app for the provided package name got"
    val app = installManager.getApp(packageName)

    m When "remove the app"
    app.remove()

    m Then "app info removed from the repo"
    assertTrue(appInfoRepository.info.isEmpty())
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("knownPackageAppInfoProvider")
  fun `Remove app details from repo and cancel download task on remove`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock with the provided app info"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "package downloader mock that waits for cancellation"
    val packageDownloader = PackageDownloaderMock(waitForCancel = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.packageDownloader = packageDownloader
    }.build()
    m And "app for the provided package name got"
    val app = installManager.getApp(packageName)
    m And "installation task created"
    app.install(installInfo)
    m And "download progressed to some point"
    scope.advanceUntilIdle()

    m When "remove the app"
    app.remove()
    m And "wait for repos coroutines to finish"
    scope.advanceTimeBy(2_000)

    m Then "app has no task"
    assertNull(app.getTask())
    m And "app info removed from the repo"
    assertTrue(appInfoRepository.info.isEmpty())
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("knownPackageAppInfoProvider")
  fun `Remove app details from repo and cancel installation task on remove`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock with the provided app info"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "package installer mock that waits for cancellation"
    val packageInstaller = PackageInstallerMock(waitForCancel = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "app for the provided package name got"
    val app = installManager.getApp(packageName)
    m And "installation task created"
    app.install(installInfo)
    m And "download progressed to some point"
    scope.advanceUntilIdle()

    m When "remove the app"
    app.remove()
    m And "wait for repos coroutines to finish"
    scope.advanceTimeBy(2_000)

    m Then "app has no task"
    assertNull(app.getTask())
    m And "app info removed from the repo"
    assertTrue(appInfoRepository.info.isEmpty())
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installedPackageAppInfoProvider")
  fun `Remove app details from repo and cancel uninstallation task on remove`(
    comment: String,
    packageName: String,
    appInfo: Map<String, AppInfo<String>>
  ) = coScenario { scope ->
    m Given "app info repository mock with the provided app info"
    val appInfoRepository = AppInfoRepositoryMock(appInfo)
    m And "package installer mock installed provided package name that waits for cancellation"
    val packageInstaller = PackageInstallerMock(setOf(packageName), waitForCancel = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.appInfoRepository = appInfoRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "app for the provided package name got"
    val app = installManager.getApp(packageName)
    m And "uninstallation task created"
    app.uninstall()
    m And "download progressed to some point"
    scope.advanceUntilIdle()

    m When "remove the app"
    app.remove()
    m And "wait for repos coroutines to finish"
    scope.advanceTimeBy(2_000)

    m Then "app has no task"
    assertNull(app.getTask())
    m And "app info removed from the repo"
    assertTrue(appInfoRepository.info.isEmpty())
  }

  companion object {
    @JvmStatic
    fun variousPackageAppInfoProvider(): Stream<Arguments> = savedPackageAppInfo()

    @JvmStatic
    fun unknownPackageAppInfoProvider(): Stream<Arguments> = savedPackageAppInfo().limit(1)

    @JvmStatic
    fun knownPackageAppInfoProvider(): Stream<Arguments> = savedPackageAppInfo().skip(1)

    @JvmStatic
    fun uninstalledPackageAppInfoProvider(): Stream<Arguments> = savedPackageAppInfo().limit(3)

    @JvmStatic
    fun installedPackageAppInfoProvider(): Stream<Arguments> = savedPackageAppInfo().skip(3)
  }
}