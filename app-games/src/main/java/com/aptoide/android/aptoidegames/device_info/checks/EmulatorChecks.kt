package com.aptoide.android.aptoidegames.device_info.checks

/** Build fields carry the names of known emulators (Android Studio, Genymotion, Nox, ...). */
class EmulatorBuildCheck(private val build: BuildInfo) : DeviceCheck {
  override val name = "emulator_build"
  override val group = DeviceCheckGroup.EMULATOR

  override fun fired(): Boolean = with(build) {
    fingerprint.startsWith("generic", ignoreCase = true) ||
      fingerprint.startsWith("unknown", ignoreCase = true) ||
      fingerprint.contains("emulator", ignoreCase = true) ||
      fingerprint.contains("sdk_gphone", ignoreCase = true) ||
      model.contains("google_sdk", ignoreCase = true) ||
      model.contains("emulator", ignoreCase = true) ||
      model.contains("droid4x", ignoreCase = true) ||
      model.equals("sdk", ignoreCase = true) ||
      model.contains("Android SDK built for", ignoreCase = true) ||
      manufacturer.contains("genymotion", ignoreCase = true) ||
      brand.startsWith("generic", ignoreCase = true) ||
      device.startsWith("generic", ignoreCase = true) ||
      product.equals("sdk", ignoreCase = true) ||
      product.contains("sdk_gphone", ignoreCase = true) ||
      product.contains("vbox", ignoreCase = true) ||
      product.contains("emulator", ignoreCase = true) ||
      product.contains("simulator", ignoreCase = true) ||
      hardware.contains("goldfish", ignoreCase = true) ||
      hardware.contains("ranchu", ignoreCase = true) ||
      hardware.contains("nox", ignoreCase = true) ||
      hardware.contains("vbox", ignoreCase = true) ||
      board.contains("nox", ignoreCase = true) ||
      bootloader.contains("nox", ignoreCase = true)
  }
}

/** A QEMU system property is set (anything but empty or "0"). */
class EmulatorPropsCheck(private val properties: SystemProperties) : DeviceCheck {
  override val name = "emulator_props"
  override val group = DeviceCheckGroup.EMULATOR

  override fun fired(): Boolean = QEMU_PROPS.anySafely { prop ->
    val value = properties.get(prop)
    !value.isNullOrEmpty() && value != "0"
  }

  private companion object {
    val QEMU_PROPS = listOf(
      "ro.kernel.qemu",
      "ro.kernel.android.qemud",
      "qemu.hw.mainkeys",
      "ro.kernel.qemu.gles"
    )
  }
}

/** Sockets, pipes and libraries that only emulators ship. */
class EmulatorFilesCheck(private val files: DeviceFiles) : DeviceCheck {
  override val name = "emulator_files"
  override val group = DeviceCheckGroup.EMULATOR

  override fun fired(): Boolean = EMULATOR_FILES.anySafely(files::exists)

  private companion object {
    val EMULATOR_FILES = listOf(
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
  }
}

/** `Build.BOARD` is empty or "unknown". */
class UnknownBoardCheck(private val build: BuildInfo) : DeviceCheck {
  override val name = "unknown_board"
  override val group = DeviceCheckGroup.EMULATOR

  override fun fired(): Boolean =
    build.board.isEmpty() || build.board.equals("unknown", ignoreCase = true)
}
