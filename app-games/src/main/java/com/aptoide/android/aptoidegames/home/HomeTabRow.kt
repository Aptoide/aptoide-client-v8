package com.aptoide.android.aptoidegames.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.aptoide.android.aptoidegames.appview.CustomScrollableTabRow
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

val defaultHomeTabs = HomeTab.entries.toList()

enum class HomeTab(val title: String) {
  FOR_YOU("For You"),
  TOP_CHARTS("Top Charts"),
  APPCOINS("AppCoins"),
  EDITORIAL("Editorial"),
  CATEGORIES("Categories")
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
    tabTextStyle = AGTypography.InputsL
  )
}
