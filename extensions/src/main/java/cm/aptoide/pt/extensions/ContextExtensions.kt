package cm.aptoide.pt.extensions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import androidx.core.content.ContextCompat
import timber.log.Timber

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

fun Context.getAppIconDrawable(packageName: String): Drawable? =
  packageManager.getPackageInfo(packageName)?.applicationInfo?.loadIconDrawable(packageManager)

fun Context.getAppName(packageName: String): String =
  packageManager.getPackageInfo(packageName)?.applicationInfo?.loadLabel(packageManager).toString()

fun Context.getAppSize(packageName: String): Long =
  packageManager.getPackageInfo(packageName)?.getAppSize() ?: 0

fun Context.getAppVersionName(packageName: String): String =
  packageManager.getPackageInfo(packageName)?.versionName.toString()

fun Context.openUrlInBrowser(url: String) {
  val browserIntent = Intent(Intent.ACTION_VIEW)
  browserIntent.setData(Uri.parse(url))
  ContextCompat.startActivity(this, browserIntent, null)
}

fun Context.sendMail(
  destinationEmail: String,
  subject: String,
  body: String = "",
) {
  try {
    val intent = Intent(Intent.ACTION_VIEW)
    val data = Uri.parse("mailto:$destinationEmail?subject=$subject&body=$body")
    intent.data = data
    startActivity(intent)
  } catch (t: Throwable) {
    Timber.e(t)
  }
}
