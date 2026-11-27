package com.aptoide.android.aptoidegames.device_info

import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheck
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckOutcome
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckReport
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Runs the named device checks (emulator, root and runtime tampering) and reports each one by
 * name. A check runs at most once per process: its outcome is kept and reused.
 */
class DeviceSecurityChecker(
  private val checks: List<DeviceCheck>,
  private val dispatcher: CoroutineDispatcher,
) {

  private val mutex = Mutex()
  private val outcomes = mutableMapOf<String, DeviceCheckOutcome>()

  /** Runs the checks off the main thread and reports every one that ran. Never throws. */
  suspend fun runChecks(): DeviceCheckReport = withContext(dispatcher) {
    mutex.withLock {
      DeviceCheckReport(
        checks.map { check ->
          DeviceCheckResult(
            name = check.name,
            group = check.group,
            outcome = outcomes.getOrPut(check.name) { check.outcome() },
          )
        }
      )
    }
  }

  /**
   * Whether Play & Earn must stay hidden: an emulator or a root check fired. The runtime checks
   * (hooking, debugger, test harness) never count here; they are only reported.
   */
  suspend fun isCompromisedDevice(): Boolean = runChecks().hidesPlayAndEarn

  private fun DeviceCheck.outcome(): DeviceCheckOutcome = try {
    if (fired()) DeviceCheckOutcome.FIRED else DeviceCheckOutcome.CLEAR
  } catch (_: Throwable) {
    DeviceCheckOutcome.FAILED
  }
}
