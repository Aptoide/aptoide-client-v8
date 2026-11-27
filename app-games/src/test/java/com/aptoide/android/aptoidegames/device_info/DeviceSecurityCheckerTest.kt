package com.aptoide.android.aptoidegames.device_info

import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckGroup
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckOutcome
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckResult
import com.aptoide.android.aptoidegames.device_info.checks.FakeCheck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@OptIn(ExperimentalCoroutinesApi::class)
internal class DeviceSecurityCheckerTest {

  private val existingChecks = listOf(
    FakeCheck("emulator_build", DeviceCheckGroup.EMULATOR),
    FakeCheck("emulator_props", DeviceCheckGroup.EMULATOR),
    FakeCheck("emulator_files", DeviceCheckGroup.EMULATOR),
    FakeCheck("unknown_board", DeviceCheckGroup.EMULATOR),
    FakeCheck("root_files", DeviceCheckGroup.ROOT),
    FakeCheck("su_binary", DeviceCheckGroup.ROOT),
    FakeCheck("root_apps", DeviceCheckGroup.ROOT),
    FakeCheck("dangerous_props", DeviceCheckGroup.ROOT),
    FakeCheck("rw_system_paths", DeviceCheckGroup.ROOT),
    FakeCheck("test_keys", DeviceCheckGroup.ROOT),
  )

  private fun checker(checks: List<FakeCheck>) = DeviceSecurityChecker(
    checks = checks,
    dispatcher = Dispatchers.Unconfined,
  )

  private fun existingChecksWhereOnlyFires(name: String) = existingChecks.map {
    if (it.name == name) FakeCheck(it.name, it.group) { true } else it
  }

  @Test
  fun `A clean device is not compromised`() = coScenario {
    m Given "the existing checks, none firing"
    val checker = checker(existingChecks)

    m When "the device is checked"
    val compromised = checker.isCompromisedDevice()

    m Then "it is not compromised"
    assertFalse(compromised)
  }

  @ParameterizedTest
  @ValueSource(
    strings = [
      "emulator_build", "emulator_props", "emulator_files", "unknown_board", "root_files",
      "su_binary", "root_apps", "dangerous_props", "rw_system_paths", "test_keys",
    ]
  )
  fun `Each existing indicator alone makes the device compromised`(name: String) =
    coScenario {
      m Given "only the $name check firing"
      val checker = checker(existingChecksWhereOnlyFires(name))

      m When "the device is checked"
      val compromised = checker.isCompromisedDevice()

      m Then "it is compromised, as before the checks had names"
      assertTrue(compromised)
    }

  @Test
  fun `A runtime check firing alone does not make the device compromised`() =
    coScenario {
      m Given "the existing checks clean and a runtime check firing"
      val checker = checker(
        existingChecks + FakeCheck("debugger", DeviceCheckGroup.RUNTIME) { true }
      )

      m When "the device is checked"
      val compromised = checker.isCompromisedDevice()

      m Then "it is not compromised: runtime checks are only reported"
      assertFalse(compromised)
    }

  @Test
  fun `The report names every check that ran and what it found`() = coScenario {
    m Given "one root check firing, one runtime check clean"
    val checker = checker(
      listOf(
        FakeCheck("su_binary", DeviceCheckGroup.ROOT) { true },
        FakeCheck("debugger", DeviceCheckGroup.RUNTIME),
      )
    )

    m When "the checks run"
    val report = checker.runChecks()

    m Then "each result carries its name and outcome"
    assertEquals(
      listOf(
        DeviceCheckResult("su_binary", DeviceCheckGroup.ROOT, DeviceCheckOutcome.FIRED),
        DeviceCheckResult("debugger", DeviceCheckGroup.RUNTIME, DeviceCheckOutcome.CLEAR),
      ),
      report.results
    )
    assertEquals(listOf("su_binary"), report.firedNames)
  }

  @Test
  fun `A check that throws is not fired and is reported as not run`() = coScenario {
    m Given "a root check that throws next to one that fires"
    val checker = checker(
      listOf(
        FakeCheck("root_apps", DeviceCheckGroup.ROOT) { throw SecurityException("denied") },
        FakeCheck("test_keys", DeviceCheckGroup.ROOT) { true },
      )
    )

    m When "the checks run"
    val report = checker.runChecks()

    m Then "the throwing check failed, the other still fired"
    assertEquals(listOf("test_keys"), report.firedNames)
    assertEquals(listOf("root_apps"), report.couldNotRun)
  }

  @Test
  fun `A device whose only check throws is not compromised`() = coScenario {
    m Given "a root check that throws an error"
    val checker = checker(
      listOf(FakeCheck("root_apps", DeviceCheckGroup.ROOT) { throw LinkageError("missing") })
    )

    m When "the device is checked"
    val compromised = checker.isCompromisedDevice()

    m Then "it is not compromised and nothing crashed"
    assertFalse(compromised)
  }

  @Test
  fun `Each check runs at most once per process`() = coScenario {
    m Given "a checker with one check"
    val check = FakeCheck("su_binary", DeviceCheckGroup.ROOT) { true }
    val checker = checker(listOf(check))

    m When "the device is checked three times"
    repeat(3) { checker.runChecks() }

    m Then "the check ran once and its result is reused"
    assertEquals(1, check.runs)
    assertTrue(checker.isCompromisedDevice())
  }

  @Test
  fun `The emulator and root groups are told apart`() = coScenario {
    m Given "an emulator check firing and every root check clean"
    val checker = checker(existingChecksWhereOnlyFires("emulator_files"))

    m When "the checks run"
    val report = checker.runChecks()

    m Then "only the emulator group fired"
    assertTrue(report.firedIn(DeviceCheckGroup.EMULATOR))
    assertFalse(report.firedIn(DeviceCheckGroup.ROOT))
  }
}
