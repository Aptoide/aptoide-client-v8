package cm.aptoide.pt.install_manager

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
 * FOR watching for app package info changes and managing install/uninstall tasks
 */
@ExperimentalCoroutinesApi
internal class AppTest {

  @Test
  fun `Cache the tasks or nulls for the same app`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for the outdated version package name"
    val app = installManager.getApp(outdatedPackage)

    m When "get null task from the app"
    val nullITask = app.tasks.first()
    m And "get app install call result"
    val newITask = app.install(installInfo)
    m And "get running install task from the app immediately"
    val currentITaskNow = app.tasks.first()
    m And "get running install task from the app 4 seconds later"
    scope.advanceTimeBy(4_000)
    val currentITaskLater = app.tasks.first()
    m And "wait until install task is finished"
    scope.advanceUntilIdle()
    m And "get null task from the app after installation finished"
    val nullUTask = app.tasks.first()
    m And "get app uninstall call result"
    val newUTask = app.uninstall()
    m And "get running uninstall task from the app immediately"
    val currentUTaskNow = app.tasks.first()
    m And "get running uninstall task from the app 4 seconds later"
    scope.advanceTimeBy(4_000)
    val currentUTaskLater = app.tasks.first()
    m And "wait until uninstall task is finished"
    scope.advanceUntilIdle()
    m And "get null task from the app after uninstallation finished"
    val nullTask = app.tasks.first()

    m Then "Null tasks are actually null"
    assertNull(nullITask)
    assertNull(nullUTask)
    assertNull(nullTask)
    m And "Install and Uninstall tasks are not null"
    assertNotNull(newITask)
    assertNotNull(currentITaskNow)
    assertNotNull(currentITaskLater)
    assertNotNull(newUTask)
    assertNotNull(currentUTaskNow)
    assertNotNull(currentUTaskLater)
    m And "Install tasks are the same"
    assertSame(newITask, currentITaskNow)
    assertSame(currentITaskNow, currentITaskLater)
    m And "Uninstall tasks are the same"
    assertSame(newUTask, currentUTaskNow)
    assertSame(currentUTaskNow, currentUTaskLater)
    m And "Install and Uninstall tasks are different"
    assertNotEquals(currentITaskLater, newUTask)
  }

  @Test
  fun `Package info from the app isn't affected by installs & uninstalls`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for the outdated version package name"
    val app = installManager.getApp(outdatedPackage)

    m When "get package info if app is not installed yet"
    val packageInfo = app.packageInfo.first()
    m And "get package info for the app during installation"
    app.install(installInfo)
    scope.advanceTimeBy(4_000)
    val installingPackageInfo = app.packageInfo.first()
    m And "get the info for the app after installation"
    scope.advanceUntilIdle()
    val installedPackageInfo = app.packageInfo.first()
    m And "get package info for the app during uninstallation"
    app.uninstall()
    scope.advanceTimeBy(4_000)
    val uninstallingPackageInfo = app.packageInfo.first()
    m And "get the info for the app after uninstallation"
    scope.advanceUntilIdle()
    val uninstalledPackageInfo = app.packageInfo.first()

    m Then "package info is not null"
    assertNotNull(packageInfo)
    m And "is the same for all app actions"
    assertSame(packageInfo, installingPackageInfo)
    assertSame(installingPackageInfo, installedPackageInfo)
    assertSame(installedPackageInfo, uninstallingPackageInfo)
    assertSame(uninstallingPackageInfo, uninstalledPackageInfo)
  }

  @Test
  fun `Return the actual package info for the app`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for the not installed package name"
    val app = installManager.getApp(notInstalledPackage)

    m When "get null package info"
    val nullInfo = app.packageInfo.first()
    m And "new package info is added by the system"
    mocks.packageInfoRepository.update(notInstalledPackage, installedInfo(notInstalledPackage, 0))
    m And "get new package info from the app immediately"
    val newInfoNow = app.packageInfo.first()
    scope.advanceUntilIdle()
    m And "get new package info from the app later"
    val newInfoLater = app.packageInfo.first()
    m And "package info is removed by the system"
    mocks.packageInfoRepository.update(notInstalledPackage, null)
    m And "get null package info from the app immediately"
    val nullInfoNow = app.packageInfo.first()
    scope.advanceUntilIdle()
    m And "get null package info from the app later"
    val nullInfoLater = app.packageInfo.first()
    m And "newer package info is added by the system"
    mocks.packageInfoRepository.update(notInstalledPackage, installedInfo(notInstalledPackage))
    m And "get newer package info from the app immediately"
    val newerInfoNow = app.packageInfo.first()
    scope.advanceUntilIdle()
    m And "get newer package info from the app later"
    val newerInfoLater = app.packageInfo.first()
    m And "package info is removed by the system again"
    mocks.packageInfoRepository.update(notInstalledPackage, null)
    m And "get null package info again from the app immediately"
    val nullAgainInfoNow = app.packageInfo.first()
    scope.advanceUntilIdle()
    m And "get null package info again from the app later"
    val nullAgainInfoLater = app.packageInfo.first()

    m Then "null package info's are actually null"
    assertNull(nullInfo)
    assertNull(nullInfoNow)
    assertNull(nullInfoLater)
    assertNull(nullAgainInfoNow)
    assertNull(nullAgainInfoLater)
    m And "new package info is not null and is the same always"
    assertNotNull(newInfoNow)
    assertSame(newInfoNow, newInfoLater)
    m And "newer package info is not null and is the same always"
    assertNotNull(newerInfoNow)
    assertSame(newerInfoNow, newerInfoLater)
    m And "new info is not the same as newer info"
    assertNotEquals(newInfoNow, newerInfoNow)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Create an install Task if calling install`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "get app install call result"
    val task = app.install(installInfo)

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("uninstallablePackageAppInfoProvider")
  fun `Create an uninstall Task if calling uninstall`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "get app uninstall call result"
    val task = app.uninstall()

    m Then "the created task is of uninstall type"
    assertEquals(Task.Type.UNINSTALL, task.type)
  }

  @Test
  fun `Error calling install for the same version installed`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for the current version package name"
    val app = installManager.getApp(currentPackage)

    m When "calling app install"
    val installThrown = assertThrows<IllegalArgumentException> {
      app.install(installInfo)
    }

    m Then "already installed exception is thrown"
    assertEquals("This version is already installed", installThrown.message)
  }

  @Test
  fun `Error calling install for the newer version installed`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for the newer version package name"
    val app = installManager.getApp(newerPackage)

    m When "calling app install"
    val installThrown = assertThrows<IllegalArgumentException> {
      app.install(installInfo)
    }

    m Then "newer version installed exception is thrown"
    assertEquals("Newer version is installed", installThrown.message)
  }

  @Test
  fun `Error calling uninstall for not installed apps`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for the not installed package name"
    val app = installManager.getApp(notInstalledPackage)

    m When "calling app uninstall"
    val uninstallThrown = assertThrows<IllegalStateException> {
      app.uninstall()
    }

    m Then "not installed exception is thrown"
    assertEquals("The $notInstalledPackage is not installed", uninstallThrown.message)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Error calling install during installation`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)
    m And "app install called"
    app.install(installInfo)

    m When "calling app install again"
    val installThrown = assertThrows<IllegalStateException> {
      app.install(installInfo)
    }

    m Then "busy exception is thrown"
    assertEquals("Another task is already queued", installThrown.message)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("uninstallablePackageAppInfoProvider")
  fun `Error calling install during uninstallation`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for a given package name"
    val app = installManager.getApp(currentPackage)
    m And "app uninstall called"
    app.uninstall()

    m When "calling app install"
    val installThrown = assertThrows<IllegalStateException> {
      app.install(installInfo)
    }

    m Then "busy exception is thrown"
    assertEquals("Another task is already queued", installThrown.message)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Error calling uninstall during installation`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)
    m And "app install called"
    app.install(installInfo)

    m When "calling app uninstall"
    val uninstallThrown = assertThrows<IllegalStateException> {
      app.uninstall()
    }

    m Then "busy exception is thrown"
    assertEquals("Another task is already queued", uninstallThrown.message)
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("uninstallablePackageAppInfoProvider")
  fun `Error calling uninstall during uninstallation`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for a given package name"
    val app = installManager.getApp(currentPackage)
    m And "app uninstall called"
    app.uninstall()

    m When "calling app uninstall"
    val uninstallThrown = assertThrows<IllegalStateException> {
      app.uninstall()
    }

    m Then "busy exception is thrown"
    assertEquals("Another task is already queued", uninstallThrown.message)
  }

  companion object {
    @JvmStatic
    fun installablePackageAppInfoProvider(): Stream<Arguments> = Stream.of(
      Arguments.arguments("Not installed package", notInstalledPackage),
      Arguments.arguments("Outdated package installed", outdatedPackage),
    )

    @JvmStatic
    fun uninstallablePackageAppInfoProvider(): Stream<Arguments> = Stream.of(
      Arguments.arguments("Outdated package installed", outdatedPackage),
      Arguments.arguments("Current package installed", currentPackage),
      Arguments.arguments("Newer package installed", newerPackage),
    )
  }
}
