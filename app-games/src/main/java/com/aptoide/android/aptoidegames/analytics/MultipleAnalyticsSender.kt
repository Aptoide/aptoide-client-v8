package com.aptoide.android.aptoidegames.analytics

import androidx.annotation.Size
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MultipleAnalyticsSender @Inject constructor(
  private val analyticsSenderList: List<AnalyticsSender>
) : AnalyticsSender {

  override fun setUserProperties(vararg props: Pair<String, Any?>) {
    for (analyticsSender in analyticsSenderList) {
      analyticsSender.setUserProperties()
    }
  }

  override fun logEvent(
    @Size(min = 1L, max = 40L) name: String,
    params: Map<String, Any?>?,
  ) {
    for (analyticsSender in analyticsSenderList) {
      analyticsSender.logEvent(name, params)
    }
  }
}
