package com.aptoide.android.aptoidegames.feature_promotional.domain

import android.content.Context
import cm.aptoide.pt.appcomingsoon.domain.SubscribedAppComingSoonCard
import cm.aptoide.pt.appcomingsoon.repository.AppComingSoonPromotionalRepository
import cm.aptoide.pt.appcomingsoon.repository.SubscribedAppsRepository
import com.aptoide.android.aptoidegames.feature_promotional.repository.AppComingSoonPromotionalWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppComingSoonManager @Inject constructor(
  private val repository: AppComingSoonPromotionalRepository,
  private val subscribedAppsRepository: SubscribedAppsRepository,
  @ApplicationContext private val context: Context
) {

  @OptIn(ExperimentalCoroutinesApi::class)
  suspend fun loadAppComingSoonCard(cardUrl: String): Flow<SubscribedAppComingSoonCard> {
    return flowOf(repository.getAppComingSoonCard(cardUrl))
      .flatMapLatest { card ->
        subscribedAppsRepository.isAppSubscribed(card.packageName)
          .map { SubscribedAppComingSoonCard(appComingSoonCard = card, isSubscribed = it) }
      }
  }

  suspend fun updateSubscribedApp(packageName: String, subscribe: Boolean) {
    subscribedAppsRepository.saveSubscribedApp(packageName, subscribe)
    if (subscribe) {
      AppComingSoonPromotionalWorker.enqueue(context, packageName)
    } else {
      AppComingSoonPromotionalWorker.cancel(context, packageName)
    }
  }
}
