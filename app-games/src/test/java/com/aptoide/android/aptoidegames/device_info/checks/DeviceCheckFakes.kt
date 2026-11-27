package com.aptoide.android.aptoidegames.device_info.checks

class FakeDeviceFiles(
  private val existing: Set<String> = emptySet(),
  private val writable: Set<String> = emptySet(),
) : DeviceFiles {
  override fun exists(path: String): Boolean = path in existing || path in writable
  override fun isWritable(path: String): Boolean = path in writable
}

class FakeSystemProperties(private val values: Map<String, String> = emptyMap()) :
  SystemProperties {
  override fun get(name: String): String? = values[name]
}

class FakeInstalledPackages(private val installed: Set<String> = emptySet()) : InstalledPackages {
  val asked = mutableListOf<String>()

  override fun isInstalled(packageName: String): Boolean {
    asked += packageName
    return packageName in installed
  }
}

/** A real phone's build: nothing in it looks like an emulator or a test-keys build. */
val physicalPhoneBuild = BuildInfo(
  fingerprint = "google/oriole/oriole:14/AP1A.240305.019/11314321:user/release-keys",
  model = "Pixel 6",
  manufacturer = "Google",
  brand = "google",
  device = "oriole",
  product = "oriole",
  hardware = "oriole",
  board = "oriole",
  bootloader = "slider-1.3-11009093",
  tags = "release-keys",
)

class FakeCheck(
  override val name: String,
  override val group: DeviceCheckGroup = DeviceCheckGroup.ROOT,
  private val result: () -> Boolean = { false },
) : DeviceCheck {
  var runs = 0

  override fun fired(): Boolean {
    runs++
    return result()
  }
}
