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
      val event = UsageEvents.Event()

      val resumedActivities = mutableMapOf<String, MutableSet<String>>()
      var lastResumedPackage: String? = null
      var hasAnyActivityEvents = false

      while (usageEvents.hasNextEvent()) {
        usageEvents.getNextEvent(event)
        val packageName = event.packageName

        when (event.eventType) {
          UsageEvents.Event.ACTIVITY_RESUMED -> {
            hasAnyActivityEvents = true
            val activeClasses = resumedActivities.getOrPut(packageName) { mutableSetOf() }
            val className = event.className ?: "unknown"

            activeClasses.add(className)
            lastResumedPackage = packageName
          }

          UsageEvents.Event.ACTIVITY_PAUSED,
          UsageEvents.Event.ACTIVITY_STOPPED -> {
            hasAnyActivityEvents = true
            val className = event.className ?: "unknown"

            resumedActivities[packageName]?.remove(className)

            // If all activities for this package are paused/stopped, remove from map
            if (resumedActivities[packageName]?.isEmpty() == true) {
              resumedActivities.remove(packageName)
            }
          }
        }
      }

      // Determine the state based on what we found
      when {
        // If we have resumed activities, return the most recent one
        resumedActivities.isNotEmpty() -> {
          // Find the package that's actually in foreground (the last one resumed that's still active)
          val foregroundPackage = if (resumedActivities.contains(lastResumedPackage)) {
            lastResumedPackage
          } else {
            // Fallback to any resumed package
            resumedActivities.keys.firstOrNull()
          }
          foregroundPackage?.let { PackageUsageState.ForegroundPackage(it) }
            ?: PackageUsageState.NoForegroundPackage
        }

        // If we saw activity events but nothing is resumed, apps were paused/stopped
        hasAnyActivityEvents -> {
          PackageUsageState.NoForegroundPackage
        }

        // Either no activity or a failure fetching the events.
        // We treat this as Error to be safe and avoid incorrect time tracking
        else -> {
          PackageUsageState.Error
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
      PackageUsageState.Error
    }
  }

  companion object {
    private const val QUERY_WINDOW_MS = 60_000L
  }
}
