package cm.aptoide.pt.aptoide_ui.animations

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
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

@OptIn(ExperimentalAnimationApi::class)
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
      towards = AnimatedContentScope.SlideDirection.Left,
      animationSpec = tween(300)
    )
  },
  exitTransition = null,
  popEnterTransition = null,
  popExitTransition = {
    slideOutOfContainer(
      towards = AnimatedContentScope.SlideDirection.Right,
      animationSpec = tween(300)
    )
  },
  content = content
)
