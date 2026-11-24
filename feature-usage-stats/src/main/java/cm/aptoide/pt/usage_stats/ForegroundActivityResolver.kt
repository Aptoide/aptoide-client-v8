package cm.aptoide.pt.usage_stats

/** An activity lifecycle event from `UsageStatsManager.queryEvents`, reduced to what matters. */
data class ActivityEvent(
  val packageName: String,
  val className: String,
  val type: Type,
) {
  enum class Type { RESUMED, PAUSED }
}

/**
 * Replays the activity events of a query window into the foreground app and whether another app
 * is resumed together with it.
 *
 * What the events can tell:
 * - Only resumed activities are in the foreground. A locked screen, the launcher, the recents
 *   screen or another app pauses the game, so it stops being the foreground.
 * - A game moved to picture-in-picture is paused (Android never resumes a PiP activity while
 *   another app is in front), so the app behind it becomes the foreground.
 * - Split-screen on Android 10+ (multi-resume) leaves both halves resumed: the foreground is
 *   reported with `sharesScreen`.
 *
 * What they cannot tell:
 * - Whether a paused activity is hidden or still drawn (another app's picture-in-picture or
 *   freeform window over the game, the unfocused half of a split-screen without multi-resume).
 *   Paused apps are deliberately NOT counted as sharing: one PAUSED that never gets its STOPPED
 *   (launcher at launch, a Custom Tab or sign-in sheet closed from the game, an OEM that emits no
 *   stops) would stop the game earning for the whole session. So another app's PiP or freeform
 *   window floating over the game does not stop earning, and without multi-resume (Android 8/9)
 *   split-screen with the game as the focused half looks like full screen.
 * - Which half of a split-screen has the input focus: there is no usage event for focus.
 * - Apps that resumed before the query window and had no events since are not seen at all.
 * - Windows that are not activities (notification shade, overlays, chat heads) are invisible.
 *
 * Residual risk: another package's RESUMED never followed by a PAUSED (its process killed while
 * resumed, if the system logs nothing for it) reads as sharing until that package logs again.
 */
internal object ForegroundActivityResolver {

  fun resolve(events: Sequence<ActivityEvent>): PackageUsageState {
    val resumed = mutableMapOf<String, MutableSet<String>>()
    var lastResumedPackage: String? = null
    var hasAnyActivityEvents = false

    events.forEach { event ->
      hasAnyActivityEvents = true
      val pkg = event.packageName
      when (event.type) {
        ActivityEvent.Type.RESUMED -> {
          resumed.getOrPut(pkg) { mutableSetOf() }.add(event.className)
          lastResumedPackage = pkg
        }

        ActivityEvent.Type.PAUSED -> {
          val classes = resumed[pkg] ?: return@forEach
          classes.remove(event.className)
          if (classes.isEmpty()) resumed.remove(pkg)
        }
      }
    }

    return when {
      resumed.isNotEmpty() -> {
        // The last one resumed that is still resumed; otherwise any resumed package.
        val foreground = lastResumedPackage?.takeIf { it in resumed } ?: resumed.keys.first()
        PackageUsageState.ForegroundPackage(foreground, sharesScreen = resumed.size > 1)
      }

      // Activity events, but nothing resumed: everything was paused.
      hasAnyActivityEvents -> PackageUsageState.NoForegroundPackage

      // No activity at all, or a failure reading the events: avoid counting time.
      else -> PackageUsageState.Error
    }
  }
}
