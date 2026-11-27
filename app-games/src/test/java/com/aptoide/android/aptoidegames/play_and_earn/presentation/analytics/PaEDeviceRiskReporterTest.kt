package com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckGroup
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckOutcome
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckReport
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class PaEDeviceRiskReporterTest {

  private class RecordingSender : AnalyticsSender {
    val events = mutableListOf<Pair<String, Map<String, Any?>?>>()

    override fun logEvent(name: String, params: Map<String, Any?>?) {
      events += name to params
    }
  }

  private val sender = RecordingSender()
  private val reporter = PaEDeviceRiskReporter(PaEAnalytics(GenericAnalytics(sender)))

  private fun report(vararg fired: String) = DeviceCheckReport(
    fired.map { DeviceCheckResult(it, DeviceCheckGroup.ROOT, DeviceCheckOutcome.FIRED) }
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
}
