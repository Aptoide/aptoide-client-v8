package com.aptoide.android.aptoidegames.device_info

import android.content.pm.PackageManager
import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class to detect if the device is an emulator or rooted.
 * Uses multiple detection techniques for improved accuracy.
 */
@Singleton
class DeviceSecurityChecker @Inject constructor(
  private val packageManager: PackageManager,
) {

  private val isDeviceCompromised: Boolean by lazy {
    isEmulator() || isRooted()
  }

  /**
   * Checks if the device is likely an emulator.
   * Uses multiple heuristics to detect various emulator types (Android Studio, Genymotion, BlueStacks, etc.)
   *
   * @return true if the device appears to be an emulator, false otherwise
   */
  fun isEmulator(): Boolean {
    return checkBuildProperties() ||
      checkHardwareProperties() ||
      checkEmulatorFiles() ||
      checkUnknownBoard()
  }

  /**
   * Checks if the device is likely rooted.
   * Uses multiple detection methods for comprehensive root detection.
   *
   * @return true if the device appears to be rooted, false otherwise
   */
  fun isRooted(): Boolean {
    return checkRootBinaries() ||
      checkSuBinary() ||
      checkRootManagementApps() ||
      checkDangerousProps() ||
      checkRWPaths() ||
      checkBuildTags()
  }

  // ==================== Emulator Detection ====================

  @Suppress("DEPRECATION")
  private fun checkBuildProperties(): Boolean {
    return (Build.FINGERPRINT.startsWith("generic", ignoreCase = true) ||
      Build.FINGERPRINT.startsWith("unknown", ignoreCase = true) ||
      Build.FINGERPRINT.contains("emulator", ignoreCase = true) ||
      Build.FINGERPRINT.contains("sdk_gphone", ignoreCase = true) ||
      Build.MODEL.contains("google_sdk", ignoreCase = true) ||
      Build.MODEL.contains("emulator", ignoreCase = true) ||
      Build.MODEL.contains("droid4x", ignoreCase = true) ||
      Build.MODEL.equals("sdk", ignoreCase = true) ||
      Build.MODEL.contains("Android SDK built for", ignoreCase = true) ||
      Build.MANUFACTURER.contains("genymotion", ignoreCase = true) ||
      Build.BRAND.startsWith("generic", ignoreCase = true) ||
      Build.DEVICE.startsWith("generic", ignoreCase = true) ||
      Build.PRODUCT.equals("sdk", ignoreCase = true) ||
      Build.PRODUCT.contains("sdk_gphone", ignoreCase = true) ||
      Build.PRODUCT.contains("vbox", ignoreCase = true) ||
      Build.PRODUCT.contains("emulator", ignoreCase = true) ||
      Build.PRODUCT.contains("simulator", ignoreCase = true) ||
      Build.HARDWARE.contains("goldfish", ignoreCase = true) ||
      Build.HARDWARE.contains("ranchu", ignoreCase = true) ||
      Build.HARDWARE.contains("nox", ignoreCase = true) ||
      Build.HARDWARE.contains("vbox", ignoreCase = true) ||
      Build.BOARD.contains("nox", ignoreCase = true) ||
      Build.BOOTLOADER.contains("nox", ignoreCase = true))
  }

  private fun checkHardwareProperties(): Boolean {
    val qemuProps = listOf(
      "ro.kernel.qemu",
      "ro.kernel.android.qemud",
      "qemu.hw.mainkeys",
      "ro.kernel.qemu.gles"
    )

    return qemuProps.any { prop ->
      try {
        val process = Runtime.getRuntime().exec(arrayOf("getprop", prop))
        val result = BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
          val value = reader.readLine()
          !value.isNullOrEmpty() && value != "0"
        }
        process.waitFor()
        process.destroy()
        result
      } catch (_: Exception) {
        false
      }
    }
  }

  private fun checkEmulatorFiles(): Boolean {
    val emulatorFiles = listOf(
      "/dev/socket/genyd",
      "/dev/socket/baseband_genyd",
      "/dev/socket/qemud",
      "/dev/qemu_pipe",
      "/system/lib/libc_malloc_debug_qemu.so",
      "/sys/qemu_trace",
      "/system/bin/qemu-props",
      "/dev/goldfish_pipe",
      "/system/lib/libdvm_em.so",
      "/system/bin/microvirtd",
      "/system/bin/ldinit",
      "/system/bin/ldmountsf",
      "/system/lib/ldutils.so",
      "/system/bin/nox-prop",
      "/system/bin/nox-vbox-sf",
      "/system/lib/libnoxd.so",
      "/system/lib/libnoxspeedup.so"
    )

    return emulatorFiles.any { path ->
      try {
        File(path).exists()
      } catch (_: Exception) {
        false
      }
    }
  }

  private fun checkUnknownBoard(): Boolean {
    return Build.BOARD.isEmpty() || Build.BOARD.equals("unknown", ignoreCase = true)
  }

  // ==================== Root Detection ====================

  private fun checkRootBinaries(): Boolean {
    val rootBinaries = listOf(
      "/system/app/Superuser.apk",
      "/sbin/su",
      "/system/bin/su",
      "/system/xbin/su",
      "/data/local/xbin/su",
      "/data/local/bin/su",
      "/system/sd/xbin/su",
      "/system/bin/failsafe/su",
      "/data/local/su",
      "/su/bin/su",
      "/system/xbin/busybox",
      "/sbin/.magisk",
      "/sbin/.core",
      "/data/adb/magisk",
      "/system/etc/init.d",
      "/system/bin/.ext",
      "/system/xbin/daemonsu",
      "/system/etc/.has_su_daemon",
      "/system/etc/.installed_su_daemon",
      "/dev/.superuser.mounted"
    )

    return rootBinaries.any { path ->
      try {
        File(path).exists()
      } catch (_: Exception) {
        false
      }
    }
  }

  private fun checkSuBinary(): Boolean {
    val paths = System.getenv("PATH")?.split(":") ?: emptyList()
    return paths.any { path ->
      try {
        File("$path/su").exists()
      } catch (_: Exception) {
        false
      }
    }
  }

  private fun checkRootManagementApps(): Boolean {
    val rootApps = listOf(
      "com.topjohnwu.magisk",
      "com.koushikdutta.superuser",
      "com.thirdparty.superuser",
      "eu.chainfire.supersu",
      "com.noshufou.android.su",
      "com.noshufou.android.su.elite",
      "com.yellowes.su",
      "com.kingroot.kinguser",
      "com.kingo.root",
      "com.smedialink.oneclickroot",
      "com.zhiqupk.root.global",
      "com.alephzain.framaroot",
      "com.zachspong.temprootremovejb",
      "com.ramdroid.appquarantine",
      "com.amphoras.hidemyroot",
      "com.amphoras.hidemyrootadfree",
      "com.formyhm.hiderootPremium",
      "com.formyhm.hideroot",
      "me.phh.superuser",
      "eu.chainfire.supersu.pro",
      "com.kingouser.com"
    )

    return rootApps.any { packageName ->
      try {
        packageManager.getPackageInfo(packageName, 0)
        true
      } catch (_: PackageManager.NameNotFoundException) {
        false
      }
    }
  }

  private fun checkDangerousProps(): Boolean {
    val dangerousProps = mapOf(
      "ro.debuggable" to "1",
      "ro.secure" to "0",
      "service.adb.root" to "1"
    )

    return dangerousProps.any { (prop, dangerousValue) ->
      try {
        val process = Runtime.getRuntime().exec(arrayOf("getprop", prop))
        val result = BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
          val value = reader.readLine()
          value == dangerousValue
        }
        process.waitFor()
        process.destroy()
        result
      } catch (_: Exception) {
        false
      }
    }
  }

  private fun checkRWPaths(): Boolean {
    val pathsToCheck = listOf(
      "/system",
      "/system/bin",
      "/system/sbin",
      "/system/xbin",
      "/vendor/bin",
      "/sbin",
      "/etc"
    )

    return pathsToCheck.any { path ->
      try {
        val file = File(path)
        file.exists() && file.canWrite()
      } catch (_: Exception) {
        false
      }
    }
  }

  private fun checkBuildTags(): Boolean {
    val buildTags = Build.TAGS
    return buildTags != null && buildTags.contains("test-keys")
  }

  fun isCompromisedDevice(): Boolean = isDeviceCompromised
}
