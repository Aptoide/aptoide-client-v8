package cm.aptoide.pt.usage_stats

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.USAGE_STATS_SERVICE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Singleton
class DefaultPackageUsageManager(context: Context) : PackageUsageManager {

  private val usageStatsManager = context.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

  override val foregroundPackage: Flow<String?> = MutableStateFlow(value = null)

  override fun getForegroundPackage(): String? {
    val endTime = System.currentTimeMillis()
    val startTime = endTime - 1000 * 10 // check last 10 seconds

    val usageEvents: UsageEvents = usageStatsManager.queryEvents(startTime, endTime)
    val event = UsageEvents.Event()

    val resumedActivities = mutableMapOf<String, MutableSet<String>>()

    var lastPackage: String? = null

    while (usageEvents.hasNextEvent()) {
      usageEvents.getNextEvent(event)
      val packageName = event.packageName

      when (event.eventType) {
        UsageEvents.Event.ACTIVITY_RESUMED -> {
          val activeClasses = resumedActivities.getOrPut(packageName) { mutableSetOf() }
          val className = event.className ?: "unknown"

          activeClasses.add(className)
          lastPackage = packageName
        }

        UsageEvents.Event.ACTIVITY_PAUSED,
        UsageEvents.Event.ACTIVITY_STOPPED -> {
          val className = event.className ?: "unknown"
          resumedActivities[packageName]?.remove(className)

          if (resumedActivities.get(packageName)?.isEmpty() == true) {
            resumedActivities.remove(packageName)
          }
        }
      }
    }

    return if (resumedActivities.isNotEmpty() && resumedActivities.contains(lastPackage)) {
      lastPackage
    } else {
      null
    }
  }

  override fun getForegroundPackageState(startTimeMs: Long?): PackageUsageState {
    return try {
      val endTime = System.currentTimeMillis()
      val defaultStartTime = endTime - QUERY_WINDOW_MS

      // Subtract a buffer to ensure the capture of RESUMED events
      val adjustedStartTime = startTimeMs?.let { it - QUERY_WINDOW_MS }

      val queryStartTime =
        adjustedStartTime?.let { minOf(it, defaultStartTime) } ?: defaultStartTime

      val usageEvents: UsageEvents = usageStatsManager.queryEvents(queryStartTime, endTime)
      ForegroundActivityResolver.resolve(usageEvents.activityEvents())
    } catch (e: Exception) {
      e.printStackTrace()
      PackageUsageState.Error
    }
  }

  private fun UsageEvents.activityEvents(): Sequence<ActivityEvent> = sequence {
    val event = UsageEvents.Event()
    while (hasNextEvent()) {
      getNextEvent(event)
      val type = when (event.eventType) {
        UsageEvents.Event.ACTIVITY_RESUMED -> ActivityEvent.Type.RESUMED
        // Same value as MOVE_TO_BACKGROUND on Android 8/9. Stops always follow a pause, so they
        // add nothing here.
        UsageEvents.Event.ACTIVITY_PAUSED -> ActivityEvent.Type.PAUSED
        else -> null
      } ?: continue
      yield(ActivityEvent(event.packageName, event.className ?: "unknown", type))
    }
  }

  companion object {
    private const val QUERY_WINDOW_MS = 60_000L
  }
}
