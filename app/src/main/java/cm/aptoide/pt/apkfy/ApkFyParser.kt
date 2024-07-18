package cm.aptoide.pt.apkfy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import cm.aptoide.pt.DeepLinkIntentReceiver.DeepLinksKeys
import cm.aptoide.pt.DeepLinkIntentReceiver.DeepLinksTargets
import cm.aptoide.pt.analytics.FirstLaunchAnalytics
import cm.aptoide.pt.preferences.secure.SecurePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by neuro on 30-12-2016.
 */
class ApkFyParser(
  private val context: Context,
  private val intent: Intent,
  private val securePreferences: SharedPreferences,
  private val apkfyManager: ApkfyManager,
  private val firstLaunchAnalytics: FirstLaunchAnalytics
) {
  fun run() {
    CoroutineScope(Dispatchers.Main).launch {
      val shouldRunApkfy = SecurePreferences.shouldRunApkFy(securePreferences)
      if (shouldRunApkfy) {
        try {
          val apkfyModel = apkfyManager.getApkfy()
          updateApkfy(apkfyModel)
          firstLaunchAnalytics.sendIndicativeFirstLaunchSourceUserProperties(
            apkfyModel.utmContent,
            apkfyModel.utmSource,
            apkfyModel.utmCampaign,
            apkfyModel.utmMedium,
          )
        } catch (throwable: Throwable) {
          throwable.printStackTrace()
        }
      }
    }
  }

  private fun updateApkfy(apkfyModel: ApkfyModel) {
    if (!apkfyModel.packageName.isNullOrEmpty() && !apkfyModel.packageName.contains("cm.aptoide.pt")) {
      if (apkfyModel.appId != null) {
        intent.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true)
        intent.putExtra(DeepLinksKeys.APP_ID_KEY, apkfyModel.appId)
        if (!apkfyModel.oemId.isNullOrEmpty()) {
          intent.putExtra(DeepLinksKeys.OEM_ID_KEY, apkfyModel.oemId)
        }
        intent.putExtra(DeepLinksKeys.APK_FY, true)
        SecurePreferences.setApkFyRun(securePreferences)
        context.startActivity(intent)
      } else if (!apkfyModel.packageName.isNullOrEmpty()) {
        intent.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true)
        intent.putExtra(DeepLinksKeys.PACKAGE_NAME_KEY, apkfyModel.packageName)
        if (!apkfyModel.oemId.isNullOrEmpty()) {
          intent.putExtra(DeepLinksKeys.OEM_ID_KEY, apkfyModel.oemId)
        }
        intent.putExtra(DeepLinksKeys.APK_FY, true)
        SecurePreferences.setApkFyRun(securePreferences)
        context.startActivity(intent)
      }
    }
  }
}
