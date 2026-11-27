package com.aptoide.android.aptoidegames.device_info.checks

import android.app.ActivityManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.Reader
import java.util.concurrent.TimeUnit

class AndroidDeviceFiles : DeviceFiles {
  override fun exists(path: String): Boolean = runCatching { File(path).exists() }
    .getOrDefault(false)

  override fun isWritable(path: String): Boolean = runCatching { File(path).canWrite() }
    .getOrDefault(false)
}

/**
 * Reads properties through the `getprop` command. A getprop that has not finished within
 * [timeoutMillis] is killed and read as unset, so one stuck process cannot hold up the checks.
 */
class GetpropSystemProperties(
  private val timeoutMillis: Long = GETPROP_TIMEOUT_MILLIS,
  private val start: (String) -> Process = { Runtime.getRuntime().exec(arrayOf("getprop", it)) },
) : SystemProperties {

  override fun get(name: String): String? {
    val process = try {
      start(name)
    } catch (_: Exception) {
      return null
    }
    return try {
      // getprop prints one short line, far below the pipe buffer: waiting before reading is safe.
      if (process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS)) {
        BufferedReader(InputStreamReader(process.inputStream)).use { it.readLine() }
      } else {
        null
      }
    } catch (_: Exception) {
      null
    } finally {
      process.destroy()
    }
  }

  private companion object {
    const val GETPROP_TIMEOUT_MILLIS = 2_000L
  }
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

/** Streams `/proc/self/maps` line by line; nothing of it is kept in memory. */
class ProcSelfMaps : ProcessMaps {
  override fun anyMappedPath(predicate: (String) -> Boolean): Boolean =
    File("/proc/self/maps").bufferedReader().use { anyMappedPath(it, predicate = predicate) }
}

// An app maps a few thousand regions; the bound only stops a runaway read. Injected libraries
// tend to be mapped last, so it is far above any real file to let the scan reach its end.
private const val MAX_MAPS_LINES = 200_000

// A maps line is a fixed prefix plus a path of at most PATH_MAX (4096) characters.
private const val MAX_MAPS_LINE_LENGTH = 8_192

/**
 * Whether the path column of a maps line matches [predicate], reading line by line and stopping
 * at the first match. At most [maxLines] lines are read; a line longer than [maxLineLength] is
 * cut there. Anonymous regions (no path) are skipped.
 */
internal fun anyMappedPath(
  reader: Reader,
  maxLines: Int = MAX_MAPS_LINES,
  maxLineLength: Int = MAX_MAPS_LINE_LENGTH,
  predicate: (String) -> Boolean,
): Boolean {
  val input = reader as? BufferedReader ?: BufferedReader(reader)
  val line = StringBuilder()
  var lines = 0
  while (lines < maxLines) {
    line.setLength(0)
    var char = input.read()
    if (char == -1) return false
    while (char != -1 && char != '\n'.code) {
      if (line.length < maxLineLength) line.append(char.toChar())
      char = input.read()
    }
    lines++
    val pathStart = line.indexOf("/")
    if (pathStart >= 0 && predicate(line.substring(pathStart))) return true
  }
  return false
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
