package com.aptoide.android.aptoidegames.play_and_earn.domain

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.device_info.DeviceSecurityChecker
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheck
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckGroup
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckReport
import com.aptoide.android.aptoidegames.device_info.checks.FakeCheck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class PaEDeviceGateTest {

  private class RecordingReporter : DeviceRiskReporter {
    val reports = mutableListOf<DeviceCheckReport>()

    override fun report(report: DeviceCheckReport) {
      reports += report
    }
  }

  private val reporter = RecordingReporter()

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
  fun `A release build hides Play and Earn on a rooted device and reports it`() = coScenario {
    m Given "a release build where a root check fires"
    val gate = gate(listOf(FakeCheck("su_binary", DeviceCheckGroup.ROOT) { true }), false)

    m When "the gate is asked"
    val hides = gate.hidesPlayAndEarn()

    m Then "Play and Earn is hidden and the report names the check"
    assertTrue(hides)
    assertEquals(listOf("su_binary"), reporter.reports.single().firedNames)
  }

  @Test
  fun `A debug build never hides Play and Earn but still runs and reports the checks`() =
    coScenario {
      m Given "a debug build where a root check fires"
      val check = FakeCheck("su_binary", DeviceCheckGroup.ROOT) { true }
      val gate = gate(listOf(check), true)

      m When "the gate is asked"
      val hides = gate.hidesPlayAndEarn()

      m Then "Play and Earn stays visible, yet the check ran and was reported"
      assertFalse(hides)
      assertEquals(1, check.runs)
      assertEquals(listOf("su_binary"), reporter.reports.single().firedNames)
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

    m Then "Play and Earn stays visible and the report names the debugger"
    assertFalse(hides)
    assertEquals(listOf("debugger"), reporter.reports.single().firedNames)
  }
}
