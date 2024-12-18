package cm.aptoide.pt.installer.platform

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import cm.aptoide.pt.extensions.hasPackageInstallsPermission
import cm.aptoide.pt.extensions.hasWriteExternalStoragePermission
import cm.aptoide.pt.install_manager.AbortException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

const val REQUEST_INSTALL_PACKAGES_NOT_ALLOWED = "REQUEST_INSTALL_PACKAGES permission not allowed"
const val REQUEST_INSTALL_PACKAGES_RATIONALE_REJECTED = "REQUEST_INSTALL_PACKAGES permission rationale rejected"
const val WRITE_EXTERNAL_STORAGE_NOT_ALLOWED = "WRITE_EXTERNAL_STORAGE permission not allowed"
const val WRITE_EXTERNAL_STORAGE_RATIONALE_REJECTED = "WRITE_EXTERNAL_STORAGE permission rationale rejected"

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
    if (!context.hasPackageInstallsPermission()) {
      if (userActionLauncher.confirm(UserConfirmation.INSTALL_SOURCE)) {
        userActionLauncher.launchIntent(
          Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:${context.packageName}")
          )
        )
      } else {
        throw AbortException(REQUEST_INSTALL_PACKAGES_RATIONALE_REJECTED)
      }
      if (!context.hasPackageInstallsPermission()) throw AbortException(
        REQUEST_INSTALL_PACKAGES_NOT_ALLOWED
      )
    }
  }

  override suspend fun checkIfCanWriteExternal() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
      //Android is below 13
      if (!context.hasWriteExternalStoragePermission()) {
        when (userActionLauncher.checkPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
          //Requested permission
          true -> if (!userActionLauncher.requestPermissions(
              Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
          ) {
            throw AbortException(WRITE_EXTERNAL_STORAGE_NOT_ALLOWED)
          }

          //Need to show rationale
          null -> if (userActionLauncher.confirm(UserConfirmation.WRITE_EXTERNAL_RATIONALE)) {
            if (!userActionLauncher.requestPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
              )
            ) {
              throw AbortException(WRITE_EXTERNAL_STORAGE_NOT_ALLOWED)
            }
          } else {
            throw AbortException(WRITE_EXTERNAL_STORAGE_RATIONALE_REJECTED)
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
              WRITE_EXTERNAL_STORAGE_NOT_ALLOWED
            )
          } else {
            throw AbortException(WRITE_EXTERNAL_STORAGE_RATIONALE_REJECTED)
          }
        }
      }
    }
  }
}
