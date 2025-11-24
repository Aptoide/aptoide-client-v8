package com.aptoide.android.aptoidegames.appview

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun AppViewTabRow(
  selectedTabIndex: Int,
  tabsList: List<AppViewTab>,
  onSelectTab: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  CustomScrollableTabRow(
    modifier = modifier
      .padding(top = 24.dp, bottom = 4.dp)
      .height(40.dp),
    tabs = tabsList.map { it.getTabName() },
    selectedTabIndex = selectedTabIndex,
    onTabClick = onSelectTab,
    contentColor = Palette.Primary,
    backgroundColor = Color.Transparent
  )
}

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
