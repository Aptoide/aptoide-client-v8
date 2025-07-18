package com.aptoide.android.aptoidegames.home

import androidx.annotation.Keep
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
  CustomScrollableTabRow(
    modifier = Modifier.padding(top = 8.dp),
    tabs = tabsList,
    selectedTabIndex = selectedTab,
    onTabClick = onSelectTab,
    contentColor = Palette.Primary,
    backgroundColor = Color.Transparent,
    tabTextStyle = AGTypography.InputsL
  )
}
