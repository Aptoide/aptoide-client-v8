package com.aptoide.android.aptoidegames.appview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.play_and_earn.presentation.components.animations.RewardsStarsAnimation
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun AppViewTabRow(
  selectedTabIndex: Int,
  tabsList: List<AppViewTab>,
  onSelectTab: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {

  val selectedTab = tabsList[selectedTabIndex]
  val density = LocalDensity.current

  var tabPositions by remember { mutableStateOf<List<TabPositionInfo>>(emptyList()) }
  val rewardsTabIndex = tabsList.indexOf(AppViewTab.REWARDS)

  Box(modifier = modifier) {
    CustomScrollableTabRow(
      modifier = Modifier.fillMaxWidth(),
      tabs = tabsList.map { it.getTabName() },
      selectedTabIndex = selectedTabIndex,
      onTabClick = onSelectTab,
      contentColor = if (selectedTab == AppViewTab.REWARDS) {
        Palette.Yellow100
      } else {
        Palette.Primary
      },
      backgroundColor = Color.Transparent,
      tabTextStyle = AGTypography.InputsL,
      onTabPositioned = { index, x, width ->
        val updatedPositions = tabPositions.toMutableList()
        while (updatedPositions.size <= index) {
          updatedPositions.add(TabPositionInfo(0f, 0f))
        }
        updatedPositions[index] = TabPositionInfo(x, width)
        tabPositions = updatedPositions
      }
    )

    // Overlay animation for Rewards tab
    if (rewardsTabIndex >= 0) {
      val tabPosition = tabPositions.getOrNull(rewardsTabIndex)

      if (tabPosition != null) {
        val widthDp = with(density) { tabPosition.width.toDp() }

        Box(
          modifier = Modifier
            .wrapContentHeight(unbounded = true, align = Alignment.Bottom)
            .offset { IntOffset(x = tabPosition.x.toInt(), y = 0) }
            .width(widthDp)
            .offset(y = widthDp.times(0.12f)),
        ) {
          RewardsStarsAnimation(
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
          )
        }
      }
    }
  }
}

private data class TabPositionInfo(
  val x: Float,
  val width: Float
)

@Composable
fun AppViewTab.getTabName(): String = stringResource(
  when (this) {
    AppViewTab.DETAILS -> R.string.appview_details_tab_title
    AppViewTab.REWARDS -> R.string.appview_rewards_tab_title
    AppViewTab.RELATED -> R.string.appview_related_tab_title
    AppViewTab.INFO -> R.string.appview_info_tab_title
  }
)

enum class AppViewTab {
  DETAILS,
  REWARDS,
  RELATED,
  INFO
}

@Preview(showBackground = true)
@Composable
fun AppViewTabRowPreview() {
  AptoideTheme {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF1E1E26))
        .padding(top = 24.dp, bottom = 4.dp)
        .height(40.dp)
    ) {
      AppViewTabRow(
        selectedTabIndex = Random.nextInt(0..AppViewTab.entries.size - 1),
        tabsList = AppViewTab.entries,
        onSelectTab = {}
      )
    }
  }
}
