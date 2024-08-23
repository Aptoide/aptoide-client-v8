package com.aptoide.android.aptoidegames.permissions

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppPermissionsViewModel @Inject constructor(
  private val appPermissionsManager: AppPermissionsManager,
) : ViewModel() {

  suspend fun hasRequestedPermission(permission: String) =
    appPermissionsManager.hasRequestedPermission(permission)

  suspend fun setPermissionRequested(permission: String) =
    appPermissionsManager.setPermissionRequested(permission)
}
