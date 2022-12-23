package cm.aptoide.pt.feature_appview.presentation

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_appview.domain.model.RelatedCard

data class AppViewUiState(
  val app: App?,
  val type: AppViewUiStateType,
  val selectedTab: Pair<AppViewTab, Int>,
  val tabsList: List<Pair<AppViewTab, Int>>,
  val similarAppsList: List<App>,
  val similarAppcAppsList: List<App>,
  val otherVersionsList: List<App>,
  val relatedContent: List<RelatedCard>
)

enum class AppViewUiStateType {
  IDLE, LOADING, NO_CONNECTION, ERROR
}
