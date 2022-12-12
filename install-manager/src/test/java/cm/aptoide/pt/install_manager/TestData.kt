package cm.aptoide.pt.install_manager

import cm.aptoide.pt.install_manager.dto.*
import cm.aptoide.pt.install_manager.repository.AppInfoRepository
import cm.aptoide.pt.install_manager.repository.TaskInfoRepository
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import org.junit.jupiter.params.provider.Arguments
import java.util.stream.Stream
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@ExperimentalCoroutinesApi
internal fun createBuilderWithMocks(scope: TestScope) = InstallManager.Builder<String>()
  .apply {
    this.appInfoRepository = AppInfoRepositoryMock()
    this.taskInfoRepository = TaskInfoRepositoryMock()
    this.packageDownloader = PackageDownloaderMock()
    this.packageInstaller = PackageInstallerMock()
    this.context = scope.coroutineContext
    clock = Clock { scope.currentTime }
  }

internal fun savedPackageAppInfo() = Stream.of(
  Arguments.arguments(
    "No info for the 'package0'",
    "package0",
    mapOf<String, AppInfo<String>>(),
  ),
  Arguments.arguments(
    "Not installed without details for the 'package1'",
    "package1",
    mapOf("package1" to uninstalledWithoutDetails),
  ),
  Arguments.arguments(
    "Not installed with details for the 'package2'",
    "package2",
    mapOf("package2" to uninstalledWithDetails),
  ),
  Arguments.arguments(
    "Installed without details for the 'package3'",
    "package3",
    mapOf("package3" to installedWithoutDetails),
  ),
  Arguments.arguments(
    "Installed with details for the 'package'",
    "package4",
    mapOf("package4" to installedWithDetails),
  )
)

internal val uninstalledWithoutDetails = AppInfo<String>(packageName = "package1")

internal val uninstalledWithDetails = AppInfo(
  packageName = "package2",
  installedVersion = null,
  details = "details2"
)

internal val installedWithoutDetails = AppInfo<String>(
  packageName = "package3",
  installedVersion = Version("1.0.0", 1)
)

internal val installedWithDetails = AppInfo(
  packageName = "package4",
  installedVersion = Version(versionName = "1.0.0", versionCode = 1),
  details = "details4"
)

internal val installInfo = InstallPackageInfo(
  version = Version(versionName = "2", versionCode = 2),
  downloadSize = 12345,
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
    ), InstallationFile(
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
  version = installedWithDetails.installedVersion!!,
  downloadSize = Long.MIN_VALUE,
  installationFiles = setOf()
)

// Crashes on duplicated calls for optimization reasons
internal class AppInfoRepositoryMock(
  initial: Map<String, AppInfo<String>> = emptyMap(),
  private val letItCrash: Boolean = false,
  private val speed: Speed = Speed.RANDOM,
) : AppInfoRepository<String> {
  val info: MutableMap<String, AppInfo<String>> = initial.toMutableMap()
  private var allCalled = false
  private val getCalledFor: MutableSet<String> = mutableSetOf()
  private val saveCalledFor: MutableSet<AppInfo<String>> = mutableSetOf()
  private val removeCalledFor: MutableSet<String> = mutableSetOf()

  override suspend fun getAll(): Set<AppInfo<String>> {
    wait()
    if (allCalled) throw java.lang.IllegalStateException("Duplicate call")
    if (letItCrash) throw RuntimeException("Problem!")
    allCalled = true
    return info.values.toSet()
  }

  override suspend fun get(packageName: String): AppInfo<String>? {
    wait()
    if (getCalledFor.contains(packageName)) throw java.lang.IllegalStateException("Duplicate call for $packageName")
    if (letItCrash) throw RuntimeException("Problem!")
    getCalledFor.add(packageName)
    return info[packageName]
  }

  override suspend fun save(appInfo: AppInfo<String>) {
    wait()
    if (saveCalledFor.contains(appInfo)) throw java.lang.IllegalStateException("Duplicate call for ${appInfo.packageName}")
    if (letItCrash) throw RuntimeException("Problem!")
    allCalled = false
    getCalledFor.remove(appInfo.packageName)
    saveCalledFor.add(appInfo)
    removeCalledFor.remove(appInfo.packageName)
    info[appInfo.packageName] = appInfo
  }

  override suspend fun remove(packageName: String) {
    wait()
    if (removeCalledFor.contains(packageName)) throw java.lang.IllegalStateException("Duplicate call for $packageName")
    if (letItCrash) throw RuntimeException("Problem!")
    allCalled = false
    getCalledFor.remove(packageName)
    saveCalledFor.removeIf { it.packageName == packageName }
    removeCalledFor.add(packageName)
    info.remove(packageName)
  }

  // Delay to emulate real duration
  private suspend fun wait() = delay(
    when (speed) {
      Speed.SLOW -> 2.toLong().seconds
      Speed.NORMAL -> 1.toLong().seconds
      Speed.FAST -> 20.toLong().milliseconds
      Speed.RANDOM -> Random.nextLong(LongRange(20, 2000)).milliseconds
    }
  )
}

// Crashes on duplicated calls for optimization reasons
internal class TaskInfoRepositoryMock(
  initial: Set<TaskInfo> = emptySet(),
  private val letItCrash: Boolean = false,
  private val speed: Speed = Speed.RANDOM,
) : TaskInfoRepository {
  val info: MutableSet<TaskInfo> = initial.toMutableSet()
  private var allCalled = false
  private val saveCalledFor: MutableSet<TaskInfo> = mutableSetOf()
  private val removeAllCalledFor: MutableSet<String> = mutableSetOf()

  override suspend fun getAll(): Set<TaskInfo> {
    wait()
    if (allCalled) throw java.lang.IllegalStateException("Duplicate call")
    if (letItCrash) throw RuntimeException("Problem!")
    allCalled = true
    return info
  }

  override suspend fun saveJob(taskInfo: TaskInfo) {
    wait()
    if (saveCalledFor.contains(taskInfo)) throw java.lang.IllegalStateException("Duplicate call for ${taskInfo.packageName}")
    if (letItCrash) throw RuntimeException("Problem!")
    allCalled = false
    saveCalledFor.add(taskInfo)
    removeAllCalledFor.remove(taskInfo.packageName)
    info.add(taskInfo)
  }

  override suspend fun removeAll(packageName: String) {
    wait()
    if (removeAllCalledFor.contains(packageName)) throw java.lang.IllegalStateException("Duplicate call for $packageName")
    if (letItCrash) throw RuntimeException("Problem!")
    allCalled = false
    removeAllCalledFor.add(packageName)
    saveCalledFor.removeIf { it.packageName == packageName }
    info.removeAll { it.packageName == packageName }
  }

  // Delay to emulate real duration
  private suspend fun wait() = delay(
    when (speed) {
      Speed.SLOW -> 2.toLong().seconds
      Speed.NORMAL -> 1.toLong().seconds
      Speed.FAST -> 20.toLong().milliseconds
      Speed.RANDOM -> Random.nextLong(LongRange(20, 2000)).milliseconds
    }
  )
}

// Crashes on duplicated calls for optimization reasons
internal class PackageDownloaderMock(
  initial: Set<String> = emptySet(),
  private val waitForCancel: Boolean = false,
  private val letItCrash: Boolean = false,
  private val speed: Speed = Speed.RANDOM,
) : PackageDownloader {
  private val info: MutableSet<String> = initial.toMutableSet()
  private val cancelled: MutableList<String> = initial.toMutableList()
  private val blocker = Blocker()

  override suspend fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ): Flow<Int> {
    if (!info.add(packageName)) throw java.lang.IllegalStateException("Duplicate call for $packageName")
    return flow {
      for (it in 0..4) {
        wait()
        if (it > 1 && waitForCancel) blocker.await()
        if (cancelled.contains(packageName)) throw CancellationException("Cancelled")
        emit(it * 25)
        if (it > 1 && letItCrash) throw RuntimeException("Problem!")
      }
    }
  }

  override fun cancel(packageName: String) {
    if (cancelled.contains(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    cancelled.add(packageName)
    blocker.yield()
  }

  // Delay to emulate real duration
  private suspend fun wait() = delay(
    when (speed) {
      Speed.SLOW -> 4.toLong().minutes
      Speed.NORMAL -> 1.toLong().minutes
      Speed.FAST -> 12.toLong().seconds
      Speed.RANDOM -> Random.nextLong(LongRange(12, 240)).seconds
    }
  )
}

// Crashes on duplicated calls for optimization reasons
internal class PackageInstallerMock(
  initial: Set<String> = emptySet(),
  private val waitForCancel: Boolean = false,
  private val letItCrash: Boolean = false,
  private val speed: Speed = Speed.RANDOM,
) : PackageInstaller {
  private val info: MutableSet<String> = initial.toMutableSet()
  private val cancelled: MutableSet<String> = mutableSetOf()
  private val blocker = Blocker()

  override suspend fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ): Flow<Int> {
    if (!info.add(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    return flow {
      for (it in 0..4) {
        wait()
        if (it > 1 && waitForCancel) blocker.await()
        if (cancelled.contains(packageName)) throw CancellationException("Cancelled")
        emit(it * 25)
        if (it > 1 && letItCrash) throw RuntimeException("Problem!")
      }
    }
  }

  override suspend fun uninstall(packageName: String): Flow<Int> {
    if (!info.remove(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    return flow {
      for (it in 0..4) {
        wait()
        if (it > 1 && waitForCancel) blocker.await()
        if (cancelled.contains(packageName)) throw CancellationException("Cancelled")
        emit(it * 25)
        if (it > 1 && letItCrash) throw RuntimeException("Problem!")
      }
    }
  }

  override fun cancel(packageName: String) {
    if (cancelled.contains(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    cancelled.add(packageName)
    blocker.yield()
  }

  // Delay to emulate real duration
  private suspend fun wait() = delay(
    when (speed) {
      Speed.SLOW -> 24.toLong().seconds
      Speed.NORMAL -> 16.toLong().seconds
      Speed.FAST -> 4.toLong().seconds
      Speed.RANDOM -> Random.nextLong(LongRange(4, 24)).seconds
    }
  )
}

enum class Speed {
  SLOW,
  NORMAL,
  FAST,
  RANDOM
}

@JvmInline
value class Blocker(private val channel: Channel<Unit> = Channel(0)) {
  suspend fun await() = channel.receive()
  fun yield() = channel.trySend(Unit)
}