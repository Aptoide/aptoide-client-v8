package cm.aptoide.pt.install_manager.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import cm.aptoide.pt.extensions.getInstalledPackages
import cm.aptoide.pt.extensions.getPackageInfo
import cm.aptoide.pt.extensions.ifNormalApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class PackageInfoRepositoryImpl(
  context: Context,
  private val scope: CoroutineScope,
) : BroadcastReceiver(),
  PackageInfoRepository {

  private lateinit var listener: suspend (String) -> Unit

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

  override fun onReceive(context: Context, intent: Intent) {
    intent.data?.encodedSchemeSpecificPart?.let {
      scope.launch {
        listener.invoke(it)
      }
    }
  }

  override suspend fun getAll(): Set<PackageInfo> = pm.getInstalledPackages()
    .filter(PackageInfo::ifNormalApp)
    .toSet()

  override suspend fun get(packageName: String): PackageInfo? = pm.getPackageInfo(packageName)
    ?.takeIf(PackageInfo::ifNormalApp)

  override fun setOnChangeListener(onChange: suspend (String) -> Unit) {
    listener = onChange
  }
}
