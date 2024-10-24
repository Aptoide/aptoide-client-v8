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
      val hasGuestUID = !securePreferences.getString(MMP_GUEST_UID, "").isNullOrEmpty()
      val shouldRunApkfy = SecurePreferences.shouldRunApkFy(securePreferences) && !hasGuestUID
      if (shouldRunApkfy) {
        try {
          val apkfyModel = apkfyManager.getApkfy()
          saveGuestUID(apkfyModel.guestUid)
          updateApkfy(apkfyModel)
          setApkfyUtmProperties(apkfyModel)
        } catch (throwable: Throwable) {
          throwable.printStackTrace()
        }
      }
    }
  }

  private fun saveGuestUID(guestUid: String) {
    securePreferences.edit().putString(MMP_GUEST_UID, guestUid).apply()
  }

  private fun setApkfyUtmProperties(apkfyModel: ApkfyModel) {
    if (apkfyModel.hasUTMs()) {
      if (!apkfyModel.packageName.isNullOrBlank() || apkfyModel.appId != null) {
        firstLaunchAnalytics.sendIndicativeFirstLaunchSourceUserProperties(
          apkfyModel.utmContent,
          apkfyModel.utmSource,
          apkfyModel.utmCampaign,
          apkfyModel.utmMedium,
          apkfyModel.utmTerm,
          apkfyModel.packageName
        )
      } else {
        firstLaunchAnalytics.sendIndicativeFirstLaunchSourceUserProperties(
          apkfyModel.utmContent,
          apkfyModel.utmSource,
          apkfyModel.utmCampaign,
          apkfyModel.utmMedium,
          apkfyModel.utmTerm,
          APKFY_PACKAGE_NO_APP
        )
      }
    } else {
      if (!apkfyModel.packageName.isNullOrBlank() || apkfyModel.appId != null) {
        firstLaunchAnalytics.sendIndicativeFirstLaunchSourceUserProperties(
          apkfyModel.utmContent,
          apkfyModel.utmSource,
          apkfyModel.utmCampaign,
          apkfyModel.utmMedium,
          apkfyModel.utmTerm,
          APKFY_PACKAGE_APKFY_NO_UTM
        )
      } else {
        firstLaunchAnalytics.sendIndicativeFirstLaunchSourceUserProperties(
          APKFY_PACKAGE_NO_APKFY,
          APKFY_PACKAGE_NO_APKFY,
          APKFY_PACKAGE_NO_APKFY,
          APKFY_PACKAGE_NO_APKFY,
          APKFY_PACKAGE_NO_APKFY,
          APKFY_PACKAGE_NO_APKFY
        )
      }
    }
  }

  private fun updateApkfy(apkfyModel: ApkfyModel) {
    if (!apkfyModel.packageName.isNullOrBlank() && !apkfyModel.packageName.contains("cm.aptoide.pt")) {
      if (apkfyModel.appId != null) {
        intent.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true)
        intent.putExtra(DeepLinksKeys.APP_ID_KEY, apkfyModel.appId)
        if (!apkfyModel.oemId.isNullOrBlank()) {
          intent.putExtra(DeepLinksKeys.OEM_ID_KEY, apkfyModel.oemId)
        }
        intent.putExtra(DeepLinksKeys.APK_FY, true)
        SecurePreferences.setApkFyRun(securePreferences)
        context.startActivity(intent)
      } else if (!apkfyModel.packageName.isNullOrBlank()) {
        intent.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true)
        intent.putExtra(DeepLinksKeys.PACKAGE_NAME_KEY, apkfyModel.packageName)
        if (!apkfyModel.oemId.isNullOrBlank()) {
          intent.putExtra(DeepLinksKeys.OEM_ID_KEY, apkfyModel.oemId)
        }
        intent.putExtra(DeepLinksKeys.APK_FY, true)
        SecurePreferences.setApkFyRun(securePreferences)
        context.startActivity(intent)
      }
    }
  }

  companion object {
    const val APKFY_PACKAGE_NO_APP = "APKFY_BUT_NO_APP"
    const val APKFY_PACKAGE_APKFY_NO_UTM = "APKFY_BUT_NO_UTM"
    const val APKFY_PACKAGE_NO_APKFY = "NO_APKFY"
    const val MMP_GUEST_UID = "MMP_GUEST_UID"
  }
}
