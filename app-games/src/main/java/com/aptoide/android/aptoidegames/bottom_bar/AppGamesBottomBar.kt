package com.aptoide.android.aptoidegames.bottom_bar

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.home.BottomBarMenus
import com.aptoide.android.aptoidegames.home.Icon
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@PreviewDark
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
fun AppGamesBottomBar(navController: NavController) {
  val genericAnalytics = rememberGenericAnalytics()
  val selection = selectionIndex(items = bottomNavigationItems, navController = navController)
  if (selection >= 0) {
    AppGamesBottomNavigation(backgroundColor = Color.Transparent) {
      bottomNavigationItems.forEachIndexed { index, item ->
        val isSelected = selection == index
        AddBottomNavigationItem(
          item = item,
          isSelected = isSelected,
          onItemClicked = {
            when (item) {
              BottomBarMenus.Games -> genericAnalytics.sendBottomBarHomeClick()
              BottomBarMenus.Search -> genericAnalytics.sendBottomBarSearchClick()
              BottomBarMenus.Categories -> genericAnalytics.sendBottomBarCategoriesClick()
              BottomBarMenus.Updates -> genericAnalytics.sendBottomBarUpdatesClick()
            }
            navController.navigate(item.route) {
              popUpTo(navController.graph.startDestinationId) {
                inclusive =
                  BottomBarMenus.Games.route == item.route
                    && BottomBarMenus.Games.route == navController.currentDestination?.route
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
    modifier = Modifier.fillMaxHeight(),
    selected = isSelected,
    onClick = onItemClicked,
    alwaysShowLabel = true,
    icon = { item.Icon() },
    label = {
      Text(
        text = stringResource(id = item.titleId),
        style = if (isSelected) {
          AGTypography.BodyBold
        } else {
          AGTypography.Body
        },
        color = if (isSelected) {
          Palette.Primary
        } else {
          Palette.GreyLight
        }
      )
    },
    selectedContentColor = Palette.Primary,
    unselectedContentColor = Palette.GreyLight,
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
  BottomBarMenus.Games,
  BottomBarMenus.Search,
  BottomBarMenus.Categories,
  BottomBarMenus.Updates,
)

/**
Custom implementation of the Material Design BottomNavigation, that receives
a customizable horizontal arrangement
 */
@Composable
private fun AppGamesBottomNavigation(
  modifier: Modifier = Modifier,
  horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
  backgroundColor: Color = MaterialTheme.colors.primarySurface,
  contentColor: Color = contentColorFor(backgroundColor),
  elevation: Dp = 0.dp,
  content: @Composable RowScope.() -> Unit,
) {
  Surface(
    color = backgroundColor,
    contentColor = contentColor,
    elevation = elevation,
    modifier = modifier
  ) {
    Row(
      Modifier
        .fillMaxWidth()
        .height(BottomNavigationHeight)
        .selectableGroup(),
      horizontalArrangement = horizontalArrangement,
      content = content
    )
  }
}

private val BottomNavigationHeight = 74.dp
