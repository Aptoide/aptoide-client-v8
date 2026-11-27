package com.aptoide.android.aptoidegames.device_info

import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheck
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckOutcome
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckReport
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheckResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Runs the named device checks (emulator, root and runtime tampering) and reports each one by
 * name. A check runs at most once per process: its outcome is kept and reused.
 *
 * Which checks run comes from the [DEVICE_CHECKS_FLAG_KEY] remote config flag, a JSON array of
 * check names. Without the flag (or when it cannot be parsed) every check runs; names the app
 * does not know are ignored; a check left out of the list does not run at all.
 */
class DeviceSecurityChecker(
  private val checks: List<DeviceCheck>,
  private val featureFlags: FeatureFlags,
  private val dispatcher: CoroutineDispatcher,
) {

  companion object {
    const val DEVICE_CHECKS_FLAG_KEY = "pae_device_checks"
  }

  private val mutex = Mutex()
  private val outcomes = mutableMapOf<String, DeviceCheckOutcome>()

  /** Runs the checks off the main thread and reports every one that ran. Never throws. */
  suspend fun runChecks(): DeviceCheckReport = withContext(dispatcher) {
    val enabled = enabledChecks()
    mutex.withLock {
      DeviceCheckReport(
        enabled.map { check ->
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

  private suspend fun enabledChecks(): List<DeviceCheck> {
    val names = featureFlags.getStringListOrNull(DEVICE_CHECKS_FLAG_KEY)
      ?.map { it.trim().lowercase(Locale.US) }
      ?.toSet()
      ?: return checks
    return checks.filter { it.name in names }
  }

  private fun DeviceCheck.outcome(): DeviceCheckOutcome = try {
    if (fired()) DeviceCheckOutcome.FIRED else DeviceCheckOutcome.CLEAR
  } catch (_: Throwable) {
    DeviceCheckOutcome.FAILED
  }
}
