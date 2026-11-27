package com.aptoide.android.aptoidegames.device_info.checks

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class RootChecksTest {

  @Test
  fun `The root checks keep their names and hide Play and Earn`() = scenario {
    m Given "the six root checks"
    val checks = listOf(
      RootFilesCheck(FakeDeviceFiles()),
      SuBinaryCheck({ emptyList() }, FakeDeviceFiles()),
      RootAppsCheck(FakeInstalledPackages()),
      DangerousPropsCheck(FakeSystemProperties()),
      RwSystemPathsCheck(FakeDeviceFiles()),
      TestKeysCheck(physicalPhoneBuild),
    )

    m When "their names and groups are read"
    val names = checks.map { it.name }

    m Then "they are the stable root names, all in the root group"
    assertEquals(
      listOf(
        "root_files",
        "su_binary",
        "root_apps",
        "dangerous_props",
        "rw_system_paths",
        "test_keys"
      ),
      names
    )
    assertTrue(checks.all { it.group == DeviceCheckGroup.ROOT })
  }

  @Test
  fun `The root files check fires when Magisk's folder exists`() = scenario {
    m Given "the Magisk folder exists"
    val check = RootFilesCheck(FakeDeviceFiles(existing = setOf("/data/adb/magisk")))

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The root files check does not fire on a clean phone`() = scenario {
    m Given "only ordinary system files exist"
    val check = RootFilesCheck(FakeDeviceFiles(existing = setOf("/system/bin/sh")))

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The su check fires when su is in a PATH directory`() = scenario {
    m Given "su in one of the PATH directories"
    val check = SuBinaryCheck(
      pathDirectories = { listOf("/product/bin", "/apex/bin") },
      files = FakeDeviceFiles(existing = setOf("/apex/bin/su")),
    )

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The su check does not fire when no PATH directory has su`() = scenario {
    m Given "PATH directories without su"
    val check = SuBinaryCheck(
      pathDirectories = { listOf("/product/bin", "/system/bin") },
      files = FakeDeviceFiles(existing = setOf("/system/bin/sh")),
    )

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The root apps check fires when Magisk is installed`() = scenario {
    m Given "the Magisk app installed"
    val check = RootAppsCheck(FakeInstalledPackages(setOf("com.topjohnwu.magisk")))

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The root apps check does not fire without root apps`() = scenario {
    m Given "only ordinary apps installed"
    val check = RootAppsCheck(FakeInstalledPackages(setOf("com.android.chrome")))

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The dangerous props check fires on each dangerous value`() = scenario {
    m Given "one phone per dangerous property"
    val checks = listOf(
      DangerousPropsCheck(FakeSystemProperties(mapOf("ro.debuggable" to "1"))),
      DangerousPropsCheck(FakeSystemProperties(mapOf("ro.secure" to "0"))),
      DangerousPropsCheck(FakeSystemProperties(mapOf("service.adb.root" to "1"))),
    )

    m When "the checks run"
    val fired = checks.map { it.fired() }

    m Then "every one fires"
    assertEquals(listOf(true, true, true), fired)
  }

  @Test
  fun `The dangerous props check does not fire on a user build`() = scenario {
    m Given "a user build's properties"
    val check = DangerousPropsCheck(
      FakeSystemProperties(mapOf("ro.debuggable" to "0", "ro.secure" to "1"))
    )

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The read-write check fires when a system folder is writable`() = scenario {
    m Given "a writable /system"
    val check = RwSystemPathsCheck(FakeDeviceFiles(writable = setOf("/system")))

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The read-write check does not fire on read-only system folders`() = scenario {
    m Given "system folders that exist but are read-only"
    val check = RwSystemPathsCheck(FakeDeviceFiles(existing = setOf("/system", "/system/bin")))

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The test keys check fires on a test-keys build`() = scenario {
    m Given "a build signed with test keys"
    val check = TestKeysCheck(physicalPhoneBuild.copy(tags = "test-keys"))

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The test keys check does not fire on release keys or missing tags`() = scenario {
    m Given "a release-keys build and a build without tags"
    val release = TestKeysCheck(physicalPhoneBuild)
    val noTags = TestKeysCheck(physicalPhoneBuild.copy(tags = null))

    m When "the checks run"
    val fired = listOf(release.fired(), noTags.fired())

    m Then "neither fires"
    assertEquals(listOf(false, false), fired)
  }
}
