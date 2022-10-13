package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.theme.AppTheme


@Composable
fun CustomScrollableTabRow(
  tabs: List<AppViewTab>,
  selectedTabIndex: Int,
  onTabClick: (AppViewTab) -> Unit,
  contentColor: Color, backgroundColor: Color
) {
  val density = LocalDensity.current
  val tabWidths = remember {
    val tabWidthStateList = mutableStateListOf<Dp>()
    repeat(tabs.size) {
      tabWidthStateList.add(0.dp)
    }
    tabWidthStateList
  }
  ScrollableTabRow(
    selectedTabIndex = selectedTabIndex,
    contentColor = contentColor,
    backgroundColor = backgroundColor,
    modifier = Modifier.wrapContentWidth(),
    edgePadding = 0.dp,
    indicator = { tabPositions ->
      TabRowDefaults.Indicator(
        modifier = Modifier.customTabIndicatorOffset(
          currentTabPosition = tabPositions[selectedTabIndex],
          tabWidth = tabWidths[selectedTabIndex]
        )
      )
    }
  ) {
    tabs.forEachIndexed { tabIndex, tab ->
      Tab(
        selected = selectedTabIndex == tabIndex,
        modifier = Modifier.fillMaxWidth(),
        onClick = { onTabClick(tab) },
        text = {
          if (selectedTabIndex == tabIndex) {
            Text(
              text = tab.tabName,
              style = AppTheme.typography.medium_M,
              color = AppTheme.colors.appViewTabRowColor,
              onTextLayout = { textLayoutResult ->
                tabWidths[tabIndex] =
                  with(density) { textLayoutResult.size.width.toDp() }
              }
            )
          } else {
            Text(
              text = tab.tabName,
              style = AppTheme.typography.medium_M,
              onTextLayout = { textLayoutResult ->
                tabWidths[tabIndex] =
                  with(density) { textLayoutResult.size.width.toDp() }
              }
            )
          }
        }
      )
    }
  }
}

fun Modifier.customTabIndicatorOffset(
  currentTabPosition: TabPosition,
  tabWidth: Dp
): Modifier = composed(
  inspectorInfo = debugInspectorInfo {
    name = "customTabIndicatorOffset"
    value = currentTabPosition
  }
) {
  val currentTabWidth by animateDpAsState(
    targetValue = tabWidth,
    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
  )
  val indicatorOffset by animateDpAsState(
    targetValue = ((currentTabPosition.left + currentTabPosition.right - tabWidth) / 2),
    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
  )
  fillMaxWidth()
    .wrapContentSize(Alignment.BottomStart)
    .offset(x = indicatorOffset)
    .width(currentTabWidth)
}