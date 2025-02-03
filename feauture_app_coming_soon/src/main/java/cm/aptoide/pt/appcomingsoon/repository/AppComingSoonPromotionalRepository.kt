package cm.aptoide.pt.appcomingsoon.repository

import cm.aptoide.pt.appcomingsoon.domain.AppComingSoonCard

interface AppComingSoonPromotionalRepository {
  suspend fun getAppComingSoonCard(url: String): AppComingSoonCard
}
