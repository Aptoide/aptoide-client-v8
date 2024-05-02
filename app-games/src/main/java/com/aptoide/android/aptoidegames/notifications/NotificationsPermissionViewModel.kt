package com.aptoide.android.aptoidegames.notifications

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aptoide.android.aptoidegames.launch.AppLaunchPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsPermissionViewModel @Inject constructor(
  private val appLaunchPreferencesManager: AppLaunchPreferencesManager,
  private val notificationsPermissionManager: NotificationsPermissionManager
) :
  ViewModel() {

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
          && !notificationsPermissionManager.hasNotificationsPermission()
      }
    }
  }

  fun dismissDialog() {
    viewModelScope.launch {
      appLaunchPreferencesManager.setNotificationDialogShown()
      viewModelState.update { false }
    }
  }

  fun updatePermissionRequestedPreference(requestedPermission: Boolean) {
    viewModelScope.launch {
      notificationsPermissionManager.updatePermissionRequestedPreference(requestedPermission)
    }
  }

  fun requestPermission(shouldShowRationale: Boolean, openDialog: () -> Unit) {
    viewModelScope.launch {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        notificationsPermissionManager.openAppSystemSettings()
        return@launch
      }

      val hasRequestedNotificationPermissions =
        notificationsPermissionManager.hasRequestedPermissions()

      if (!hasRequestedNotificationPermissions && !shouldShowRationale) {
        openDialog()
      } else if (shouldShowRationale) {
        openDialog()
        //Should only be set here, since it is the last state before the case
        //where the user is redirected to the device settings
        updatePermissionRequestedPreference(true)
      } else {
        notificationsPermissionManager.openAppSystemSettings()
      }
    }
  }
}
