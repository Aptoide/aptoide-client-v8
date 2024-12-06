package cm.aptoide.pt.install_manager.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.InstallSourceInfo
import android.content.pm.PackageInfo
import android.os.Build
import cm.aptoide.pt.extensions.getInstalledPackages
import cm.aptoide.pt.extensions.getPackageInfo
import cm.aptoide.pt.extensions.ifNormalAppOrGame

internal class AppInfoRepositoryImpl(context: Context) : BroadcastReceiver(), AppInfoRepository {

  private lateinit var listener: (String) -> Unit

  private val pm = context.packageManager

  init {
    context.registerReceiver(
      this,
      IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
        addDataScheme("package")
      }
    )
  }

  override fun onReceive(
    context: Context,
    intent: Intent,
  ) {
    intent.data
      ?.encodedSchemeSpecificPart
      ?.let(listener::invoke)
  }

  override fun getAllPackageInfos(): Set<PackageInfo> = pm.getInstalledPackages()
    .filter(PackageInfo::ifNormalAppOrGame)
    .toSet()

  override fun getPackageInfo(packageName: String): PackageInfo? = pm.getPackageInfo(packageName)
    ?.takeIf(PackageInfo::ifNormalAppOrGame)

  override fun getInstallSourceInfo(packageName: String): InstallSourceInfo? = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      pm.getInstallSourceInfo(packageName)
    } else {
      null
    }
  }.getOrNull()

  override fun setOnChangeListener(onChange: (String) -> Unit) {
    listener = onChange
  }
}
