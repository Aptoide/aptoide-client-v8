package cm.aptoide.pt.oem_extractor.extractors

import android.content.Context

internal abstract class BaseOemIdExtractor(
  private val context: Context
) : IExtractOemId {
  protected fun getPackageName(packageName: String): String {
    return context.packageManager
      .getPackageInfo(packageName, 0)
      .applicationInfo.sourceDir
  }
}
