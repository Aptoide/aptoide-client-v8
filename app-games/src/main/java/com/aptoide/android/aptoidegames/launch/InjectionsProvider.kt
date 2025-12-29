package com.aptoide.android.aptoidegames.launch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val appLaunchPreferencesManager: AppLaunchPreferencesManager,
) : ViewModel()

@Composable
fun rememberIsFirstLaunch(): Boolean = runPreviewable(
  preview = { Random.nextBoolean() },
  real = {
    val vm = hiltViewModel<InjectionsProvider>()
    var isFirstLaunch by remember { mutableStateOf(false) }
    LaunchedEffect(vm) {
      isFirstLaunch = vm.appLaunchPreferencesManager.isFirstLaunch()
    }
    isFirstLaunch
  }
)
