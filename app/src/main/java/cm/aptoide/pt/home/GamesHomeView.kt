package cm.aptoide.pt.home

import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.aptoide_ui.animations.staticComposable
import cm.aptoide.pt.feature_home.presentation.bundlesList

const val gamesRoute = "gamesView"

fun NavGraphBuilder.gamesScreen(
  navigate: (String) -> Unit,
) = staticComposable(
  gamesRoute
) {
  val (viewState, _) = bundlesList(context = "home_games")
  BundlesView(viewState, navigate)
}
