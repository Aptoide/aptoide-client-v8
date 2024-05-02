package com.aptoide.android.aptoidegames.installer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import cm.aptoide.pt.extensions.isAllowed
import cm.aptoide.pt.install_manager.AbortException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface UninstallPermissions {
  /**
   * Asks for uninstallation permissions. Throws [AbortException] if user denies.
   */
  suspend fun checkIfCanUninstall()
}

@Singleton
class UninstallPermissionsImpl @Inject constructor(
  @ApplicationContext
  private val context: Context,
  private val userActionLauncher: UserActionLauncher,
) : UninstallPermissions {

  override suspend fun checkIfCanUninstall() {
    if (!context.isAllowed(Manifest.permission.REQUEST_DELETE_PACKAGES)) {
      when (userActionLauncher.requestPermissions(Manifest.permission.REQUEST_DELETE_PACKAGES)) {
        true -> Unit
        null -> if (userActionLauncher.confirm(UserConfirmation.REQUEST_DELETE_PACKAGES_RATIONALE)) {
          if (userActionLauncher.requestPermissions(Manifest.permission.REQUEST_DELETE_PACKAGES) != true) {
            throw AbortException("Not allowed")
          }
        } else {
          throw AbortException("Not allowed")
        }

        false -> if (userActionLauncher.confirm(UserConfirmation.REQUEST_DELETE_PACKAGES)) {
          userActionLauncher.launchIntent(
            Intent(
              Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
              Uri.parse("package:${context.packageName}")
            )
          )
          if (!context.isAllowed(Manifest.permission.REQUEST_DELETE_PACKAGES)) throw AbortException(
            "Not allowed"
          )
        } else {
          throw AbortException("Not allowed")
        }
      }
    }
  }
}
