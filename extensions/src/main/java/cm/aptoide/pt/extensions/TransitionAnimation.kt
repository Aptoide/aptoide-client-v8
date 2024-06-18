package cm.aptoide.pt.extensions

import android.os.Bundle
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

data class ScreenData(
  val route: String,
  val arguments: List<NamedNavArgument> = emptyList(),
  val deepLinks: List<NavDeepLink> = emptyList(),
  val content: @Composable (Bundle?, (String) -> Unit, () -> Unit) -> Unit,
){
  companion object
}

fun NavGraphBuilder.staticComposable(
  navigate: (String) -> Unit,
  goBack: () -> Unit,
  screenData: ScreenData,
) = staticComposable(
  route = screenData.route,
  arguments = screenData.arguments,
  deepLinks = screenData.deepLinks,
  content = {
    screenData.content(it.arguments, navigate, goBack)
  }
)

fun NavGraphBuilder.animatedComposable(
  navigate: (String) -> Unit,
  goBack: () -> Unit,
  screenData: ScreenData,
) = animatedComposable(
  route = screenData.route,
  arguments = screenData.arguments,
  deepLinks = screenData.deepLinks,
  content = {
    screenData.content(it.arguments, navigate, goBack)
  }
)

fun NavGraphBuilder.staticComposable(
  route: String,
  arguments: List<NamedNavArgument> = emptyList(),
  deepLinks: List<NavDeepLink> = emptyList(),
  content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) = composable(
  route = route,
  arguments = arguments,
  deepLinks = deepLinks,
  enterTransition = null,
  exitTransition = null,
  popEnterTransition = null,
  popExitTransition = null,
  content = content
)

fun NavGraphBuilder.animatedComposable(
  route: String,
  arguments: List<NamedNavArgument> = emptyList(),
  deepLinks: List<NavDeepLink> = emptyList(),
  content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit,
) = composable(
  route = route,
  arguments = arguments,
  deepLinks = deepLinks,
  enterTransition = {
    slideIntoContainer(
      towards = AnimatedContentTransitionScope.SlideDirection.Left,
      animationSpec = tween(300)
    )
  },
  exitTransition = null,
  popEnterTransition = null,
  popExitTransition = {
    slideOutOfContainer(
      towards = AnimatedContentTransitionScope.SlideDirection.Right,
      animationSpec = tween(300)
    )
  },
  content = content
)
