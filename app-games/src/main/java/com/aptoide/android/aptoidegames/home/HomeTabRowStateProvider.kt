package com.aptoide.android.aptoidegames.home

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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeTabRowInjectionsProvider @Inject constructor(
  val featureFlags: FeatureFlags,
) : ViewModel()

@Composable
fun rememberHomeTabRowState(): Boolean = runPreviewable(
  preview = { true },
  real = {
    val vm = hiltViewModel<HomeTabRowInjectionsProvider>()
    var showHomeTabRow by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
      showHomeTabRow = vm.featureFlags.getFlag("show_home_tabs", false)
    }

    showHomeTabRow
  }
)
