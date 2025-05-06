package com.aptoide.android.aptoidegames.permissions

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.installer.platform.UserActionLauncher
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstallPermissionsViewModel @Inject constructor(
  private val userActionLauncher: UserActionLauncher,
  @ApplicationContext private val context: Context
) : ViewModel() {

  fun requestInstallPermissions() = viewModelScope.launch {
    userActionLauncher.launchIntent(
      Intent(
        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
        "package:${context.packageName}".toUri()
      )
    )
  }
}
