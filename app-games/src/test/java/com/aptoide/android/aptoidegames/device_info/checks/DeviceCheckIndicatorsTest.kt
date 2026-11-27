package com.aptoide.android.aptoidegames.device_info.checks

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Pins every emulator and root indicator to the list the app shipped before the checks had
 * names: these decide whether Play & Earn is hidden, so dropping or editing one must fail here.
 */
internal class DeviceCheckIndicatorsTest {

  private val frozenQemuProps = listOf(
    "ro.kernel.qemu",
    "ro.kernel.android.qemud",
    "qemu.hw.mainkeys",
    "ro.kernel.qemu.gles",
  )

  private val frozenEmulatorFiles = listOf(
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
    "/system/lib/libnoxspeedup.so",
  )

  private val frozenRootFiles = listOf(
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
    "/dev/.superuser.mounted",
  )

  private val frozenRootApps = listOf(
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
    "com.kingouser.com",
  )

  private val frozenSystemPaths = listOf(
    "/system",
    "/system/bin",
    "/system/sbin",
    "/system/xbin",
    "/vendor/bin",
    "/sbin",
    "/etc",
  )

  private val frozenDangerousProps = mapOf(
    "ro.debuggable" to "1",
    "ro.secure" to "0",
    "service.adb.root" to "1",
  )

  private val frozenBuildRules = listOf(
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

  private fun BuildInfo.with(field: BuildField, value: String): BuildInfo = when (field) {
    BuildField.FINGERPRINT -> copy(fingerprint = value)
    BuildField.MODEL -> copy(model = value)
    BuildField.MANUFACTURER -> copy(manufacturer = value)
    BuildField.BRAND -> copy(brand = value)
    BuildField.DEVICE -> copy(device = value)
    BuildField.PRODUCT -> copy(product = value)
    BuildField.HARDWARE -> copy(hardware = value)
    BuildField.BOARD -> copy(board = value)
    BuildField.BOOTLOADER -> copy(bootloader = value)
  }

  @Test
  fun `Every indicator list is the one shipped before`() = scenario {
    m Given "the frozen copies of the old lists"
    val frozen = listOf(
      frozenQemuProps, frozenEmulatorFiles, frozenRootFiles, frozenRootApps, frozenSystemPaths,
    )

    m When "the app's lists are read"
    val current = listOf(QEMU_PROPS, EMULATOR_FILES, ROOT_FILES, ROOT_APPS, SYSTEM_PATHS)

    m Then "they are identical, as are the dangerous props and the Build patterns"
    assertEquals(frozen, current)
    assertEquals(frozenDangerousProps, DANGEROUS_PROPS)
    assertEquals(frozenBuildRules, EMULATOR_BUILD_RULES)
  }

  @Test
  fun `Each Build pattern alone fires the emulator build check`() = scenario {
    m Given "one physical phone build per pattern, with only that field changed"
    val builds = EMULATOR_BUILD_RULES.map { physicalPhoneBuild.with(it.field, it.value) }

    m When "the check runs on each"
    val notFired = EMULATOR_BUILD_RULES.zip(builds)
      .filterNot { (_, build) -> EmulatorBuildCheck(build).fired() }

    m Then "every one fires"
    assertEquals(emptyList<Pair<BuildRule, BuildInfo>>(), notFired)
  }

  @Test
  fun `Each qemu property alone fires the emulator props check`() = scenario {
    m Given "one device per qemu property set to 1"
    val checks = QEMU_PROPS.map { EmulatorPropsCheck(FakeSystemProperties(mapOf(it to "1"))) }

    m When "the checks run"
    val fired = checks.map { it.fired() }

    m Then "every one fires"
    assertEquals(QEMU_PROPS.map { true }, fired)
  }

  @Test
  fun `Each emulator file alone fires the emulator files check`() = scenario {
    m Given "one device per emulator file"
    val checks = EMULATOR_FILES.map { EmulatorFilesCheck(FakeDeviceFiles(existing = setOf(it))) }

    m When "the checks run"
    val fired = checks.map { it.fired() }

    m Then "every one fires"
    assertEquals(EMULATOR_FILES.map { true }, fired)
  }

  @Test
  fun `Each root file alone fires the root files check`() = scenario {
    m Given "one device per root file"
    val checks = ROOT_FILES.map { RootFilesCheck(FakeDeviceFiles(existing = setOf(it))) }

    m When "the checks run"
    val fired = checks.map { it.fired() }

    m Then "every one fires"
    assertEquals(ROOT_FILES.map { true }, fired)
  }

  @Test
  fun `Each root app alone fires the root apps check`() = scenario {
    m Given "one device per root app"
    val checks = ROOT_APPS.map { RootAppsCheck(FakeInstalledPackages(setOf(it))) }

    m When "the checks run"
    val fired = checks.map { it.fired() }

    m Then "every one fires"
    assertEquals(ROOT_APPS.map { true }, fired)
  }

  @Test
  fun `Each writable system path alone fires the read-write check`() = scenario {
    m Given "one device per writable system path"
    val checks = SYSTEM_PATHS.map { RwSystemPathsCheck(FakeDeviceFiles(writable = setOf(it))) }

    m When "the checks run"
    val fired = checks.map { it.fired() }

    m Then "every one fires"
    assertEquals(SYSTEM_PATHS.map { true }, fired)
  }

  @Test
  fun `Each dangerous property alone fires the dangerous props check`() = scenario {
    m Given "one device per dangerous property value"
    val checks = DANGEROUS_PROPS.map { (prop, value) ->
      DangerousPropsCheck(FakeSystemProperties(mapOf(prop to value)))
    }

    m When "the checks run"
    val fired = checks.map { it.fired() }

    m Then "every one fires"
    assertEquals(DANGEROUS_PROPS.map { true }, fired)
  }
}
