package com.aptoide.android.aptoidegames.analytics

import androidx.annotation.Size
import com.indicative.client.android.Indicative
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IndicativeAnalyticsSender @Inject constructor() : AnalyticsSender {
  override fun setUserProperties(vararg props: Pair<String, Any?>) =
    Indicative.addProperties(props.toMap())

  override fun logEvent(
    @Size(min = 1L, max = 40L) name: String,
    params: Map<String, Any?>?,
  ) = Indicative.recordEvent(name, params)
}
