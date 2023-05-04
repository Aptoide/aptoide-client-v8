package cm.aptoide.pt.download_view.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat

fun Context.checkIfInstallationsAllowed(): Boolean = packageManager.canRequestPackageInstalls()

fun Context.requestAllowInstallations() = ContextCompat.startActivity(
  this,
  Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName")),
  Bundle()
)
