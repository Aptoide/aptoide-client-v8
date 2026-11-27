package com.aptoide.android.aptoidegames.play_and_earn.presentation.installs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import com.aptoide.android.aptoidegames.play_and_earn.domain.installs.PaEInstallTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Hands the screens the one [PaEInstallTracker] that sends the install clicks (AND-954). */
@HiltViewModel
class PaEInstallStartedViewModel @Inject constructor(
  val installTracker: PaEInstallTracker,
) : ViewModel()

/**
 * Tells the Play & Earn install clicks that Google Play took the install of a package (AND-954):
 * those installs never reach the install manager, which tells the others.
 */
@Composable
fun rememberPaEPlayInstallStarted(): (packageName: String) -> Unit = runPreviewable(
  preview = { {} },
  real = {
    val vm = hiltViewModel<PaEInstallStartedViewModel>()
    remember(vm) { vm.installTracker::onInstallStarted }
  },
)
