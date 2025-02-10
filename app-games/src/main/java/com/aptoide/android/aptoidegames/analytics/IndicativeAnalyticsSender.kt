package com.aptoide.android.aptoidegames.analytics

import androidx.annotation.Size
import com.aptoide.android.aptoidegames.BuildConfig
import com.indicative.client.android.Indicative
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IndicativeAnalyticsSender @Inject constructor() : AnalyticsSender {
  override fun setUserProperties(vararg props: Pair<String, Any?>) {
    if (BuildConfig.DEBUG) {
      props.forEach {
        Timber.tag("BA").i("set UP  ${it.first} = ${it.second}")
      }
    }
    //Add new properties
    Indicative.addProperties(props.toMap())

    //Remove properties with null values
    props.filter { it.second == null }.forEach { Indicative.removeProperty(it.first) }
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
