package com.aptoide.android.aptoidegames.home

import androidx.annotation.Keep
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.appview.CustomScrollableTabRow
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

val defaultHomeTabs = listOf<HomeTab>(
  HomeTab.ForYou,
  HomeTab.TopCharts(),
  HomeTab.Bonus,
  HomeTab.Editorial,
  HomeTab.Categories,
  HomeTab.Rewards
)

@Keep
sealed class HomeTab {
  @Keep
  object ForYou : HomeTab()

  @Keep
  data class TopCharts(val sort: String = "pdownloads") : HomeTab()

  @Keep
  object Bonus : HomeTab()

  @Keep
  object Editorial : HomeTab()

  @Keep
  object Categories : HomeTab()

  @Keep
  object Rewards : HomeTab()

  @Composable
  fun getTitle() = stringResource(
    when (this) {
      ForYou -> R.string.tag_for_you
      is TopCharts -> R.string.tag_top_charts
      Bonus -> R.string.tag_bonus
      Editorial -> R.string.tag_editorial
      Categories -> R.string.tag_categories
      Rewards -> R.string.tag_rewards
    }
  )
}

@Composable
fun HomeTabRow(
  selectedTab: Int,
  tabsList: List<String>,
  onSelectTab: (Int) -> Unit,
) {
  val rewardsTabName = stringResource(R.string.tag_rewards)

  val tabBadges: List<(@Composable BoxScope.() -> Unit)?> = tabsList.map { tabName ->
    if (tabName == rewardsTabName) {
      @Composable { NewBadge() }
    } else {
      null
    }
  }

  CustomScrollableTabRow(
    modifier = Modifier
      .padding(top = 8.dp, bottom = 16.dp)
      .wrapContentHeight(unbounded = true),
    tabs = tabsList,
    selectedTabIndex = selectedTab,
    onTabClick = onSelectTab,
    contentColor = Palette.Primary,
    backgroundColor = Color.Transparent,
    tabTextStyle = AGTypography.InputsL,
    tabBadges = tabBadges
  )
}

@Composable
fun BoxScope.NewBadge() {
  Box(
    modifier = Modifier
      .align(Alignment.TopEnd)
      .offset(x = 5.dp, y = (-14).dp)
      .background(color = Palette.Primary),
    contentAlignment = Alignment.Center
  ) {
    Text(
      modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp),
      text = stringResource(R.string.new_badge).uppercase(),
      style = TextStyle(
        fontFamily = AGTypography.InputsM.fontFamily,
        fontWeight = AGTypography.InputsM.fontWeight,
        fontSize = 7.sp,
        lineHeight = 5.sp
      ),
      color = Palette.Black
    )
  }
}
