package cm.aptoide.pt.extensions

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
fun Context.hasNotificationsPermission(): Boolean = ContextCompat.checkSelfPermission(
  this,
  android.Manifest.permission.POST_NOTIFICATIONS
) == PackageManager.PERMISSION_GRANTED
