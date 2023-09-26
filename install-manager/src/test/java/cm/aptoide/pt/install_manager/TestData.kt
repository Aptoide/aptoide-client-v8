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
internal fun createBuilderWithMocks(scope: TestScope) = TestBuilder(scope)

@ExperimentalCoroutinesApi
class TestBuilder(scope: TestScope) : InstallManager.IBuilder {
  override var packageInfoRepository: PackageInfoRepository = PackageInfoRepositoryMock()
  override var packageDownloader: PackageDownloader = PackageDownloaderMock()
  override var packageInstaller: PackageInstaller = PackageInstallerMock()
  override var taskInfoRepository: TaskInfoRepository = TaskInfoRepositoryMock()
  override var scope: CoroutineScope = scope
  override var clock: Clock = Clock { scope.currentTime }
}

internal fun savedPackageAppInfo() = Stream.of(
  Arguments.arguments(
    "Not installed for the 'package0'",
    "package0",
    mapOf<String, PackageInfo>(),
  ),
  Arguments.arguments(
    "Installed for the 'package1'",
    "package2",
    mapOf("package2" to installedInfo("package2")),
  ),
)

@Suppress("DEPRECATION")
internal fun installedInfo(packageName: String, vc: Int = 1) = PackageInfo().apply {
  this.packageName = packageName
  versionName = "1.0.0"
  versionCode = vc
}

internal val installInfo = InstallPackageInfo(
  versionCode = 2,
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
  versionCode = 1,
  downloadSize = Long.MIN_VALUE,
  installationFiles = setOf()
)

// Crashes on duplicated calls for optimization reasons
internal class PackageInfoRepositoryMock(
  initial: Map<String, PackageInfo> = emptyMap(),
  private val letItCrash: Boolean = false,
  private val speed: Speed = Speed.RANDOM,
) : PackageInfoRepository {
  private var allCalled = false
  val info: MutableMap<String, PackageInfo> =
    mutableMapOf<String, PackageInfo>().apply { putAll(initial) }
  var listener: suspend (String) -> Unit = {}

  override suspend fun getAll(): Set<PackageInfo> {
    wait()
    if (allCalled) throw java.lang.IllegalStateException("Duplicate call")
    if (letItCrash) throw RuntimeException("Problem!")
    allCalled = true
    return info.values.toSet()
  }

  override suspend fun get(packageName: String): PackageInfo? {
    wait()
    if (letItCrash) throw RuntimeException("Problem!")
    return info[packageName]
  }

  override fun setOnChangeListener(onChange: suspend (String) -> Unit) {
    listener = onChange
  }

  suspend fun update(pn: String, pi: PackageInfo?) {
    pi?.let { info[pn] = it } ?: info.remove(pn)
    listener.invoke(pn)
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
  val info: MutableSet<TaskInfo> = mutableSetOf<TaskInfo>().apply { addAll(initial) }
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
  private val waitForCancel: Boolean = false,
  private val letItCrash: Boolean = false,
  private val letItAbort: Boolean = false,
  private val speed: Speed = Speed.RANDOM,
) : PackageDownloader {
  private val downloaded: MutableSet<String> = mutableSetOf()
  private val downloading: MutableSet<String> = mutableSetOf()
  private val cancelled: MutableList<String> = mutableListOf()
  private val suspendLock = SuspendLock()

  override suspend fun download(
    packageName: String,
    forceDownload: Boolean,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    if (!downloaded.add(packageName)) throw java.lang.IllegalStateException("Duplicate call for $packageName")
    return flow {
      downloading.add(packageName)
      for (it in 0..4) {
        wait()
        if (it == 2 && waitForCancel) suspendLock.await()
        if (cancelled.contains(packageName)) throw CancellationException("Cancelled")
        emit(it * 25)
        if (it > 1 && letItCrash) throw RuntimeException("Problem!")
        if (it > 1 && letItAbort) throw AbortException("No go!")
      }
      downloading.remove(packageName)
    }
  }

  override fun cancel(packageName: String): Boolean {
    if (cancelled.contains(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    cancelled.add(packageName)
    suspendLock.yield()
    return downloading.contains(packageName)
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
  private val waitForCancel: Boolean = false,
  private val letItCrash: Boolean = false,
  private val letItAbort: Boolean = false,
  private val speed: Speed = Speed.RANDOM,
) : PackageInstaller {
  private val installed: MutableSet<String> = mutableSetOf()
  private val uninstalled: MutableSet<String> = mutableSetOf()
  private val installing: MutableSet<String> = mutableSetOf()
  private val uninstalling: MutableSet<String> = mutableSetOf()
  private val cancelled: MutableSet<String> = mutableSetOf()
  private val suspendLock = SuspendLock()

  override suspend fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<Int> {
    if (!installed.add(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    return flow {
      installing.add(packageName)
      for (it in 0..4) {
        wait()
        if (it > 1 && waitForCancel) suspendLock.await()
        if (cancelled.contains(packageName)) throw CancellationException("Cancelled")
        emit(it * 25)
        if (it > 1 && letItCrash) throw RuntimeException("Problem!")
        if (it > 1 && letItAbort) throw AbortException("No go!")
      }
      installing.remove(packageName)
      uninstalled.remove(packageName)
    }
  }

  override suspend fun uninstall(packageName: String): Flow<Int> {
    if (!uninstalled.add(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    return flow {
      uninstalling.add(packageName)
      for (it in 0..4) {
        wait()
        if (it > 1 && waitForCancel) suspendLock.await()
        if (cancelled.contains(packageName)) throw CancellationException("Cancelled")
        emit(it * 25)
        if (it > 1 && letItCrash) throw RuntimeException("Problem!")
        if (it > 1 && letItAbort) throw AbortException("No go!")
      }
      uninstalling.remove(packageName)
      installed.remove(packageName)
    }
  }

  override fun cancel(packageName: String): Boolean {
    if (cancelled.contains(packageName)) throw IllegalStateException("Duplicate call for $packageName")
    cancelled.add(packageName)
    suspendLock.yield()
    return uninstalling.contains(packageName) or installing.contains(packageName)
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

class SuspendLock(private val channel: Channel<Unit> = Channel(0)) {
  suspend fun await() = channel.receive()
  fun yield() = channel.trySend(Unit)
}
