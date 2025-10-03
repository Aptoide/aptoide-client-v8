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
}

data class AppUsageInfo(
  val classes: MutableMap<String, ClassUsageState> = mutableMapOf(),
)

data class ClassUsageState(
  var isResumed: Boolean,
  val startTimestamp: Long
)
