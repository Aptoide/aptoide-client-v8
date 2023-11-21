package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.repository.PackageInfoRepository
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

enum class Speed {
  SLOW,
  MODERATE,
  FAST
}

internal const val notInstalledPackage = "package0"
internal const val outdatedPackage = "package1"
internal const val currentPackage = "package2"
internal const val newerPackage = "package3"

internal data class Mocks(internal val scope: TestScope) {
  internal val packageInfoRepository = PackageInfoRepositoryMock()
  internal val taskInfoRepository = TaskInfoRepositoryMock()
  internal val packageDownloader = PackageDownloaderMock(scope)
  internal val packageInstaller = PackageInstallerMock(scope)
}

@ExperimentalCoroutinesApi
internal fun InstallManager.Companion.with(mocks: Mocks): InstallManager = RealInstallManager(
  scope = mocks.scope,
  currentTime = { mocks.scope.currentTime },
  packageInfoRepository = mocks.packageInfoRepository,
  taskInfoRepository = mocks.taskInfoRepository,
  packageDownloader = mocks.packageDownloader,
  packageInstaller = mocks.packageInstaller
)

/* Data */

@Suppress("DEPRECATION")
internal fun installedInfo(packageName: String, vc: Int = 1) = PackageInfo().apply {
  this.packageName = packageName
  versionName = "1.0.$vc"
  versionCode = vc
}

internal val installInfo = InstallPackageInfo(
  versionCode = 2,
  installationFiles = setOf(
    InstallationFile(
      name = "base.apk",
      md5 = "md5-base.apk",
      fileSize = 1560,
      type = InstallationFile.Type.BASE,
      url = "http://base.apk",
      altUrl = "https://base.apk",
      localPath = "file://base.apk"
    ),
    InstallationFile(
      name = "pfd.apk",
      md5 = "md5-pfd.apk",
      fileSize = 560,
      type = InstallationFile.Type.PFD_INSTALL_TIME,
      url = "http://pfd.apk",
      altUrl = "https://pfd.apk",
      localPath = "file://pfd.apk"
    ),
    InstallationFile(
      name = "pad.apk",
      md5 = "md5-pad.apk",
      fileSize = 760,
      type = InstallationFile.Type.PAD_INSTALL_TIME,
      url = "http://pad.apk",
      altUrl = "https://pad.apk",
      localPath = "file://pad.apk"
    )
  )
)

internal val uninstallInfo = InstallPackageInfo(
  versionCode = 1,
  installationFiles = setOf()
)

/* Mocks flows */

internal val successFlow: suspend FlowCollector<Int>.(Duration, Channel<Unit>) -> Unit =
  { duration, _ ->
    delay(duration)
    emit(0)
    delay(duration)
    emit(25)
    delay(duration)
    emit(50)
    delay(duration)
    emit(75)
    delay(duration)
  }

internal val failingFlow: suspend FlowCollector<Int>.(Duration, Channel<Unit>) -> Unit =
  { duration, _ ->
    delay(duration)
    emit(0)
    delay(duration)
    emit(25)
    delay(duration)
    throw RuntimeException("Problem!")
  }

internal val abortingFlow: suspend FlowCollector<Int>.(Duration, Channel<Unit>) -> Unit =
  { duration, _ ->
    delay(duration)
    emit(0)
    delay(duration)
    emit(25)
    delay(duration)
    throw AbortException("No go!")
  }

internal val cancellingFlow: suspend FlowCollector<Int>.(Duration, Channel<Unit>) -> Unit =
  { duration, lock ->
    delay(duration)
    emit(0)
    delay(duration)
    emit(25)
    delay(duration)
    lock.receive()
    throw CancellationException("Cancelled")
  }

/* Mocks */

// Crashes on duplicated calls for optimization reasons
internal class PackageInfoRepositoryMock : PackageInfoRepository {
  private var allCalled = false
  internal val info: MutableMap<String, PackageInfo?> = mutableMapOf(
    outdatedPackage to installedInfo(outdatedPackage),
    currentPackage to installedInfo(currentPackage, vc = 2),
    newerPackage to installedInfo(newerPackage, vc = 3),
  )

  private var listener: (String) -> Unit = {}

  override fun getAll(): Set<PackageInfo> {
    if (allCalled) throw java.lang.IllegalStateException("Duplicate call")
    allCalled = true
    return info.values.filterNotNull().toSet()
  }

  override fun get(packageName: String): PackageInfo? = info[packageName]

  override fun setOnChangeListener(onChange: (String) -> Unit) {
    listener = onChange
  }

  suspend fun update(pn: String, pi: PackageInfo?) {
    info[pn] = pi
    listener(pn)
    delay(1) // Suspend to let informing the listeners before the next app data update
  }
}

// Crashes on duplicated calls for optimization reasons
internal class TaskInfoRepositoryMock : TaskInfoRepository {
  private val info: MutableSet<TaskInfo> = mutableSetOf(
    TaskInfo(
      packageName = notInstalledPackage,
      installPackageInfo = installInfo,
      type = Task.Type.INSTALL,
      timestamp = 2
    ),
    TaskInfo(
      packageName = currentPackage,
      installPackageInfo = uninstallInfo,
      type = Task.Type.UNINSTALL,
      timestamp = 3
    ),
    TaskInfo(
      packageName = newerPackage,
      installPackageInfo = uninstallInfo,
      type = Task.Type.UNINSTALL,
      timestamp = 1
    ),
  )

  private var allCalled = false
  private val saveCalledFor: MutableSet<TaskInfo> = mutableSetOf()
  private val removeAllCalledFor: MutableSet<String> = mutableSetOf()
  private val execute: suspend () -> Unit = {
    delay(delay)
    if (shouldFail) throw RuntimeException("Problem!")
  }

  private var delay = Random.nextLong(LongRange(20, 2000)).milliseconds
  internal var shouldFail: Boolean = false

  internal fun setSpeed(speed: Speed) {
    delay = when (speed) {
      Speed.SLOW -> 2.toLong().seconds
      Speed.MODERATE -> 1.toLong().seconds
      Speed.FAST -> 20.toLong().milliseconds
    }
  }

  override suspend fun getAll(): Set<TaskInfo> {
    if (allCalled) throw java.lang.IllegalStateException("Duplicate call")
    allCalled = true
    execute()
    return info
  }

  override suspend fun saveJob(taskInfo: TaskInfo) {
    if (!saveCalledFor.add(taskInfo)) throw java.lang.IllegalStateException("Duplicate call for ${taskInfo.packageName}")
    execute()
    info.add(taskInfo)
    removeAllCalledFor.remove(taskInfo.packageName)
  }

  override suspend fun removeAll(packageName: String) {
    if (!removeAllCalledFor.add(packageName)) throw java.lang.IllegalStateException("Duplicate call for $packageName")
    execute()
    info.removeAll { it.packageName == packageName }
  }

  fun get(pn: String): TaskInfo? = info.find { it.packageName == pn }
}

// Crashes on duplicated calls for optimization reasons
internal class PackageDownloaderMock constructor(
  private val scope: CoroutineScope,
) : PackageDownloader {
  private val downloadCalled: MutableSet<String> = mutableSetOf()
  private val cancelCalled: MutableSet<String> = mutableSetOf()
  private val lock: Channel<Unit> = Channel()

  private var delay = Random.nextLong(LongRange(12, 240)).seconds
  internal var progressFlow: suspend FlowCollector<Int>.(Duration, Channel<Unit>) -> Unit =
    successFlow

  internal fun setSpeed(speed: Speed) {
    delay = when (speed) {
      Speed.SLOW -> 4.toLong().minutes
      Speed.MODERATE -> 1.toLong().minutes
      Speed.FAST -> 12.toLong().seconds
    }
  }

  override suspend fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    if (!downloadCalled.add(packageName)) throw java.lang.IllegalStateException("Duplicate call for $packageName")
    return flow {
      progressFlow(delay, lock)
    }
  }

  override fun cancel(packageName: String): Boolean {
    if (cancelCalled.contains(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    cancelCalled.add(packageName)
    return if (progressFlow == cancellingFlow) {
      scope.launch { lock.send(Unit) }
      true
    } else {
      false
    }
  }
}

// Crashes on duplicated calls for optimization reasons
internal class PackageInstallerMock constructor(
  private val scope: CoroutineScope,
) : PackageInstaller {
  private val installCalled: MutableSet<String> = mutableSetOf()
  private val uninstallCalled: MutableSet<String> = mutableSetOf()
  private val cancelCalled: MutableSet<String> = mutableSetOf()
  private val lock: Channel<Unit> = Channel()

  private var delay = Random.nextLong(LongRange(4, 24)).seconds
  internal var progressFlow: suspend FlowCollector<Int>.(Duration, Channel<Unit>) -> Unit =
    successFlow

  internal fun setSpeed(speed: Speed) {
    delay = when (speed) {
      Speed.SLOW -> 24.toLong().seconds
      Speed.MODERATE -> 16.toLong().seconds
      Speed.FAST -> 4.toLong().seconds
    }
  }

  override suspend fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    if (!installCalled.add(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    return flow {
      progressFlow(delay, lock)
      uninstallCalled.remove(packageName)
    }
  }

  override suspend fun uninstall(packageName: String): Flow<Int> {
    if (!uninstallCalled.add(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    return flow {
      progressFlow(delay, lock)
      installCalled.remove(packageName)
    }
  }

  override fun cancel(packageName: String): Boolean {
    if (cancelCalled.contains(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    cancelCalled.add(packageName)
    return if (progressFlow == cancellingFlow) {
      scope.launch { lock.send(Unit) }
      true
    } else {
      false
    }
  }
}
