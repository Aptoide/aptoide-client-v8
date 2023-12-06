package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.*
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
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
    val nullITask = app.task
    m And "get app install call result"
    val newITask = app.install(installInfo)
    m And "get running install task from the app immediately"
    val currentITaskNow = app.task
    m And "get running install task from the app 4 seconds later"
    scope.advanceTimeBy(4.seconds)
    val currentITaskLater = app.task
    m And "wait until install task is finished"
    scope.advanceUntilIdle()
    m And "get null task from the app after installation finished"
    val nullUTask = app.task
    m And "get app uninstall call result"
    val newUTask = app.uninstall()
    m And "get running uninstall task from the app immediately"
    val currentUTaskNow = app.task
    m And "get running uninstall task from the app 4 seconds later"
    scope.advanceTimeBy(4.seconds)
    val currentUTaskLater = app.task
    m And "wait until uninstall task is finished"
    scope.advanceUntilIdle()
    m And "get null task from the app after uninstallation finished"
    val nullTask = app.task

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
    m And "app package installer mock will not affect package info repository mock"
    mocks.packageInstaller.packageInfoRepositoryMock = null
    m And "app provided for the outdated version package name"
    val app = installManager.getApp(outdatedPackage)

    m When "get package info if app is not installed yet"
    val packageInfo = app.packageInfo
    m And "get package info for the app during installation"
    app.install(installInfo)
    scope.advanceTimeBy(4.seconds)
    val installingPackageInfo = app.packageInfo
    m And "get the info for the app after installation"
    scope.advanceUntilIdle()
    val installedPackageInfo = app.packageInfo
    m And "get package info for the app during uninstallation"
    app.uninstall()
    scope.advanceTimeBy(4.seconds)
    val uninstallingPackageInfo = app.packageInfo
    m And "get the info for the app after uninstallation"
    scope.advanceUntilIdle()
    val uninstalledPackageInfo = app.packageInfo

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

    m When "collect running tasks"
    val result = app.packageInfoFlow.collectAsync(scope)
    m And "get null package info"
    val nullInfo = app.packageInfo
    m And "new package info is added by the system"
    mocks.packageInfoRepository.update(notInstalledPackage, installedInfo(notInstalledPackage, 0))
    m And "get new package info from the app immediately"
    val newInfoNow = app.packageInfo
    scope.advanceTimeBy(4.seconds)
    m And "get new package info from the app later"
    val newInfoLater = app.packageInfo
    m And "package info is removed by the system"
    mocks.packageInfoRepository.update(notInstalledPackage, null)
    m And "get null package info from the app immediately"
    val nullInfoNow = app.packageInfo
    scope.advanceTimeBy(4.seconds)
    m And "get null package info from the app later"
    val nullInfoLater = app.packageInfo
    m And "newer package info is added by the system"
    mocks.packageInfoRepository.update(notInstalledPackage, installedInfo(notInstalledPackage))
    m And "get newer package info from the app immediately"
    val newerInfoNow = app.packageInfo
    scope.advanceTimeBy(4.seconds)
    m And "get newer package info from the app later"
    val newerInfoLater = app.packageInfo
    m And "package info is removed by the system again"
    mocks.packageInfoRepository.update(notInstalledPackage, null)
    m And "get null package info again from the app immediately"
    val nullAgainInfoNow = app.packageInfo
    scope.advanceUntilIdle()
    m And "get null package info again from the app later"
    val nullAgainInfoLater = app.packageInfo

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
    m And "collected info is in right sequence"
    assertEquals(
      listOf(null, newInfoNow, null, newerInfoNow, null),
      result
    )
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

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app install call result"
    val task = app.install(installInfo)
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Create an install Task if calling install with negative missing space`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "free space checker mock will report there will be -1536 of free space missing"
    mocks.freeSpaceChecker.willMissSpace = -1536
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app install call result"
    val task = app.install(installInfo)
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Create an install Task if called install omitting free space check`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "free space checker mock will report there will be -1536 of free space missing"
    mocks.freeSpaceChecker.willMissSpace = 1536
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app install call result"
    val task = app.install(installPackageInfo = installInfo, omitFreeSpaceCheck = true)
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Create an install Task if calling install and download fails`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will fail after 25%"
    mocks.packageDownloader.progressFlow = failingFlow
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app install call result"
    val task = app.install(installInfo)
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Create an install Task if calling install and installation fails`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will fail after 25%"
    mocks.packageInstaller.progressFlow = failingFlow
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app install call result"
    val task = app.install(installInfo)
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Create an install Task if calling install and download aborts`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will abort after 25%"
    mocks.packageDownloader.progressFlow = abortingFlow
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app install call result"
    val task = app.install(installInfo)
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Create an install Task if calling install and installation aborts`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will abort after 25%"
    mocks.packageInstaller.progressFlow = abortingFlow
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app install call result"
    val task = app.install(installInfo)
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Create an install Task if calling install and download cancels immediately`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will wait for cancellation after 25%"
    mocks.packageDownloader.progressFlow = cancellingFlow
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app install call result"
    val task = app.install(installInfo)
    m And "call the task cancel"
    task.cancel()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Create an install Task if calling install and download cancels`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package downloader mock will wait for cancellation after 25%"
    mocks.packageDownloader.progressFlow = cancellingFlow
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app install call result"
    val task = app.install(installInfo)
    m And "wait until task will be ready to cancel"
    delay(45.minutes)
    m And "call the task cancel"
    task.cancel()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Create an install Task if calling install and installation cancels`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will wait for cancellation after 25%"
    mocks.packageInstaller.progressFlow = cancellingFlow
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app install call result"
    val task = app.install(installInfo)
    m And "wait until task will be ready to cancel"
    delay(45.minutes)
    m And "call the task cancel"
    task.cancel()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Create an install Task if calling install with missing space for a task`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "free space checker mock will report there will be -1536 of free space missing"
    mocks.freeSpaceChecker.missingSpace = 1536
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app install call result"
    val task = app.install(installInfo)
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of install type"
    assertEquals(Task.Type.INSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
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

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app uninstall call result"
    val task = app.uninstall()
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of uninstall type"
    assertEquals(Task.Type.UNINSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("uninstallablePackageAppInfoProvider")
  fun `Create an uninstall Task if calling uninstall and uninstallation fails`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will fail after 25%"
    mocks.packageInstaller.progressFlow = failingFlow
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app uninstall call result"
    val task = app.uninstall()
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of uninstall type"
    assertEquals(Task.Type.UNINSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("uninstallablePackageAppInfoProvider")
  fun `Create an uninstall Task if calling uninstall and uninstallation aborts`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will abort after 25%"
    mocks.packageInstaller.progressFlow = abortingFlow
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app uninstall call result"
    val task = app.uninstall()
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of uninstall type"
    assertEquals(Task.Type.UNINSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("uninstallablePackageAppInfoProvider")
  fun `Create an uninstall Task if calling uninstall and uninstallation cancels`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "package installer mock will wait for cancellation after 25%"
    mocks.packageInstaller.progressFlow = cancellingFlow
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "get app uninstall call result"
    val task = app.uninstall()
    m And "wait until task will be ready to cancel"
    delay(45.minutes)
    m And "call the task cancel"
    task.cancel()
    m And "wait until the task finishes"
    scope.advanceUntilIdle()

    m Then "the created task is of uninstall type"
    assertEquals(Task.Type.UNINSTALL, task.type)
    m And "A running task appeared in the app"
    assertEquals(
      listOf(null, task, null),
      result
    )
  }

  @Test
  fun `Error calling install for the same version installed`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for the current version package name"
    val app = installManager.getApp(currentPackage)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "check if app can install"
    val check = app.canInstall(installInfo)
    m And "calling app install"
    val installThrown = assertThrows<IllegalArgumentException> {
      app.install(installInfo)
    }
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "already installed exception is returned on check"
    assertTrue(check is IllegalArgumentException)
    assertEquals("This version is already installed", check?.message)
    m And "already installed exception is thrown"
    assertEquals("This version is already installed", installThrown.message)
    m And "No running tasks appeared in the app"
    assertEquals(
      listOf(null),
      result
    )
  }

  @Test
  fun `Error calling install for the newer version installed`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for the newer version package name"
    val app = installManager.getApp(newerPackage)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "check if app can install"
    val check = app.canInstall(installInfo)
    m And "calling app install"
    val installThrown = assertThrows<IllegalArgumentException> {
      app.install(installInfo)
    }
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "newer version installed exception is returned on check"
    assertTrue(check is IllegalArgumentException)
    assertEquals("Newer version is installed", check?.message)
    m And "newer version installed exception is thrown"
    assertEquals("Newer version is installed", installThrown.message)
    m And "No running tasks appeared in the app"
    assertEquals(
      listOf(null),
      result
    )
  }

  @Test
  fun `Error calling uninstall for not installed apps`() = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "app provided for the not installed package name"
    val app = installManager.getApp(notInstalledPackage)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "check if app can uninstall"
    val check = app.canUninstall()
    m And "calling app uninstall"
    val uninstallThrown = assertThrows<IllegalStateException> {
      app.uninstall()
    }
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "not installed exception is returned on check"
    assertTrue(check is IllegalStateException)
    assertEquals("The $notInstalledPackage is not installed", check?.message)
    m And "not installed exception is thrown"
    assertEquals("The $notInstalledPackage is not installed", uninstallThrown.message)
    m And "No running tasks appeared in the app"
    assertEquals(
      listOf(null),
      result
    )
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
    val oldTask = app.install(installInfo)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "check if app can install"
    val check = app.canInstall(installInfo)
    m And "calling app install again"
    val installThrown = assertThrows<IllegalStateException> {
      app.install(installInfo)
    }
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "busy exception is returned on check"
    assertTrue(check is IllegalStateException)
    assertEquals("Another task is already queued", check?.message)
    m And "busy exception is thrown"
    assertEquals("Another task is already queued", installThrown.message)
    m And "Only already running task appeared in the app"
    assertEquals(
      listOf(oldTask, null),
      result
    )
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
    val oldTask = app.uninstall()

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "check if app can install"
    val check = app.canInstall(installInfo)
    m And "calling app install"
    val installThrown = assertThrows<IllegalStateException> {
      app.install(installInfo)
    }
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "busy exception is returned on check"
    assertTrue(check is IllegalStateException)
    assertEquals("Another task is already queued", check?.message)
    m And "busy exception is thrown"
    assertEquals("Another task is already queued", installThrown.message)
    m And "Only already running task appeared in the app"
    assertEquals(
      listOf(oldTask, null),
      result
    )
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
    val oldTask = app.install(installInfo)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "check if app can uninstall"
    val check = app.canUninstall()
    m And "calling app uninstall"
    val uninstallThrown = assertThrows<IllegalStateException> {
      app.uninstall()
    }
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "busy exception is returned on check"
    assertTrue(check is IllegalStateException)
    assertEquals("Another task is already queued", check?.message)
    m And "busy exception is thrown"
    assertEquals("Another task is already queued", uninstallThrown.message)
    m And "Only already running task appeared in the app"
    assertEquals(
      listOf(oldTask, null),
      result
    )
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
    val oldTask = app.uninstall()

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "check if app can uninstall"
    val check = app.canUninstall()
    m And "calling app uninstall"
    val uninstallThrown = assertThrows<IllegalStateException> {
      app.uninstall()
    }
    m And "wait until running task if any finishes"
    scope.advanceUntilIdle()

    m Then "busy exception is returned on check"
    assertTrue(check is IllegalStateException)
    assertEquals("Another task is already queued", check?.message)
    m And "busy exception is thrown"
    assertEquals("Another task is already queued", uninstallThrown.message)
    m And "Only already running task appeared in the app"
    assertEquals(
      listOf(oldTask, null),
      result
    )
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("installablePackageAppInfoProvider")
  fun `Error calling install if not enough free space`(
    comment: String,
    packageName: String,
  ) = coScenario { scope ->
    m Given "install manager initialised with mocks"
    val mocks = Mocks(scope)
    val installManager = InstallManager.with(mocks)
    m And "free space checker mock will report there will be 1536 of free space missing"
    mocks.freeSpaceChecker.willMissSpace = 1536
    m And "app provided for a given package name"
    val app = installManager.getApp(packageName)

    m When "collect running tasks"
    val result = app.taskFlow.collectAsync(scope)
    m And "check if app can install"
    val check = app.canInstall(installInfo)
    m And "calling app install"
    val installThrown = assertThrows<OutOfSpaceException> {
      app.install(installInfo)
    }
    m And "wait until task finishes"
    scope.advanceUntilIdle()

    m Then "out of space exception is returned on check"
    assertTrue(check is OutOfSpaceException)
    assertEquals("Not enough free space to download and install", check?.message)
    m And "out of space exception is thrown"
    assertEquals("Not enough free space to download and install", installThrown.message)
    m And "No running tasks appeared in the app"
    assertEquals(
      listOf(null),
      result
    )
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
