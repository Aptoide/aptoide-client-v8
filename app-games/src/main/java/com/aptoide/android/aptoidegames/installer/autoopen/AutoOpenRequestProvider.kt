package com.aptoide.android.aptoidegames.installer.autoopen

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

@HiltViewModel
class AutoOpenAfterInstallInjectionsProvider @Inject constructor(
  val controller: AutoOpenAfterInstallController,
  val experiment: AutoOpenAfterInstallExperiment,
  val preferences: AutoOpenAfterInstallPreferences,
) : ViewModel()

@Composable
fun rememberAutoOpenAfterInstallController(): AutoOpenAfterInstallController? = runPreviewable(
  preview = { null },
  real = { hiltViewModel<AutoOpenAfterInstallInjectionsProvider>().controller }
)

@Composable
fun rememberAutoOpenAfterInstallExperiment(): AutoOpenAfterInstallExperiment? = runPreviewable(
  preview = { null },
  real = { hiltViewModel<AutoOpenAfterInstallInjectionsProvider>().experiment }
)

@Composable
fun rememberAutoOpenAfterInstallPreferences(): AutoOpenAfterInstallPreferences? = runPreviewable(
  preview = { null },
  real = { hiltViewModel<AutoOpenAfterInstallInjectionsProvider>().preferences }
)

@Composable
fun rememberAutoOpenAfterInstallDefault(): Boolean = runPreviewable(
  preview = { true },
  real = {
    val experiment = hiltViewModel<AutoOpenAfterInstallInjectionsProvider>().experiment
    var defaultOn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { defaultOn = experiment.isDefaultOn() }
    defaultOn
  }
)
