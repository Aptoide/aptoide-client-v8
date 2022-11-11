package cm.aptoide.pt.feature_appview.presentation

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.DetailedApp
import cm.aptoide.pt.feature_appview.domain.model.RelatedCard

data class AppViewUiState(
  val app: DetailedApp?,
  val isLoading: Boolean,
  val selectedTab: AppViewTab,
  val tabsList: List<AppViewTab>,
  val similarAppsList: List<App>,
  val similarAppcAppsList: List<App>,
  val otherVersionsList: List<App>,
  val relatedContent: List<RelatedCard>
)
