package com.aptoide.android.aptoidegames.home

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.play_and_earn.rememberShouldShowPlayAndEarn
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeTabRowInjectionsProvider @Inject constructor(
  val featureFlags: FeatureFlags,
) : ViewModel()

@Composable
fun rememberHomeTabRowState(): Pair<Boolean, List<HomeTab>> = runPreviewable(
  preview = { true to defaultHomeTabs },
  real = {
    val vm = hiltViewModel<HomeTabRowInjectionsProvider>()
    var showHomeTabRow by remember { mutableStateOf(false) }
    val showPlayAndEarn = rememberShouldShowPlayAndEarn()

    var unfilteredTabs: List<HomeTab>? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
      showHomeTabRow = vm.featureFlags.getFlag("show_home_tabs", false)

      try {
        val tabsJson = vm.featureFlags.getFlagAsString("home_tabs")
        unfilteredTabs = Gson().fromJson(tabsJson, FeatureFlagTabRow::class.java).tabs.mapNotNull {
          when (it.id) {
            "ForYou" -> HomeTab.ForYou

            "TopCharts" -> runCatching {
              Gson().fromJson(
                it.details,
                HomeTab.TopCharts::class.java
              )
            }.getOrDefault(HomeTab.TopCharts())

            "Bonus" -> HomeTab.Bonus
            "Editorial" -> HomeTab.Editorial
            "Categories" -> HomeTab.Categories
            "Rewards" -> HomeTab.Rewards
            else -> null
          }
        }
      } catch (e: Throwable) {
        Timber.e(e)
      }
    }

    val filteredTabs = unfilteredTabs?.filterNot { it is HomeTab.Rewards && !showPlayAndEarn }

    showHomeTabRow to (filteredTabs.takeIf { !it.isNullOrEmpty() } ?: defaultHomeTabs)
  }
)

@Keep
data class FeatureFlagTabRow(
  val tabs: List<FeatureFlagTab>
)

@Keep
data class FeatureFlagTab(
  val id: String,
  val details: JsonObject
)
