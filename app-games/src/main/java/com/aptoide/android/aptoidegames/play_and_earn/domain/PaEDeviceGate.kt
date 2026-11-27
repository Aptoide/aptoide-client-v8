package com.aptoide.android.aptoidegames.play_and_earn.domain

import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.device_info.DeviceSecurityChecker
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckReport
import javax.inject.Inject

/** Where the device check results go; the implementation decides when to send them. */
interface DeviceRiskReporter {
  fun report(report: DeviceCheckReport)
}

/**
 * The device part of the Play & Earn visibility check. The named device checks always run and
 * are reported, also on debug builds; only release builds hide Play & Earn when an emulator or
 * root check fired.
 */
class PaEDeviceGate(
  private val deviceSecurityChecker: DeviceSecurityChecker,
  private val deviceRiskReporter: DeviceRiskReporter,
  private val isDebugBuild: Boolean,
) {

  @Inject
  constructor(
    deviceSecurityChecker: DeviceSecurityChecker,
    deviceRiskReporter: DeviceRiskReporter,
  ) : this(deviceSecurityChecker, deviceRiskReporter, BuildConfig.DEBUG)

  suspend fun hidesPlayAndEarn(): Boolean {
    val report = deviceSecurityChecker.runChecks()
    deviceRiskReporter.report(report)
    return !isDebugBuild && report.hidesPlayAndEarn
  }
}
