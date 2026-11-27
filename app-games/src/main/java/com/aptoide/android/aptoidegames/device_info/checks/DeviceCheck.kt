package com.aptoide.android.aptoidegames.device_info.checks

/**
 * What a check looks for. Only the emulator and root groups hide Play & Earn: together they are
 * the "compromised device" of before AND-956. The runtime group is only reported.
 */
enum class DeviceCheckGroup(val hidesPlayAndEarn: Boolean) {
  EMULATOR(hidesPlayAndEarn = true),
  ROOT(hidesPlayAndEarn = true),
  RUNTIME(hidesPlayAndEarn = false),
}

/** One device check. Implementations hold no state and are fed by small, fakeable sources. */
interface DeviceCheck {
  /**
   * Stable snake_case name. It is the key in the `pae_device_checks` remote config list and is
   * reported to analytics and, later, to the backend, so it must never be renamed.
   */
  val name: String

  val group: DeviceCheckGroup

  /** Runs on a background thread. It may throw: the caller then reports the check as not run. */
  fun fired(): Boolean
}

enum class DeviceCheckOutcome {
  FIRED,
  CLEAR,

  /** The check threw. It counts as not fired. */
  FAILED,
}

data class DeviceCheckResult(
  val name: String,
  val group: DeviceCheckGroup,
  val outcome: DeviceCheckOutcome,
) {
  val fired: Boolean get() = outcome == DeviceCheckOutcome.FIRED
}

/** The result of every check that ran. Checks switched off are absent. */
data class DeviceCheckReport(val results: List<DeviceCheckResult>) {

  /** The names of the checks that fired, sorted. */
  val firedNames: List<String>
    get() = results.filter { it.fired }.map { it.name }.sorted()

  /** True when a check of a group that hides Play & Earn fired. */
  val hidesPlayAndEarn: Boolean
    get() = results.any { it.fired && it.group.hidesPlayAndEarn }

  fun firedIn(group: DeviceCheckGroup): Boolean = results.any { it.fired && it.group == group }
}

/**
 * Like [any], but an item whose probe throws counts as not matching: one package, file or
 * property that cannot be inspected must not fail the whole check.
 */
internal inline fun <T> Iterable<T>.anySafely(probe: (T) -> Boolean): Boolean = any {
  try {
    probe(it)
  } catch (_: Exception) {
    false
  }
}
