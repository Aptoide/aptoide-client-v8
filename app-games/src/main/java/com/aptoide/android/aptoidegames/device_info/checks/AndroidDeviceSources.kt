package com.aptoide.android.aptoidegames.device_info.checks

import android.content.pm.PackageManager
import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

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
