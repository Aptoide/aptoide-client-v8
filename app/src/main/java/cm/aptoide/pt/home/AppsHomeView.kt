package cm.aptoide.pt.home

import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.aptoide_ui.animations.staticComposable
import cm.aptoide.pt.feature_home.presentation.bundlesList

const val appsRoute = "appsView"

fun NavGraphBuilder.appsScreen(
  navigate: (String) -> Unit,
) = staticComposable(
  appsRoute
) {
  val (viewState, _) = bundlesList(context = "home_applications")
  BundlesView(viewState, navigate)
}
