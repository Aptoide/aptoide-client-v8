package com.aptoide.android.aptoidegames.device_info.checks

import android.app.ActivityManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.Reader

class AndroidDeviceFiles : DeviceFiles {
  override fun exists(path: String): Boolean = runCatching { File(path).exists() }
    .getOrDefault(false)

  override fun isWritable(path: String): Boolean = runCatching { File(path).canWrite() }
    .getOrDefault(false)
}

/** Reads properties through the `getprop` command, as the checks did before AND-956. */
class GetpropSystemProperties : SystemProperties {
  override fun get(name: String): String? = runCatching {
    val process = Runtime.getRuntime().exec(arrayOf("getprop", name))
    try {
      BufferedReader(InputStreamReader(process.inputStream)).use { it.readLine() }
        .also { process.waitFor() }
    } finally {
      process.destroy()
    }
  }.getOrNull()
}

class PackageManagerInstalledPackages(
  private val packageManager: PackageManager,
) : InstalledPackages {
  override fun isInstalled(packageName: String): Boolean = try {
    packageManager.getPackageInfo(packageName, 0)
    true
  } catch (_: PackageManager.NameNotFoundException) {
    false
  }
}

fun pathDirectories(): List<String> = System.getenv("PATH")?.split(":") ?: emptyList()

fun currentBuildInfo(): BuildInfo = BuildInfo(
  fingerprint = Build.FINGERPRINT ?: "",
  model = Build.MODEL ?: "",
  manufacturer = Build.MANUFACTURER ?: "",
  brand = Build.BRAND ?: "",
  device = Build.DEVICE ?: "",
  product = Build.PRODUCT ?: "",
  hardware = Build.HARDWARE ?: "",
  board = Build.BOARD ?: "",
  bootloader = Build.BOOTLOADER ?: "",
  tags = Build.TAGS,
)

/** Tries the system class loader, then the app's own (whose parents include the boot one). */
class ClassLoaderProbe : ClassProbe {
  override fun isLoadable(className: String): Boolean =
    listOfNotNull(ClassLoader.getSystemClassLoader(), javaClass.classLoader).any { loader ->
      runCatching { Class.forName(className, false, loader) }.isSuccess
    }
}

class ThreadCallStack : CallStack {
  override fun classNames(): List<String> = Throwable().stackTrace.map { it.className }
}

/** Reads `/proc/self/maps`, at most [maxChars] of it, so a huge file cannot stall the check. */
class ProcSelfMaps(private val maxChars: Int = MAX_MAPS_CHARS) : ProcessMaps {
  override fun mappedPaths(): Set<String> =
    File("/proc/self/maps").bufferedReader().use { readMappedPaths(it, maxChars) }

  private companion object {
    // An app's maps file is usually well under 1 MB.
    const val MAX_MAPS_CHARS = 2_000_000
  }
}

/** The path column of each whole line read within [maxChars]; anonymous regions are skipped. */
internal fun readMappedPaths(reader: Reader, maxChars: Int): Set<String> {
  val lines = reader as? BufferedReader ?: BufferedReader(reader)
  val paths = mutableSetOf<String>()
  var read = 0
  while (true) {
    val line = lines.readLine() ?: break
    read += line.length + 1
    if (read > maxChars) break
    val pathStart = line.indexOf('/')
    if (pathStart >= 0) paths += line.substring(pathStart)
  }
  return paths
}

class AndroidDebuggerState : DebuggerState {
  override fun isDebuggerConnected(): Boolean = Debug.isDebuggerConnected()
  override fun isWaitingForDebugger(): Boolean = Debug.waitingForDebugger()
}

class AndroidTestHarnessState : TestHarnessState {
  @Suppress("DEPRECATION")
  override fun isRunningInTestHarness(): Boolean = ActivityManager.isRunningInTestHarness() ||
    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ActivityManager.isRunningInUserTestHarness())

  override fun isUserAMonkey(): Boolean = ActivityManager.isUserAMonkey()
}
