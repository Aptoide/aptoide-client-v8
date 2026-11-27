package com.aptoide.android.aptoidegames.device_info.checks

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class EmulatorChecksTest {

  @Test
  fun `The emulator checks keep their names and hide Play and Earn`() = scenario {
    m Given "the four emulator checks"
    val checks = listOf(
      EmulatorBuildCheck(physicalPhoneBuild),
      EmulatorPropsCheck(FakeSystemProperties()),
      EmulatorFilesCheck(FakeDeviceFiles()),
      UnknownBoardCheck(physicalPhoneBuild),
    )

    m When "their names and groups are read"
    val names = checks.map { it.name }

    m Then "they are the stable emulator names, all in the emulator group"
    assertEquals(
      listOf("emulator_build", "emulator_props", "emulator_files", "unknown_board"),
      names
    )
    assertTrue(checks.all { it.group == DeviceCheckGroup.EMULATOR })
  }

  @Test
  fun `The build check does not fire on a physical phone`() = scenario {
    m Given "a physical phone's build"
    val check = EmulatorBuildCheck(physicalPhoneBuild)

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The build check fires on the Android Studio emulator`() = scenario {
    m Given "a build with the emulator's ranchu hardware"
    val check = EmulatorBuildCheck(physicalPhoneBuild.copy(hardware = "ranchu"))

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The build check fires on a generic fingerprint`() = scenario {
    m Given "a build with a generic fingerprint"
    val check = EmulatorBuildCheck(physicalPhoneBuild.copy(fingerprint = "generic/sdk/x86"))

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The props check fires when a qemu property is set`() = scenario {
    m Given "ro.kernel.qemu set to 1"
    val check = EmulatorPropsCheck(FakeSystemProperties(mapOf("ro.kernel.qemu" to "1")))

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The props check ignores qemu properties that are empty or 0`() = scenario {
    m Given "qemu properties that are empty or 0"
    val check = EmulatorPropsCheck(
      FakeSystemProperties(mapOf("ro.kernel.qemu" to "0", "qemu.hw.mainkeys" to ""))
    )

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The files check fires when an emulator file exists`() = scenario {
    m Given "the qemu pipe exists"
    val check = EmulatorFilesCheck(FakeDeviceFiles(existing = setOf("/dev/qemu_pipe")))

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The files check does not fire when no emulator file exists`() = scenario {
    m Given "no emulator file exists"
    val check = EmulatorFilesCheck(FakeDeviceFiles(existing = setOf("/system/bin/sh")))

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The board check fires on an empty or unknown board`() = scenario {
    m Given "builds with an empty and an unknown board"
    val empty = UnknownBoardCheck(physicalPhoneBuild.copy(board = ""))
    val unknown = UnknownBoardCheck(physicalPhoneBuild.copy(board = "UNKNOWN"))

    m When "the checks run"
    val fired = listOf(empty.fired(), unknown.fired())

    m Then "both fire"
    assertEquals(listOf(true, true), fired)
  }

  @Test
  fun `The board check does not fire on a named board`() = scenario {
    m Given "a physical phone's board"
    val check = UnknownBoardCheck(physicalPhoneBuild)

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }
}
