package com.aptoide.android.aptoidegames.installer.notifications

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val installerNotifications: InstallerNotificationsManager,
) : ViewModel()

@Composable
fun rememberInstallerNotifications(): InstallerNotificationsManager = runPreviewable(
  preview = { FakeInstallerNotificationsManager() },
  real = {
    val installerNotificationsProvider = hiltViewModel<InjectionsProvider>()
    installerNotificationsProvider.installerNotifications
  }
)
