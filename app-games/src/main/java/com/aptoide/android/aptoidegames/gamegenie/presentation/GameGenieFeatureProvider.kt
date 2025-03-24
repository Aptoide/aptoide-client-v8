package com.aptoide.android.aptoidegames.gamegenie.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class GameGenieInjectionsProvider @Inject constructor(
  val featureFlags: FeatureFlags,
) : ViewModel()

@Composable
fun rememberGameGenieVisibility(): Boolean = runPreviewable(
  preview = { Random.nextBoolean() },
  real = {
    val coroutineScope = rememberCoroutineScope()
    var state by remember { mutableStateOf(false) }
    val vm = hiltViewModel<GameGenieInjectionsProvider>()
    LaunchedEffect(key1 = Unit) {
      coroutineScope.launch {
        state = vm.featureFlags.getFlag("show_game_genie", false)
      }
    }
    state
  }
)
