package com.aptoide.android.aptoidegames.feature_editors_choice_recommendation

import android.content.Context
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditorsChoiceRecommendationManager @Inject constructor(
  private val featureFlags: FeatureFlags,
  @ApplicationContext private val context: Context
) {

  suspend fun initialize() {
    val delay = featureFlags.getFlagAsString("trending_notification_delay", "0").toLong()
    Timber.d("delay for editors choice apps ab test is $delay")
    if (delay > 0) {
      EditorsChoiceRecommendationAppsWorker.enqueue(context, delay)
    }
  }
}
