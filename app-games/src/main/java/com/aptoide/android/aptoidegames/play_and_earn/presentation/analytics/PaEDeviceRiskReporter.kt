package com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics

import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckReport
import com.aptoide.android.aptoidegames.play_and_earn.domain.DeviceRiskReporter
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/** Sends `pae_device_risk` once per process, the first time a device check fired. */
@Singleton
class PaEDeviceRiskReporter @Inject constructor(
  private val paEAnalytics: PaEAnalytics,
) : DeviceRiskReporter {

  private val sent = AtomicBoolean(false)

  override fun report(report: DeviceCheckReport) {
    val fired = report.firedNames
    if (fired.isNotEmpty() && sent.compareAndSet(false, true)) {
      paEAnalytics.sendPaEDeviceRisk(fired)
    }
  }
}
