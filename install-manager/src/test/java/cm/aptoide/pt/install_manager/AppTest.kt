package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
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
  @MethodSource("variousPackageAppInfoProvider")
  fun `Update the app details on get`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
    }.build()

    m When "get app for the provided package name with new details"
    val app = installManager.getApp(packageName, "new details")

    m Then "new details saved in the repo"
    assertEquals(app.details, appDetailsRepository.details[packageName])
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Update the app details on set`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
    }.build()
    m And "app for the provided package name got"
    val app = installManager.getApp(packageName)

    m When "set new details to the app"
    app.setDetails("new details")

    m Then "new details saved in the repo"
    assertEquals(app.details, appDetailsRepository.details[packageName])
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Cache the task for the same app`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
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
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("uninstalledPackageAppInfoProvider")
  fun `Error calling uninstall for not installed apps`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
    }.build()

    m When "create uninstall task for provided package name"
    val uninstallThrown = assertThrows<IllegalStateException> {
      installManager.getApp(packageName).uninstall()
    }

    m Then "expected exception is thrown"
    assertEquals("$packageName not installed", uninstallThrown.message)
    m And "nothing changed in the repo"
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Error calling install during installation`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
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
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installedPackageAppInfoProvider")
  fun `Error calling install during uninstallation`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
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
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Error calling uninstall during installation`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
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
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installedPackageAppInfoProvider")
  fun `Error calling uninstall during uninstallation`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
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
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Save or update app package info if install successful`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
    }.build()
    m And "app for the provided package name started"
    val app = installManager.getApp(packageName)
    m And "installation task for the app"
    app.install(installInfo)

    m When "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app info saved or updated in the repo"
    assertEquals(
      packageInfoRepository.info[packageName],
      app.packageInfo
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installedPackageAppInfoProvider")
  fun `Remove app package info if uninstall successful`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
    }.build()
    m And "app for the provided package name started"
    val app = installManager.getApp(packageName)
    m And "uninstallation task for the app"
    app.uninstall()

    m When "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app package info removed"
    assertNull(app.packageInfo)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Don't save new app details if download fails`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "package downloader mock that throws an error on download"
    val packageDownloader = PackageDownloaderMock(letItCrash = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
      this.packageDownloader = packageDownloader
    }.build()
    m And "installation task for the provided package name started"
    installManager.getApp(packageName).install(installInfo)

    m When "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app info repo unchanged"
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Don't save new app details if install fails`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "package installer mock that throws an error on install"
    val packageInstaller = PackageInstallerMock(letItCrash = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "installation task for the provided package name started"
    installManager.getApp(packageName).install(installInfo)

    m When "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app info repo unchanged"
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installedPackageAppInfoProvider")
  fun `Don't change app details if uninstall failed`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "package installer mock with installed provided package name and throws an error on uninstall"
    val packageInstaller = PackageInstallerMock(letItCrash = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
      this.packageInstaller = packageInstaller
    }.build()
    m And "uninstallation task for the provided package name started"
    installManager.getApp(packageName).uninstall()

    m When "wait for all coroutines to finish"
    scope.advanceUntilIdle()

    m Then "app info repo unchanged"
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Don't save new app details if download cancelled`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "package downloader mock that waits for cancellation"
    val packageDownloader = PackageDownloaderMock(waitForCancel = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
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
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Don't save new app details if install cancelled`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "package installer mock that waits for cancellation"
    val packageInstaller = PackageInstallerMock(waitForCancel = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
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
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installedPackageAppInfoProvider")
  fun `Don't change app details if uninstall cancelled`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "package installer mock installed provided package name that waits for cancellation"
    val packageInstaller = PackageInstallerMock(waitForCancel = true)
    m And "install manager initialised with those mocks"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
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
    assertEquals(details, appDetailsRepository.details)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Remove app details from repo on remove`(
    comment: String,
    packageName: String,
    info: Map<String, PackageInfo>,
    details: Map<String, String>,
  ) = coScenario { scope ->
    m Given "package info repository mock with the provided info"
    val packageInfoRepository = PackageInfoRepositoryMock(info)
    m And "app details repository mock with the provided details"
    val appDetailsRepository = AppDetailsRepositoryMock(details)
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
      this.appDetailsRepository = appDetailsRepository
    }.build()
    m And "app for the provided package name got"
    val app = installManager.getApp(packageName)

    m When "remove the app"
    app.removeDetails()

    m Then "app info removed from the repo"
    assertTrue(appDetailsRepository.details.isEmpty())
  }

  companion object {
    @JvmStatic
    fun variousPackageAppInfoProvider(): Stream<Arguments> = savedPackageAppInfo()

    @JvmStatic
    fun uninstalledPackageAppInfoProvider(): Stream<Arguments> = savedPackageAppInfo().limit(2)

    @JvmStatic
    fun installedPackageAppInfoProvider(): Stream<Arguments> = savedPackageAppInfo().skip(2)
  }
}