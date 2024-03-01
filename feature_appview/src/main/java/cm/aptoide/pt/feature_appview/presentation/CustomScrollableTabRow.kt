package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
  onTabClick: (Int) -> Unit,
  contentColor: Color, backgroundColor: Color,
) {
  val density = LocalDensity.current
  val indicatorWidths = MutableList(tabs.size) { 0.dp }
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
          tabWidth = indicatorWidths[selectedTabIndex]
        )
      )
    }
  ) {
    tabs.forEachIndexed { tabIndex, tab ->
      Tab(
        selected = selectedTabIndex == tabIndex,
        modifier = Modifier.fillMaxWidth(),
        onClick = { onTabClick(tabIndex) },
        text = {
          if (selectedTabIndex == tabIndex) {
            Text(
              text = tab.tabName,
              style = AppTheme.typography.medium_M,
              color = AppTheme.colors.appViewTabRowColor,
              onTextLayout = { textLayoutResult ->
                indicatorWidths[tabIndex] =
                  with(density) { textLayoutResult.size.width.toDp() }
              }
            )
          } else {
            Text(
              text = tab.tabName,
              style = AppTheme.typography.medium_M,
              onTextLayout = { textLayoutResult ->
                indicatorWidths[tabIndex] =
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
  tabWidth: Dp,
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