package com.aptoide.android.aptoidegames.usage_stats

import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.USAGE_STATS_SERVICE
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PackageUsageStatsProvider @Inject constructor(
  @ApplicationContext private val context: Context
) {
  val usageStatsManager = context.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

  private fun getUsageStats(packageName: String): UsageStats? {
    val startTime = System.currentTimeMillis() - 1000 * 60 * 60 * 24 //1 hour
    val endTime = System.currentTimeMillis()

    //Timber.tag("usage_event").d("Stat | Start time: $startTime; $endTime: $endTime")

    val usageStatsList: MutableList<UsageStats> = usageStatsManager.queryUsageStats(
      UsageStatsManager.INTERVAL_BEST,
      startTime,
      endTime
    )

    val targetStats = usageStatsList.find { it.packageName == packageName }


    /*
    val usageStatsList: Map<String, UsageStats> = usageStatsManager.queryAndAggregateUsageStats(startTime, endTime)

    val targetStats = usageStatsList[packageName]


     */

    return targetStats
  }

  fun getTotalTimeInForeground(packageName: String) =
    getUsageStats(packageName)?.totalTimeInForeground

  private fun getUsageEvents() {
    val startTime = System.currentTimeMillis() - 10000
    val endTime = System.currentTimeMillis()

    val usageEvents: UsageEvents = usageStatsManager.queryEvents(startTime, endTime)

    val event = UsageEvents.Event()

    while (usageEvents.hasNextEvent()) {
      usageEvents.getNextEvent(event)

      if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED && event.packageName == "cm.aptoide.pt") {
        println("is it in foreground? It could have moved to background meanwhile")
      }
    }
  }

  private fun getForegroundApp() {
  }
}
