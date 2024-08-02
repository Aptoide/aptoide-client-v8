package cm.aptoide.pt.install_manager.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import cm.aptoide.pt.extensions.getInstalledPackages
import cm.aptoide.pt.extensions.getPackageInfo
import cm.aptoide.pt.extensions.ifNormalApp

internal class PackageInfoRepositoryImpl(
  context: Context,
) : BroadcastReceiver(),
  PackageInfoRepository {

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

  override fun getAll(): Set<PackageInfo> = pm.getInstalledPackages()
    .filter(PackageInfo::ifNormalApp)
    .toSet()

  override fun get(packageName: String): PackageInfo? = pm.getPackageInfo(packageName)
    ?.takeIf(PackageInfo::ifNormalApp)

  override fun setOnChangeListener(onChange: (String) -> Unit) {
    listener = onChange
  }
}
