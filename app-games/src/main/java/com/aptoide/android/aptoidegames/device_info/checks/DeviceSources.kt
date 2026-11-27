package com.aptoide.android.aptoidegames.device_info.checks

/** File system probes. Each call answers false when the path cannot be inspected. */
interface DeviceFiles {
  fun exists(path: String): Boolean
  fun isWritable(path: String): Boolean
}

/** Reads a system property (`getprop`); null when it is unset or cannot be read. */
interface SystemProperties {
  fun get(name: String): String?
}

interface InstalledPackages {
  fun isInstalled(packageName: String): Boolean
}

/** The `android.os.Build` fields the checks look at. */
data class BuildInfo(
  val fingerprint: String,
  val model: String,
  val manufacturer: String,
  val brand: String,
  val device: String,
  val product: String,
  val hardware: String,
  val board: String,
  val bootloader: String,
  val tags: String?,
)
