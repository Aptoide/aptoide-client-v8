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
import com.google.gson.Gson
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

    var tabs: List<HomeTab>? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
      showHomeTabRow = vm.featureFlags.getFlag("show_home_tabs", false)

      try {
        val tabsJson = vm.featureFlags.getFlagAsString("home_tabs")
        tabs = Gson().fromJson(tabsJson, FeatureFlagTabRow::class.java).tabs.mapNotNull {
          runCatching { HomeTab.valueOf(it) }.getOrNull()
        }
      } catch (e: Throwable) {
        Timber.e(e)
      }
    }

    showHomeTabRow to (tabs ?: defaultHomeTabs)
  }
)

@Keep
data class FeatureFlagTabRow(
  val tabs: List<String>
)
