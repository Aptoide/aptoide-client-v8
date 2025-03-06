package com.aptoide.android.aptoidegames.feature_ad

import android.content.Context
import android.view.View
import com.aptoide.android.aptoidegames.BuildConfig
import com.mbridge.msdk.MBridgeConstans
import com.mbridge.msdk.out.Campaign
import com.mbridge.msdk.out.MBNativeHandler
import com.mbridge.msdk.out.MBridgeSDKFactory
import com.mbridge.msdk.out.SDKInitStatusListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Mintegral @Inject constructor(
  @ApplicationContext private val context: Context
) {
  private var nativeHandler: MBNativeHandler? = null

  fun initializeSdk() {
    val sdk = MBridgeSDKFactory.getMBridgeSDK()
    val configMap = sdk.getMBConfigurationMap(
      BuildConfig.MINTEGRAL_APP_ID,
      BuildConfig.MINTEGRAL_APP_KEY
    )

    sdk.init(configMap, context, object : SDKInitStatusListener {
      override fun onInitSuccess() {
        val preloadMap: MutableMap<String, Any> = HashMap()
        preloadMap[MBridgeConstans.PROPERTIES_LAYOUT_TYPE] = MBridgeConstans.LAYOUT_NATIVE
        preloadMap[MBridgeConstans.PROPERTIES_UNIT_ID] = BuildConfig.NATIVE_UNIT_ID
        preloadMap[MBridgeConstans.PLACEMENT_ID] = BuildConfig.NATIVE_PLACEMENT_ID
        sdk.preload(preloadMap)
      }

      override fun onInitFail(errorMsg: String) {
      }
    })
  }

  fun initNativeAd(adClick: (String) -> Unit): Flow<Campaign?> {
    val appContext = context
    val properties = MBNativeHandler.getNativeProperties(
      BuildConfig.NATIVE_PLACEMENT_ID,
      BuildConfig.NATIVE_UNIT_ID
    )
    nativeHandler = MBNativeHandler(properties, appContext)

    return callbackFlow {
      trySend(null)
      nativeHandler?.setAdListener(object : AbstractNativeAdListener {
        override fun onAdLoaded(campaigns: List<Campaign?>?, totalCount: Int) {
          super.onAdLoaded(campaigns, totalCount)
          trySend(campaigns?.firstOrNull())
        }

        override fun onAdClick(campaign: Campaign) {
          adClick(campaign.packageName)
          super.onAdClick(campaign)
        }
      })

      nativeHandler?.trackingListener = object : AbstractNativeTrackingListener {
        override fun onStartRedirection(campaign: Campaign?, redirectUrl: String?) {
          if (!redirectUrl.isNullOrEmpty()) {
            httpKnock(redirectUrl)
          }
        }
      }
      nativeHandler?.load()
      awaitClose {
      }
    }
  }

  fun registerNativeAdView(view: View, campaign: Campaign) {
    val handler = nativeHandler
      ?: throw IllegalStateException("Native handler not initialized")
    handler.registerView(view, campaign)
  }

  fun httpKnock(url: String) {
    val client = OkHttpClient()
    val request = Request.Builder()
      .url(url)
      .build()

    client.newCall(request).execute()
  }
}
