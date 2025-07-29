package com.aptoide.android.aptoidegames.home

import androidx.annotation.Keep
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.appview.CustomScrollableTabRow
import com.aptoide.android.aptoidegames.appview.GenericScrollableTabRow
import com.aptoide.android.aptoidegames.home.HomeTab.Bonus
import com.aptoide.android.aptoidegames.home.HomeTab.Categories
import com.aptoide.android.aptoidegames.home.HomeTab.Editorial
import com.aptoide.android.aptoidegames.home.HomeTab.ForYou
import com.aptoide.android.aptoidegames.home.HomeTab.Rewards
import com.aptoide.android.aptoidegames.home.HomeTab.TopCharts
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
fun HomeTab.getContent(selectedTab: Boolean): @Composable () -> Unit = when (this) {
  ForYou,
  is TopCharts,
  Bonus,
  Editorial,
  Categories -> {
    @Composable {
      Text(
        text = this.getTitle(),
        style = AGTypography.InputsL,
        color = if (selectedTab) {
          Palette.Primary
        } else {
          Palette.White
        }
      )
    }
  }

  Rewards -> {
    @Composable {
      val title = this.getTitle()
      Box {
        Box(
          modifier = Modifier
            .offset(x = 5.dp, y = (-11).dp)
            .align(Alignment.TopEnd)
            .size(20.dp, 11.dp)
            .background(Palette.Primary)
            .padding(bottom = 2.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "NEW",
            style = AGTypography.InputsXS.copy(fontSize = 7.sp, lineHeight = 5.sp),
            color = Palette.Black
          )
        }

        Text(
          text = title,
          style = AGTypography.InputsL,
          color = if (selectedTab) {
            Palette.Primary
          } else {
            Palette.White
          }
        )
      }
    }
  }
}

@Composable
fun HomeTabRow(
  selectedTab: Int,
  tabsList: List<String>,
  onSelectTab: (Int) -> Unit,
) {
  CustomScrollableTabRow(
    tabs = tabsList,
    selectedTabIndex = selectedTab,
    onTabClick = onSelectTab,
    contentColor = Palette.Primary,
    backgroundColor = Color.Transparent,
    tabTextStyle = AGTypography.InputsL,
  )
}

@Composable
fun GenericHomeTabRow(
  selectedTab: Int,
  tabs: List<@Composable () -> Unit>,
  onSelectTab: (Int) -> Unit,
) {
  GenericScrollableTabRow(
    tabs = tabs,
    selectedTabIndex = selectedTab,
    onTabClick = onSelectTab,
    contentColor = Palette.Primary,
    backgroundColor = Color.Transparent,
  )
}
