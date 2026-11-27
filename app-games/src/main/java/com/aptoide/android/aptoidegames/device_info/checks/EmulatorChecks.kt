package com.aptoide.android.aptoidegames.device_info.checks

/** Build fields carry the names of known emulators (Android Studio, Genymotion, Nox, ...). */
class EmulatorBuildCheck(private val build: BuildInfo) : DeviceCheck {
  override val name = "emulator_build"
  override val group = DeviceCheckGroup.EMULATOR

  override fun fired(): Boolean = EMULATOR_BUILD_RULES.any { it.matches(build) }
}

internal enum class BuildField(val read: (BuildInfo) -> String) {
  FINGERPRINT({ it.fingerprint }),
  MODEL({ it.model }),
  MANUFACTURER({ it.manufacturer }),
  BRAND({ it.brand }),
  DEVICE({ it.device }),
  PRODUCT({ it.product }),
  HARDWARE({ it.hardware }),
  BOARD({ it.board }),
  BOOTLOADER({ it.bootloader }),
}

internal enum class BuildMatch { STARTS_WITH, CONTAINS, EQUALS }

/** One emulator pattern on one Build field. Every comparison ignores case. */
internal data class BuildRule(val field: BuildField, val match: BuildMatch, val value: String) {
  fun matches(build: BuildInfo): Boolean {
    val actual = field.read(build)
    return when (match) {
      BuildMatch.STARTS_WITH -> actual.startsWith(value, ignoreCase = true)
      BuildMatch.CONTAINS -> actual.contains(value, ignoreCase = true)
      BuildMatch.EQUALS -> actual.equals(value, ignoreCase = true)
    }
  }
}

internal val EMULATOR_BUILD_RULES = listOf(
  BuildRule(BuildField.FINGERPRINT, BuildMatch.STARTS_WITH, "generic"),
  BuildRule(BuildField.FINGERPRINT, BuildMatch.STARTS_WITH, "unknown"),
  BuildRule(BuildField.FINGERPRINT, BuildMatch.CONTAINS, "emulator"),
  BuildRule(BuildField.FINGERPRINT, BuildMatch.CONTAINS, "sdk_gphone"),
  BuildRule(BuildField.MODEL, BuildMatch.CONTAINS, "google_sdk"),
  BuildRule(BuildField.MODEL, BuildMatch.CONTAINS, "emulator"),
  BuildRule(BuildField.MODEL, BuildMatch.CONTAINS, "droid4x"),
  BuildRule(BuildField.MODEL, BuildMatch.EQUALS, "sdk"),
  BuildRule(BuildField.MODEL, BuildMatch.CONTAINS, "Android SDK built for"),
  BuildRule(BuildField.MANUFACTURER, BuildMatch.CONTAINS, "genymotion"),
  BuildRule(BuildField.BRAND, BuildMatch.STARTS_WITH, "generic"),
  BuildRule(BuildField.DEVICE, BuildMatch.STARTS_WITH, "generic"),
  BuildRule(BuildField.PRODUCT, BuildMatch.EQUALS, "sdk"),
  BuildRule(BuildField.PRODUCT, BuildMatch.CONTAINS, "sdk_gphone"),
  BuildRule(BuildField.PRODUCT, BuildMatch.CONTAINS, "vbox"),
  BuildRule(BuildField.PRODUCT, BuildMatch.CONTAINS, "emulator"),
  BuildRule(BuildField.PRODUCT, BuildMatch.CONTAINS, "simulator"),
  BuildRule(BuildField.HARDWARE, BuildMatch.CONTAINS, "goldfish"),
  BuildRule(BuildField.HARDWARE, BuildMatch.CONTAINS, "ranchu"),
  BuildRule(BuildField.HARDWARE, BuildMatch.CONTAINS, "nox"),
  BuildRule(BuildField.HARDWARE, BuildMatch.CONTAINS, "vbox"),
  BuildRule(BuildField.BOARD, BuildMatch.CONTAINS, "nox"),
  BuildRule(BuildField.BOOTLOADER, BuildMatch.CONTAINS, "nox"),
)

/** A QEMU system property is set (anything but empty or "0"). */
class EmulatorPropsCheck(private val properties: SystemProperties) : DeviceCheck {
  override val name = "emulator_props"
  override val group = DeviceCheckGroup.EMULATOR

  override fun fired(): Boolean = QEMU_PROPS.anySafely { prop ->
    val value = properties.get(prop)
    !value.isNullOrEmpty() && value != "0"
  }
}

/** Sockets, pipes and libraries that only emulators ship. */
class EmulatorFilesCheck(private val files: DeviceFiles) : DeviceCheck {
  override val name = "emulator_files"
  override val group = DeviceCheckGroup.EMULATOR

  override fun fired(): Boolean = EMULATOR_FILES.anySafely(files::exists)
}

/** `Build.BOARD` is empty or "unknown". */
class UnknownBoardCheck(private val build: BuildInfo) : DeviceCheck {
  override val name = "unknown_board"
  override val group = DeviceCheckGroup.EMULATOR

  override fun fired(): Boolean =
    build.board.isEmpty() || build.board.equals("unknown", ignoreCase = true)
}

internal val QEMU_PROPS = listOf(
  "ro.kernel.qemu",
  "ro.kernel.android.qemud",
  "qemu.hw.mainkeys",
  "ro.kernel.qemu.gles"
)

internal val EMULATOR_FILES = listOf(
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
