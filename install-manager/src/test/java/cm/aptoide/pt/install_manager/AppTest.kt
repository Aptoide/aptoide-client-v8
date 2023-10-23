package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.*
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
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
  fun `Cache the tasks or nulls for the same app`(
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
    m And "app for the provided package name got"
    val app = installManager.getApp(packageName)

    m When "get task if nothing is running"
    val task1 = app.tasks.first()
    m And "create install task"
    val task2 = app.install(installInfo)
    m And "get the task for the app"
    val task3 = app.tasks.first()
    m And "get the task for the app again 4 seconds later"
    scope.advanceTimeBy(4_000)
    val task4 = app.tasks.first()
    m And "make the app appear as installed"
    packageInfoRepository.update(packageName, installedInfo(packageName))
    m And "wait until task is finished"
    scope.advanceUntilIdle()
    m And "get task if nothing is running again"
    val task5 = app.tasks.first()
    m And "create uninstall task"
    val task6 = app.uninstall()
    m And "get the task for the app again"
    val task7 = app.tasks.first()
    m And "get the task for the app again more 4 seconds later"
    scope.advanceTimeBy(4_000)
    val task8 = app.tasks.first()
    m And "wait until task is finished again"
    scope.advanceUntilIdle()
    m And "get task if nothing is running ance again"
    val task9 = app.tasks.first()

    m Then "First task is null"
    assertNull(task1)
    m And "Second, Third and Fourth tasks are the same"
    assertSame(task2, task3)
    assertSame(task3, task4)
    m And "Fifth task is null"
    assertNull(task5)
    m And "Sixth task is not the same as Fourth"
    assertNotSame(task4, task6)
    m And "Sixth, Seventh and Eighth tasks are the same as the one before"
    assertSame(task6, task7)
    assertSame(task7, task8)
    m And "Ninth task is null"
    assertNull(task9)
  }

  @Test
  fun `Cache the latest package info for the same app`() = coScenario { scope ->
    m Given "a package name"
    val packageName = "package0"
    m And "a repository mock"
    val packageInfoRepository = PackageInfoRepositoryMock()
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
    }.build()
    m And "app for the provided package name got"
    val app = installManager.getApp(packageName)

    m When "get info if nothing has happened yet"
    val info1 = app.packageInfo.first()
    m And "info updated in the system"
    packageInfoRepository.update(packageName, installedInfo(packageName, 0))
    scope.advanceUntilIdle()
    m And "get new info for the app"
    val info2 = app.packageInfo.first()
    m And "get the info for the app again during installation"
    app.install(installInfo)
    scope.advanceTimeBy(4_000)
    val info3 = app.packageInfo.first()
    m And "get the info for the app after install"
    scope.advanceUntilIdle()
    val info4 = app.packageInfo.first()
    m And "get info updated in the system again"
    packageInfoRepository.update(packageName, null)
    scope.advanceUntilIdle()
    m And "get new info if for the app again"
    val info5 = app.packageInfo.first()
    m And "info updated in the system once again"
    packageInfoRepository.update(packageName, installedInfo(packageName))
    scope.advanceUntilIdle()
    m And "get info before uninstall"
    val info6 = app.packageInfo.first()
    m And "get the info for the app again during uninstallation"
    app.uninstall()
    scope.advanceTimeBy(4_000)
    val info7 = app.packageInfo.first()
    m And "get the info for the app after uninstall"
    scope.advanceUntilIdle()
    val info8 = app.packageInfo.first()
    m And "info updated in the system one more time"
    packageInfoRepository.info.remove(packageName)
    packageInfoRepository.update(packageName, null)
    scope.advanceUntilIdle()
    m And "get the last app info"
    val info9 = app.packageInfo.first()

    m Then "First info is the same as initial one"
    assertNull(info1)
    m And "Second info is not the same as First"
    assertNotSame(info1, info2)
    m And "Second, Third and Fourth info are the same"
    assertSame(info2, info3)
    assertSame(info3, info4)
    m And "Fifth task is null"
    assertNull(info5)
    m And "Sixth info is not the same as Fourth"
    assertNotSame(info4, info6)
    m And "Sixth, Seventh and Eighth info are the same as the one before"
    assertSame(info6, info7)
    assertSame(info7, info8)
    m And "Ninth info is null"
    assertNull(info9)
  }

  @Test
  fun `Error calling install for the same version installed`() = coScenario { scope ->
    m Given "a package name"
    val packageName = "package0"
    m And "package info repository mock with the same version installed"
    val packageInfoRepository =
      PackageInfoRepositoryMock(mapOf(packageName to installedInfo(packageName, 2)))
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
    }.build()
    m And "app for the provided package name got or created"
    val app = installManager.getApp(packageName)

    m When "installation task started"
    val installThrown = assertThrows<IllegalArgumentException> {
      app.install(installInfo)
    }

    m Then "expected exception is thrown"
    assertEquals("This version is already installed", installThrown.message)
  }

  @Test
  fun `Error calling install for the newer version installed`() = coScenario { scope ->
    m Given "a package name"
    val packageName = "package0"
    m And "package info repository mock with the newer version installed"
    val packageInfoRepository =
      PackageInfoRepositoryMock(mapOf(packageName to installedInfo(packageName, 3)))
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
    }.build()
    m And "app for the provided package name got or created"
    val app = installManager.getApp(packageName)

    m When "installation task started"
    val installThrown = assertThrows<IllegalArgumentException> {
      app.install(installInfo)
    }

    m Then "expected exception is thrown"
    assertEquals("Newer version is installed", installThrown.message)
  }

  @Test
  fun `Error calling uninstall for not installed apps`() = coScenario { scope ->
    m Given "a package name"
    val packageName = "package0"
    m And "install manager initialised"
    val installManager = createBuilderWithMocks(scope).build()

    m When "create uninstall task for provided package name"
    val uninstallThrown = assertThrows<IllegalStateException> {
      installManager.getApp(packageName).uninstall()
    }

    m Then "expected exception is thrown"
    assertEquals("The $packageName is not installed", uninstallThrown.message)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Error calling install during installation`(
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
    m And "app for the provided package name got or created"
    val app = installManager.getApp(packageName)
    m And "installation task started"
    app.install(installInfo)

    m When "create installation task again"
    val installThrown = assertThrows<IllegalStateException> {
      app.install(installInfo)
    }

    m Then "expected exception is thrown"
    assertEquals("Another task is already queued", installThrown.message)
  }

  @Test
  fun `Error calling install during uninstallation`() = coScenario { scope ->
    m Given "a package name"
    val packageName = "package0"
    m And "package info repository mock with the same version installed"
    val packageInfoRepository =
      PackageInfoRepositoryMock(mapOf(packageName to installedInfo(packageName)))
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
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
    assertEquals("Another task is already queued", installThrown.message)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("variousPackageAppInfoProvider")
  fun `Error calling uninstall during installation`(
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
    m And "app for the provided package name got or created"
    val app = installManager.getApp(packageName)
    m And "installation task started"
    app.install(installInfo)

    m When "create uninstallation task"
    val uninstallThrown = assertThrows<IllegalStateException> {
      app.uninstall()
    }

    m Then "expected exception is thrown"
    assertEquals("Another task is already queued", uninstallThrown.message)
  }

  @Test
  fun `Error calling uninstall during uninstallation`() = coScenario { scope ->
    m Given "a package name"
    val packageName = "package0"
    m And "package info repository mock with the same version installed"
    val packageInfoRepository =
      PackageInfoRepositoryMock(mapOf(packageName to installedInfo(packageName)))
    m And "install manager initialised with this mock"
    val installManager = createBuilderWithMocks(scope).apply {
      this.packageInfoRepository = packageInfoRepository
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
    assertEquals("Another task is already queued", uninstallThrown.message)
  }

  companion object {
    @JvmStatic
    fun variousPackageAppInfoProvider(): Stream<Arguments> = savedPackageAppInfo()
  }
}