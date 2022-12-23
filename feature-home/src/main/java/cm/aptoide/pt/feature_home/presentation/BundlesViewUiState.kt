package cm.aptoide.pt.feature_home.presentation

import cm.aptoide.pt.feature_home.domain.Bundle

data class BundlesViewUiState(
  val bundles: List<Bundle>,
  val type: BundlesViewUiStateType,
)

enum class BundlesViewUiStateType {
  IDLE, LOADING, NO_CONNECTION, ERROR
}
