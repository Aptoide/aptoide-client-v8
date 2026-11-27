package com.aptoide.android.aptoidegames.device_info

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.device_info.checks.CallStack
import com.aptoide.android.aptoidegames.device_info.checks.ClassProbe
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheck
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckGroup
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckOutcome
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckResult
import com.aptoide.android.aptoidegames.device_info.checks.FakeCheck
import com.aptoide.android.aptoidegames.device_info.checks.FakeInstalledPackages
import com.aptoide.android.aptoidegames.device_info.checks.HookLibrariesCheck
import com.aptoide.android.aptoidegames.device_info.checks.ProcessMaps
import com.aptoide.android.aptoidegames.device_info.checks.XposedCheck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.IOException

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

  private class FakeFlags(private val deviceChecks: List<String>? = null) : FeatureFlags {
    var reads = 0

    override suspend fun getStringListOrNull(key: String): List<String>? {
      if (key != "pae_device_checks") return null
      reads++
      return deviceChecks
    }
  }

  private fun checker(checks: List<DeviceCheck>, flags: FeatureFlags = FakeFlags()) =
    DeviceSecurityChecker(
      checks = checks,
      featureFlags = flags,
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
  fun `Xposed installed on an otherwise clean device does not hide Play and Earn`() =
    coScenario {
      m Given "the existing checks clean and the LSPosed manager installed"
      val xposed = XposedCheck(
        classes = object : ClassProbe {
          override fun isLoadable(className: String) = false
        },
        stack = object : CallStack {
          override fun classNames() = emptyList<String>()
        },
        packages = FakeInstalledPackages(setOf("org.lsposed.manager")),
      )
      val checker = checker(existingChecks + xposed)

      m When "the checks run"
      val report = checker.runChecks()

      m Then "xposed is reported but the device is not compromised"
      assertEquals(listOf("xposed"), report.firedNames)
      assertFalse(checker.isCompromisedDevice())
    }

  @Test
  fun `Unreadable process maps leave the hook libraries check not run`() = coScenario {
    m Given "process maps that cannot be read"
    val hooks = HookLibrariesCheck(object : ProcessMaps {
      override fun mappedPaths(): Set<String> = throw IOException("no access")
    })
    val checker = checker(listOf(hooks))

    m When "the checks run"
    val report = checker.runChecks()

    m Then "the check did not fire and is reported as not run"
    assertEquals(emptyList<String>(), report.firedNames)
    assertEquals(DeviceCheckOutcome.FAILED, report.results.single().outcome)
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
    assertEquals(
      DeviceCheckOutcome.FAILED,
      report.results.single { it.name == "root_apps" }.outcome
    )
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

  @Test
  fun `Without the remote config flag every check runs`() = coScenario {
    m Given "no pae_device_checks flag"
    val runtime = FakeCheck("debugger", DeviceCheckGroup.RUNTIME)
    val checks = existingChecks + runtime
    val checker = checker(checks, FakeFlags(deviceChecks = null))

    m When "the checks run"
    val report = checker.runChecks()

    m Then "all of them ran"
    assertEquals(checks.map { it.name }, report.results.map { it.name })
    assertTrue(checks.all { it.runs == 1 })
  }

  @Test
  fun `A check left out of the remote config list never runs`() = coScenario {
    m Given "a rooted device where only root_apps fires, and root_apps switched off"
    val rootApps = FakeCheck("root_apps", DeviceCheckGroup.ROOT) { true }
    val testKeys = FakeCheck("test_keys", DeviceCheckGroup.ROOT)
    val checker = checker(listOf(rootApps, testKeys), FakeFlags(listOf("test_keys")))

    m When "the device is checked"
    val report = checker.runChecks()

    m Then "root_apps was never asked, is absent from the report and hides nothing"
    assertEquals(0, rootApps.runs)
    assertEquals(listOf("test_keys"), report.results.map { it.name })
    assertFalse(checker.isCompromisedDevice())
  }

  @Test
  fun `Names the app does not know are ignored`() = coScenario {
    m Given "a list with an unknown name next to a known one"
    val suBinary = FakeCheck("su_binary", DeviceCheckGroup.ROOT) { true }
    val checker = checker(
      listOf(suBinary, FakeCheck("test_keys", DeviceCheckGroup.ROOT)),
      FakeFlags(listOf("not_a_check", "su_binary")),
    )

    m When "the checks run"
    val report = checker.runChecks()

    m Then "only the known one ran"
    assertEquals(listOf("su_binary"), report.results.map { it.name })
  }

  @Test
  fun `Names in the list are trimmed and case-insensitive`() = coScenario {
    m Given "a list with a padded, upper-case name"
    val checker = checker(
      listOf(FakeCheck("su_binary", DeviceCheckGroup.ROOT) { true }),
      FakeFlags(listOf(" SU_Binary ")),
    )

    m When "the checks run"
    val report = checker.runChecks()

    m Then "the check ran"
    assertEquals(listOf("su_binary"), report.firedNames)
  }

  @Test
  fun `An empty list is invalid and every check runs`() = coScenario {
    m Given "an empty pae_device_checks list on a rooted device"
    val checks = existingChecks.map { FakeCheck(it.name, it.group) { true } }
    val checker = checker(checks, FakeFlags(emptyList()))

    m When "the device is checked"
    val compromised = checker.isCompromisedDevice()

    m Then "every check ran and Play and Earn stays hidden"
    assertTrue(compromised)
    assertTrue(checks.all { it.runs == 1 })
  }

  @Test
  fun `A list with no known name is invalid and every check runs`() = coScenario {
    m Given "a pae_device_checks list made only of typos"
    val checks = existingChecks.map { FakeCheck(it.name, it.group) { true } }
    val checker = checker(checks, FakeFlags(listOf("nope", "su_binnary")))

    m When "the device is checked"
    val compromised = checker.isCompromisedDevice()

    m Then "every check ran and Play and Earn stays hidden"
    assertTrue(compromised)
    assertTrue(checks.all { it.runs == 1 })
  }

  @Test
  fun `A check that threw is tried again on the next run`() = coScenario {
    m Given "a check that throws the first time and fires the second"
    var calls = 0
    val check = FakeCheck("root_apps", DeviceCheckGroup.ROOT) {
      calls++
      if (calls == 1) throw IllegalStateException("binder died") else true
    }
    val checker = checker(listOf(check))
    checker.runChecks()

    m When "the checks run again"
    val report = checker.runChecks()

    m Then "it ran again and its new result counts"
    assertEquals(2, check.runs)
    assertEquals(listOf("root_apps"), report.firedNames)
  }

  @Test
  fun `An out of memory error is not swallowed`() = coScenario {
    m Given "a check that runs out of memory"
    val checker = checker(
      listOf(FakeCheck("root_apps", DeviceCheckGroup.ROOT) { throw OutOfMemoryError() })
    )

    m When "the checks run"
    val thrown = runCatching { checker.runChecks() }.exceptionOrNull()

    m Then "the error reaches the caller"
    assertTrue(thrown is OutOfMemoryError)
  }

  @Test
  fun `Two callers at once run each check only once`() = coScenario {
    m Given "a checker on a multi-threaded dispatcher"
    val check = FakeCheck("su_binary", DeviceCheckGroup.ROOT) { Thread.sleep(20); true }
    val checker = DeviceSecurityChecker(listOf(check), FakeFlags(), Dispatchers.Default)

    m When "two callers run the checks at the same time"
    val reports = coroutineScope {
      listOf(async { checker.runChecks() }, async { checker.runChecks() }).awaitAll()
    }

    m Then "the check ran once and both got its result"
    assertEquals(1, check.runs)
    assertTrue(reports.all { it.firedNames == listOf("su_binary") })
  }

  @Test
  fun `The remote config list is read on every run`() = coScenario {
    m Given "a checker"
    val flags = FakeFlags(listOf("su_binary"))
    val checker = checker(listOf(FakeCheck("su_binary", DeviceCheckGroup.ROOT)), flags)

    m When "the checks run twice"
    repeat(2) { checker.runChecks() }

    m Then "the list was read each time, so a config update applies to the next run"
    assertEquals(2, flags.reads)
  }
}
