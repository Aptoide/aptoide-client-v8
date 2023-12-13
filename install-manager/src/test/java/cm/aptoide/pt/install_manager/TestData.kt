package cm.aptoide.pt.install_manager

import android.content.pm.PackageInfo
import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.dto.TaskInfo
import cm.aptoide.pt.install_manager.environment.FreeSpaceChecker
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import cm.aptoide.pt.install_manager.repository.PackageInfoRepository
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal data class Mocks(internal val scope: TestScope) {
  internal val packageInfoRepository = PackageInfoRepositoryMock()
  internal val taskInfoRepository = TaskInfoRepositoryMock()
  internal val packageDownloader = PackageDownloaderMock(scope)
  internal val packageInstaller = PackageInstallerMock(
    scope = scope,
    packageDownloaderMock = packageDownloader,
    packageInfoRepositoryMock = packageInfoRepository,
  )
  internal val freeSpaceChecker = FreeSpaceCheckerMock()
  internal val networkConnection = NetworkConnectionMock()
}

@ExperimentalCoroutinesApi
internal fun InstallManager.Companion.with(mocks: Mocks): InstallManager = RealInstallManager(
  scope = mocks.scope,
  currentTime = { mocks.scope.currentTime },
  packageInfoRepository = mocks.packageInfoRepository,
  taskInfoRepository = mocks.taskInfoRepository,
  packageDownloader = mocks.packageDownloader,
  packageInstaller = mocks.packageInstaller,
  freeSpaceChecker = mocks.freeSpaceChecker,
  networkConnection = mocks.networkConnection,
)

/* Data */

enum class Speed {
  SLOW,
  MODERATE,
  FAST
}

internal const val notInstalledPackage = "notInstalledPackage"
internal const val outdatedPackage = "outdatedPackage"
internal const val currentPackage = "currentPackage"
internal const val newerPackage = "newerPackage"

internal val constraints = listOf(
  Constraints(
    checkForFreeSpace = false,
    networkType = Constraints.NetworkType.NOT_REQUIRED
  ),
  Constraints(
    checkForFreeSpace = false,
    networkType = Constraints.NetworkType.ANY
  ),
  Constraints(
    checkForFreeSpace = false,
    networkType = Constraints.NetworkType.UNMETERED
  ),
  Constraints(
    checkForFreeSpace = true,
    networkType = Constraints.NetworkType.NOT_REQUIRED
  ),
  Constraints(
    checkForFreeSpace = true,
    networkType = Constraints.NetworkType.ANY
  ),
  Constraints(
    checkForFreeSpace = true,
    networkType = Constraints.NetworkType.UNMETERED
  ),
)

@Suppress("DEPRECATION")
internal fun installedInfo(packageName: String, vc: Long = 1) = PackageInfo().apply {
  this.packageName = packageName
  versionName = "1.0.$vc"
  versionCode = vc.toInt()
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

internal val savedTasksInfo = constraints.mapIndexed { index, constraints ->
  listOf(
    TaskInfo(
      packageName = "not_installed_${index * 2}",
      installPackageInfo = installInfo,
      constraints = constraints,
      type = Task.Type.INSTALL,
      timestamp = index * 2L
    ),
    TaskInfo(
      packageName = "installed_${index * 2 + 1}",
      installPackageInfo = installInfo,
      constraints = constraints,
      type = Task.Type.UNINSTALL,
      timestamp = index * 2L + 1
    ),
  )
}.flatten()

internal val installedPackages = savedTasksInfo.mapNotNull {
  if (it.type == Task.Type.INSTALL) {
    null
  } else {
    it.packageName to installedInfo(it.packageName)
  }
}

internal fun getRunnableSavedTasksPackages(
  networkState: NetworkConnection.State,
): List<String> = savedTasksInfo.mapNotNull {
  when (networkState) {
    NetworkConnection.State.GONE -> when (it.constraints.networkType) {
      Constraints.NetworkType.NOT_REQUIRED -> it.packageName
      Constraints.NetworkType.ANY -> null
      Constraints.NetworkType.UNMETERED -> null
    }

    NetworkConnection.State.METERED -> when (it.constraints.networkType) {
      Constraints.NetworkType.NOT_REQUIRED -> it.packageName
      Constraints.NetworkType.ANY -> it.packageName
      Constraints.NetworkType.UNMETERED -> null
    }

    NetworkConnection.State.UNMETERED -> it.packageName
  }
}

internal fun getRunnableTasks(
  networkState: NetworkConnection.State,
  apps: List<Task>,
): List<String> = apps.mapNotNull {
  when (networkState) {
    NetworkConnection.State.GONE -> when (it.constraints.networkType) {
      Constraints.NetworkType.NOT_REQUIRED -> it.packageName
      Constraints.NetworkType.ANY -> null
      Constraints.NetworkType.UNMETERED -> null
    }

    NetworkConnection.State.METERED -> when (it.constraints.networkType) {
      Constraints.NetworkType.NOT_REQUIRED -> it.packageName
      Constraints.NetworkType.ANY -> it.packageName
      Constraints.NetworkType.UNMETERED -> null
    }

    NetworkConnection.State.UNMETERED -> it.packageName
  }
}

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
  private var listenerSet = false
  internal val info: MutableMap<String, PackageInfo?> = mutableMapOf(
    outdatedPackage to installedInfo(outdatedPackage),
    currentPackage to installedInfo(currentPackage, vc = 2),
    newerPackage to installedInfo(newerPackage, vc = 3),
  )
    .apply { putAll(installedPackages) }
    .toMutableMap()

  private var listener: (String) -> Unit = {}

  override fun getAll(): Set<PackageInfo> {
    if (allCalled) throw java.lang.IllegalStateException("Duplicate call")
    allCalled = true
    return info.values.filterNotNull().toSet()
  }

  override fun get(packageName: String): PackageInfo? = info[packageName]

  override fun setOnChangeListener(onChange: (String) -> Unit) {
    if (listenerSet) throw java.lang.IllegalStateException("Duplicate call")
    listenerSet = true
    listener = onChange
  }

  internal fun update(pn: String, pi: PackageInfo?) {
    info[pn] = pi
    listener(pn)
  }
}

// Crashes on duplicated calls for optimization reasons
internal class TaskInfoRepositoryMock : TaskInfoRepository {
  private val info: MutableSet<TaskInfo> = savedTasksInfo.shuffled().toMutableSet()

  private var allCalled = false
  private val saveCalledFor: MutableSet<TaskInfo> = mutableSetOf()
  private val removeAllCalledFor: MutableSet<String> = mutableSetOf()
  private val tryFail: suspend () -> Unit = {
    delay(delay)
    if (shouldFail) throw RuntimeException("Problem!")
  }

  private var delay = Random.nextLong(LongRange(20, 2000)).milliseconds
  internal var shouldFail: Boolean = false

  internal fun setSpeed(speed: Speed) {
    delay = when (speed) {
      Speed.SLOW -> 2.seconds
      Speed.MODERATE -> 1.seconds
      Speed.FAST -> 20.milliseconds
    }
  }

  override suspend fun getAll(): Set<TaskInfo> {
    if (allCalled) throw java.lang.IllegalStateException("Duplicate call")
    allCalled = true
    tryFail()
    return info
  }

  override suspend fun saveJob(taskInfo: TaskInfo) {
    if (!saveCalledFor.add(taskInfo)) {
      throw java.lang.IllegalStateException("Duplicate call for ${taskInfo.packageName}")
    }
    tryFail()
    if (!info.add(taskInfo)) {
      throw java.lang.IllegalStateException("${taskInfo.packageName} already saved")
    }
    removeAllCalledFor.remove(taskInfo.packageName)
  }

  override suspend fun removeAll(packageName: String) {
    if (!removeAllCalledFor.add(packageName)) {
      throw java.lang.IllegalStateException("Duplicate call for $packageName")
    }
    tryFail()
    if (!info.removeAll { it.packageName == packageName }) {
      throw java.lang.IllegalStateException("$packageName already removed")
    }
  }

  fun get(pn: String): TaskInfo? = info.find { it.packageName == pn }
}

// Crashes on duplicated calls for optimization reasons
internal class PackageDownloaderMock(
  private val scope: CoroutineScope,
) : PackageDownloader {
  internal val downloadCalled: MutableSet<String> = mutableSetOf()
  private val cancelCalled: MutableSet<String> = mutableSetOf()
  private val lock: Channel<Unit> = Channel()

  private var delay = Random.nextLong(LongRange(12, 240)).seconds
  internal var progressFlow: suspend FlowCollector<Int>.(Duration, Channel<Unit>) -> Unit =
    successFlow

  internal fun setSpeed(speed: Speed) {
    delay = when (speed) {
      Speed.SLOW -> 4.minutes
      Speed.MODERATE -> 1.minutes
      Speed.FAST -> 12.seconds
    }
  }

  override fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    if (!downloadCalled.add(packageName)) throw IllegalStateException("Duplicate call for $packageName")
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
internal class PackageInstallerMock(
  private val scope: CoroutineScope,
  internal var packageDownloaderMock: PackageDownloaderMock?,
  internal var packageInfoRepositoryMock: PackageInfoRepositoryMock?,
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
      Speed.SLOW -> 24.seconds
      Speed.MODERATE -> 16.seconds
      Speed.FAST -> 4.seconds
    }
  }

  override fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    if (!installCalled.add(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    return flow {
      progressFlow(delay, lock)
      packageInfoRepositoryMock?.update(
        packageName,
        installedInfo(packageName, vc = installPackageInfo.versionCode)
      )
      packageDownloaderMock?.downloadCalled?.remove(packageName)
      uninstallCalled.remove(packageName)
    }
  }

  override fun uninstall(packageName: String): Flow<Int> {
    if (!uninstallCalled.add(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    return flow {
      progressFlow(delay, lock)
      packageInfoRepositoryMock?.update(packageName, null)
      installCalled.remove(packageName)
    }
  }

  override fun cancel(packageName: String): Boolean {
    if (cancelCalled.contains(packageName)) {
      throw IllegalStateException("Duplicate call for $packageName")
    }
    cancelCalled.add(packageName)
    return if (progressFlow == cancellingFlow) {
      scope.launch { lock.send(Unit) }
      true
    } else {
      false
    }
  }
}

class FreeSpaceCheckerMock : FreeSpaceChecker {
  internal var willMissSpace: Long = 0
  internal var missingSpace: Long = 0

  override fun missingSpace(appSize: Long, scheduledSize: Long?): Long =
    scheduledSize?.let { willMissSpace } ?: missingSpace
}

class NetworkConnectionMock : NetworkConnection {

  internal var currentState = NetworkConnection.State.UNMETERED

  private var listenerSet = false

  private var listener: (NetworkConnection.State) -> Unit = {}

  override val state: NetworkConnection.State get() = currentState

  override fun setOnChangeListener(onChange: (NetworkConnection.State) -> Unit) {
    if (listenerSet) throw java.lang.IllegalStateException("Duplicate call")
    listenerSet = true
    listener = onChange
  }

  internal fun update(state: NetworkConnection.State) {
    currentState = state
    listener(state)
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T> Flow<T>.collectAsync(scope: TestScope): List<T> {
  val result = mutableListOf<T>()
  scope.launch {
    withTimeoutOrNull(9.hours) {
      collect { result.add(it) }
    }
  }
  scope.runCurrent()
  return result
}
