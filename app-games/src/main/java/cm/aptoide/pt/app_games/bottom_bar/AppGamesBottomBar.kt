package cm.aptoide.pt.app_games.bottom_bar

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.app_games.home.BottomBarMenus
import cm.aptoide.pt.app_games.home.BottomBarMenus.Games
import cm.aptoide.pt.app_games.home.BottomBarMenus.Search
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.app_games.theme.AptoideTheme
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.extensions.runPreviewable
import kotlin.random.Random

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@PreviewAll
@Composable
fun AppGamesBottomBarPreview() {
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    Scaffold(
      bottomBar = {
        AppGamesBottomBar(
          navController = rememberNavController()
        )
      }
    ) {}
  }
}

@Composable
fun AppGamesBottomBar(
  modifier: Modifier = Modifier,
  navController: NavController,
) {
  val selection = selectionIndex(items = bottomNavigationItems, navController = navController)
  if (selection >= 0) {
    BottomNavigation(
      modifier = modifier,
      backgroundColor = AppTheme.colors.background,
    ) {
      bottomNavigationItems.forEachIndexed { index, item ->
        val isSelected = selection == index
        AddBottomNavigationItem(
          item = item,
          isSelected = isSelected,
          onItemClicked = {
            navController.navigate(item.route) {
              popUpTo(navController.graph.startDestinationId) {
                inclusive =
                  Games.route == item.route
                    && Games.route == navController.currentDestination?.route
              }
              launchSingleTop = true
            }
          }
        )
      }
    }
  }
}

@Composable
fun RowScope.AddBottomNavigationItem(
  item: BottomBarMenus,
  isSelected: Boolean,
  onItemClicked: () -> Unit,
) {
  BottomNavigationItem(
    selected = isSelected,
    onClick = onItemClicked,
    alwaysShowLabel = true,
    icon = {
      Icon(
        imageVector = item.icon,
        contentDescription = null
      )
    },
    label = {
      Text(
        text = stringResource(item.resourceId),
        color = if (isSelected) {
          AppTheme.colors.primary
        } else {
          AppTheme.colors.unselectedLabelColor
        }
      )
    },
    selectedContentColor = AppTheme.colors.primary,
    unselectedContentColor = AppTheme.colors.unselectedLabelColor,
  )
}

@Composable
private fun selectionIndex(
  items: List<BottomBarMenus>,
  navController: NavController,
): Int = runPreviewable(
  preview = { Random.nextInt(items.size) },
  real = {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry
      ?.destination
      ?.route
      ?.split("?")
      ?.firstOrNull()
    items.indexOfFirst { it.route.split("?").firstOrNull() == currentRoute }
  }
)

val bottomNavigationItems = listOf(
  Games,
  Search,
)
