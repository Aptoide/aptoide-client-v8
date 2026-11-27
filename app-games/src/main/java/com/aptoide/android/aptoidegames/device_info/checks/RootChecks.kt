package com.aptoide.android.aptoidegames.device_info.checks

/** Known su, Superuser, Magisk, busybox and su-daemon paths exist. */
class RootFilesCheck(private val files: DeviceFiles) : DeviceCheck {
  override val name = "root_files"
  override val group = DeviceCheckGroup.ROOT

  override fun fired(): Boolean = ROOT_FILES.anySafely(files::exists)

  private companion object {
    val ROOT_FILES = listOf(
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
  }
}

/** An `su` binary sits in one of the `PATH` directories. */
class SuBinaryCheck(
  private val pathDirectories: () -> List<String>,
  private val files: DeviceFiles,
) : DeviceCheck {
  override val name = "su_binary"
  override val group = DeviceCheckGroup.ROOT

  override fun fired(): Boolean = pathDirectories().anySafely { files.exists("$it/su") }
}

/** A root manager or root-hiding app is installed. */
class RootAppsCheck(private val packages: InstalledPackages) : DeviceCheck {
  override val name = "root_apps"
  override val group = DeviceCheckGroup.ROOT

  override fun fired(): Boolean = ROOT_APPS.anySafely(packages::isInstalled)

  private companion object {
    val ROOT_APPS = listOf(
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
  }
}

/** The system is debuggable or insecure: ro.debuggable=1, ro.secure=0 or service.adb.root=1. */
class DangerousPropsCheck(private val properties: SystemProperties) : DeviceCheck {
  override val name = "dangerous_props"
  override val group = DeviceCheckGroup.ROOT

  override fun fired(): Boolean =
    DANGEROUS_PROPS.entries.anySafely { (prop, dangerous) -> properties.get(prop) == dangerous }

  private companion object {
    val DANGEROUS_PROPS = mapOf(
      "ro.debuggable" to "1",
      "ro.secure" to "0",
      "service.adb.root" to "1"
    )
  }
}

/** A system folder that should be read-only can be written to. */
class RwSystemPathsCheck(private val files: DeviceFiles) : DeviceCheck {
  override val name = "rw_system_paths"
  override val group = DeviceCheckGroup.ROOT

  override fun fired(): Boolean =
    SYSTEM_PATHS.anySafely { files.exists(it) && files.isWritable(it) }

  private companion object {
    val SYSTEM_PATHS = listOf(
      "/system",
      "/system/bin",
      "/system/sbin",
      "/system/xbin",
      "/vendor/bin",
      "/sbin",
      "/etc"
    )
  }
}

/** The ROM is signed with the public AOSP test keys (`Build.TAGS` contains "test-keys"). */
class TestKeysCheck(private val build: BuildInfo) : DeviceCheck {
  override val name = "test_keys"
  override val group = DeviceCheckGroup.ROOT

  override fun fired(): Boolean = build.tags?.contains("test-keys") == true
}
