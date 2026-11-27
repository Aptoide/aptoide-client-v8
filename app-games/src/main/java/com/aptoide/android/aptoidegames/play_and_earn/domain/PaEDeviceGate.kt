package com.aptoide.android.aptoidegames.play_and_earn.domain

import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.device_info.DeviceSecurityChecker
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckReport
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.PaEAnalytics
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The device part of the Play & Earn visibility check. The named device checks always run and
 * are reported, also on debug builds; only release builds hide Play & Earn when an emulator or
 * root check fired.
 */
class PaEDeviceGate(
  private val deviceSecurityChecker: DeviceSecurityChecker,
  private val deviceRiskReporter: PaEDeviceRiskReporter,
  private val isDebugBuild: Boolean,
) {

  @Inject
  constructor(
    deviceSecurityChecker: DeviceSecurityChecker,
    deviceRiskReporter: PaEDeviceRiskReporter,
  ) : this(deviceSecurityChecker, deviceRiskReporter, BuildConfig.DEBUG)

  suspend fun hidesPlayAndEarn(): Boolean {
    val report = deviceSecurityChecker.runChecks()
    deviceRiskReporter.report(report)
    return !isDebugBuild && report.hidesPlayAndEarn
  }
}

/** Sends `pae_device_risk` once per process, the first time a device check fired. */
@Singleton
class PaEDeviceRiskReporter @Inject constructor(
  private val paEAnalytics: PaEAnalytics,
) {

  private val sent = AtomicBoolean(false)

  fun report(report: DeviceCheckReport) {
    val fired = report.firedNames
    if (fired.isNotEmpty() && sent.compareAndSet(false, true)) {
      paEAnalytics.sendPaEDeviceRisk(fired)
    }
  }
}
