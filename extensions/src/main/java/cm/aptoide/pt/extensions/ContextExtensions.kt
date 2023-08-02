package cm.aptoide.pt.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat

val Context.isActiveNetworkMetered
  get() = (getSystemService(
    Context.CONNECTIVITY_SERVICE
  ) as ConnectivityManager).isActiveNetworkMetered

@SuppressLint("InlinedApi")
fun Context.hasNotificationsPermission(): Boolean =
  isAllowed(Manifest.permission.POST_NOTIFICATIONS)

@SuppressLint("InlinedApi")
fun Context.isAllowed(permission: String): Boolean =
  ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
