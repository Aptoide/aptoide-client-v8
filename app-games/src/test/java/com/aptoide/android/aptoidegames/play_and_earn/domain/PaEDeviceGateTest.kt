package com.aptoide.android.aptoidegames.play_and_earn.domain

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.test.gherkin.coScenario
import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.device_info.DeviceSecurityChecker
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheck
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckGroup
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckOutcome
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckReport
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckResult
import com.aptoide.android.aptoidegames.device_info.checks.FakeCheck
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.PaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.deviceRiskParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class PaEDeviceGateTest {

  private class RecordingSender : AnalyticsSender {
    val events = mutableListOf<Pair<String, Map<String, Any?>?>>()

    override fun logEvent(name: String, params: Map<String, Any?>?) {
      events += name to params
    }
  }

  private val allNames = listOf(
    "emulator_build", "emulator_props", "emulator_files", "unknown_board", "root_files",
    "su_binary", "root_apps", "dangerous_props", "rw_system_paths", "test_keys", "xposed",
    "hook_libraries", "debugger", "test_harness", "app_debuggable",
  )

  private val sender = RecordingSender()
  private val reporter = PaEDeviceRiskReporter(PaEAnalytics(GenericAnalytics(sender)))

  private fun report(vararg fired: String) = DeviceCheckReport(
    fired.map { DeviceCheckResult(it, DeviceCheckGroup.ROOT, DeviceCheckOutcome.FIRED) }
  )

  private fun gate(checks: List<DeviceCheck>, isDebugBuild: Boolean) = PaEDeviceGate(
    deviceSecurityChecker = DeviceSecurityChecker(
      checks = checks,
      featureFlags = object : FeatureFlags {},
      dispatcher = Dispatchers.Unconfined,
    ),
    deviceRiskReporter = reporter,
    isDebugBuild = isDebugBuild,
  )

  @Test
  fun `The event carries the fired checks, sorted and comma-separated, and their count`() =
    scenario {
      m Given "a report where two checks fired out of order"
      val report = report("xposed", "debugger")

      m When "it is reported"
      reporter.report(report)

      m Then "one pae_device_risk event names both, sorted"
      assertEquals(
        listOf("pae_device_risk" to mapOf("checks" to "debugger,xposed", "count" to 2)),
        sender.events
      )
    }

  @Test
  fun `Nothing is sent when no check fired`() = scenario {
    m Given "a report where nothing fired"
    val report = DeviceCheckReport(
      listOf(DeviceCheckResult("su_binary", DeviceCheckGroup.ROOT, DeviceCheckOutcome.CLEAR))
    )

    m When "it is reported"
    reporter.report(report)

    m Then "no event is sent"
    assertEquals(emptyList<Pair<String, Map<String, Any?>?>>(), sender.events)
  }

  @Test
  fun `The event is sent at most once per process`() = scenario {
    m Given "a first report with a fired check"
    reporter.report(report("su_binary"))

    m When "more reports come in"
    reporter.report(report("su_binary"))
    reporter.report(report("su_binary", "xposed"))

    m Then "only the first one was sent"
    assertEquals(1, sender.events.size)
  }

  @Test
  fun `A clean report does not use up the one event`() = scenario {
    m Given "a first report where nothing fired"
    reporter.report(report())

    m When "a later report has a fired check"
    reporter.report(report("debugger"))

    m Then "that one is sent"
    assertEquals(listOf("pae_device_risk"), sender.events.map { it.first })
  }

  @Test
  fun `A long list is split over several parameters of at most 100 characters`() = scenario {
    m Given "every check name"
    val names = allNames

    m When "the parameters are built"
    val params = deviceRiskParams(names)

    m Then "the values fit Firebase's limit, split between names, and lose nothing"
    val chunks = listOf(params["checks"], params["checks_2"]).map { it as String }
    assertTrue(chunks.all { it.length <= 100 })
    assertEquals(allNames.sorted(), chunks.flatMap { it.split(",") })
    assertEquals(setOf("checks", "checks_2", "count"), params.keys)
    assertEquals(15, params["count"])
  }

  @Test
  fun `A release build hides Play and Earn on a rooted device and reports it`() = coScenario {
    m Given "a release build where a root check fires"
    val gate = gate(listOf(FakeCheck("su_binary", DeviceCheckGroup.ROOT) { true }), false)

    m When "the gate is asked"
    val hides = gate.hidesPlayAndEarn()

    m Then "Play and Earn is hidden and the event names the check"
    assertTrue(hides)
    assertEquals("su_binary", sender.events.single().second?.get("checks"))
  }

  @Test
  fun `A debug build never hides Play and Earn but still runs and reports the checks`() =
    coScenario {
      m Given "a debug build where a root check fires"
      val check = FakeCheck("su_binary", DeviceCheckGroup.ROOT) { true }
      val gate = gate(listOf(check), true)

      m When "the gate is asked"
      val hides = gate.hidesPlayAndEarn()

      m Then "Play and Earn stays visible, yet the check ran and the event was sent"
      assertFalse(hides)
      assertEquals(1, check.runs)
      assertEquals("su_binary", sender.events.single().second?.get("checks"))
    }

  @Test
  fun `A runtime check alone is reported but does not hide Play and Earn`() = coScenario {
    m Given "a release build where only the debugger check fires"
    val gate = gate(
      listOf(
        FakeCheck("su_binary", DeviceCheckGroup.ROOT),
        FakeCheck("debugger", DeviceCheckGroup.RUNTIME) { true },
      ),
      false
    )

    m When "the gate is asked"
    val hides = gate.hidesPlayAndEarn()

    m Then "Play and Earn stays visible and the event names the debugger"
    assertFalse(hides)
    assertEquals("debugger", sender.events.single().second?.get("checks"))
  }
}
