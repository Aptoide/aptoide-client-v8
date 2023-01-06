package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.*
import cm.aptoide.pt.util.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
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
  fun `Cache the task for the same app`(
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