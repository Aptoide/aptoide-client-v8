package cm.aptoide.pt.installer.platform

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import cm.aptoide.pt.extensions.hasWriteExternalStoragePermission
import cm.aptoide.pt.install_manager.AbortException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface InstallPermissions {
  /**
   * Asks for installation permissions. Throws [AbortException] if user denies.
   */
  suspend fun checkIfCanInstall()

  /**
   * Asks for write external storage permissions. Throws [AbortException] if user denies.
   */
  suspend fun checkIfCanWriteExternal()
}

@Singleton
class InstallPermissionsImpl @Inject constructor(
  @ApplicationContext
  private val context: Context,
  private val userActionLauncher: UserActionLauncher,
) : InstallPermissions {

  override suspend fun checkIfCanInstall() {
    if (!areInstallationsAllowed()) {
      if (userActionLauncher.confirm(UserConfirmation.INSTALL_SOURCE)) {
        userActionLauncher.launchIntent(
          Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:${context.packageName}")
          )
        )
      } else {
        throw AbortException("Not allowed")
      }
      if (!areInstallationsAllowed()) throw AbortException("Not allowed")
    }
  }

  override suspend fun checkIfCanWriteExternal() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
      //Android is below 11
      if (!context.hasWriteExternalStoragePermission()) {
        when (userActionLauncher.checkPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
          //Requested permission
          true -> if (!userActionLauncher.requestPermissions(
              Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
          ) {
            throw AbortException("Not allowed")
          }

          //Need to show rationale
          null -> if (userActionLauncher.confirm(UserConfirmation.WRITE_EXTERNAL_RATIONALE)) {
            if (!userActionLauncher.requestPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
              )
            ) {
              throw AbortException("Not allowed")
            }
          } else {
            throw AbortException("Not allowed")
          }

          //Need to open settings
          false -> if (userActionLauncher.confirm(UserConfirmation.WRITE_EXTERNAL)) {
            userActionLauncher.launchIntent(
              Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
              )
            )
            if (!context.hasWriteExternalStoragePermission()) throw AbortException(
              "Not allowed"
            )
          } else {
            throw AbortException("Not allowed")
          }
        }
      }
    }
  }

  private fun areInstallationsAllowed(): Boolean =
    context.packageManager.canRequestPackageInstalls()
}
