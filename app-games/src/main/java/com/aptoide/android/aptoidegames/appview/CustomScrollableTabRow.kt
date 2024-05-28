package com.aptoide.android.aptoidegames.appview

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun CustomScrollableTabRow(
  tabs: List<AppViewTab>,
  selectedTabIndex: Int,
  onTabClick: (Int) -> Unit,
  contentColor: Color,
  backgroundColor: Color,
) {
  val density = LocalDensity.current
  val indicatorWidths = remember(key1 = tabs.size) { MutableList(tabs.size) { 0.dp } }

  ScrollableTabRow(
    selectedTabIndex = selectedTabIndex,
    contentColor = contentColor,
    backgroundColor = backgroundColor,
    modifier = Modifier
      .padding(top = 24.dp)
      .height(40.dp),
    edgePadding = 0.dp,
    indicator = { tabPositions ->
      TabRowDefaults.Indicator(
        modifier = Modifier.customTabIndicatorOffset(
          currentTabPosition = tabPositions[selectedTabIndex],
          tabWidth = indicatorWidths[selectedTabIndex]
        )
      )
    },
    divider = {
      Box {}
    }
  ) {
    tabs.forEachIndexed { tabIndex, tab ->
      Tab(
        selected = selectedTabIndex == tabIndex,
        onClick = { onTabClick(tabIndex) },
        text = {
          Text(
            text = tab.getTabName(),
            style = AppTheme.typography.inputs_L,
            color = if (selectedTabIndex == tabIndex) {
              Palette.Primary
            } else {
              Palette.White
            },
            onTextLayout = { textLayoutResult ->
              indicatorWidths[tabIndex] =
                with(density) { textLayoutResult.size.width.toDp() }
            }
          )
        }
      )
    }
  }
}

private fun Modifier.customTabIndicatorOffset(
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

@Composable
fun AppViewTab.getTabName(): String = when (this) {
  AppViewTab.DETAILS -> "Details"
  AppViewTab.RELATED -> "Related"
  AppViewTab.INFO -> "Info"
}

enum class AppViewTab {
  DETAILS,
  RELATED,
  INFO
}

@PreviewDark
@Composable
fun CustomScrollableTabRowPreview() {
  val tabsList = List(Random.nextInt(1..3)) { AppViewTab.values()[it] }

  AptoideTheme {
    CustomScrollableTabRow(
      tabs = tabsList,
      selectedTabIndex = Random.nextInt(tabsList.size),
      onTabClick = {},
      contentColor = Palette.Primary,
      backgroundColor = Color.Transparent
    )
  }
}
