package com.aptoide.android.aptoidegames.permissions.notifications

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.hasNotificationsPermission
import com.aptoide.android.aptoidegames.launch.AppLaunchPreferencesManager
import com.aptoide.android.aptoidegames.permissions.AppPermissionsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsPermissionViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val appLaunchPreferencesManager: AppLaunchPreferencesManager,
  private val appPermissionsManager: AppPermissionsManager,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(false)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      viewModelState.update {
        appLaunchPreferencesManager.shouldShowNotificationsDialog()
          && !context.hasNotificationsPermission()
      }
    }
  }

  fun dismissDialog() {
    viewModelScope.launch {
      appLaunchPreferencesManager.setNotificationDialogShown()
      viewModelState.update { false }
    }
  }

  suspend fun canRequestPermission(
    shouldShowRationale: Boolean,
  ): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
      return false
    }

    val hasRequestedNotificationPermissions =
      appPermissionsManager.hasRequestedPermission(Manifest.permission.POST_NOTIFICATIONS)

    if (!hasRequestedNotificationPermissions && !shouldShowRationale) {
      return true
    } else if (shouldShowRationale) {
      //Should only be set here, since it is the last state before the case
      //where the user is redirected to the device settings
      appPermissionsManager.setPermissionRequested(Manifest.permission.POST_NOTIFICATIONS)
      return true
    } else {
      return false
    }
  }
}
