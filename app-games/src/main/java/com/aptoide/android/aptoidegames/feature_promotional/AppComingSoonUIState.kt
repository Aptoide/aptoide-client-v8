package com.aptoide.android.aptoidegames.feature_promotional

import cm.aptoide.pt.appcomingsoon.domain.SubscribedAppComingSoonCard

sealed class AppComingSoonUIState {
  data class Idle(
    val subscribedAppComingSoonCard: SubscribedAppComingSoonCard
  ) : AppComingSoonUIState()

  object Loading : AppComingSoonUIState()
  object NoConnection : AppComingSoonUIState()
  object Error : AppComingSoonUIState()
}
