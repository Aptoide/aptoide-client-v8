package cm.aptoide.pt.home

import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.aptoide_ui.animations.staticComposable
import cm.aptoide.pt.feature_home.presentation.bundlesList

const val bonusRoute = "bonusView"

fun NavGraphBuilder.bonusScreen(
  navigate: (String) -> Unit,
) = staticComposable(
  bonusRoute
) {
  val (viewState, _) = bundlesList()
  BundlesView(viewState, navigate)
}
