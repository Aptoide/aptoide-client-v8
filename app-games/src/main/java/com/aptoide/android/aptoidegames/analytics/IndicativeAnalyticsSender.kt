package com.aptoide.android.aptoidegames.analytics

import androidx.annotation.Size
import com.aptoide.android.aptoidegames.BuildConfig
import com.indicative.client.android.Indicative
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IndicativeAnalyticsSender @Inject constructor() : AnalyticsSender {
  override fun setUserProperties(vararg props: UserProperty) {
    if (BuildConfig.DEBUG) {
      props.forEach {
        Timber.tag("BA").i("set UP  ${it.name} = ${it.value}")
      }
    }
    //Add new properties
    val map = props.associate { it.name to it.value }
    Indicative.addProperties(map)

    //Remove properties with null values
    props.filter { it.value == null }.forEach { Indicative.removeProperty(it.name) }
  }

  override fun logEvent(
    @Size(min = 1L, max = 40L) name: String,
    params: Map<String, Any?>?,
  ) {
    if (BuildConfig.DEBUG) {
      Timber.tag("BA").i(name)
      params?.entries?.forEach {
        Timber.tag("BA").i("  ${it.key}: ${it.value.toString()}")
      }
    }
    Indicative.recordEvent(name, params)
  }
}
