package com.appcoins.billing.sdk

import android.content.pm.PackageManager
import android.os.Binder
import android.os.Bundle
import android.os.Parcel
import com.appcoins.billing.AppcoinsBilling
import javax.inject.Inject

class AppcoinsBillingBinder @Inject constructor(
  private val packageManager: PackageManager,
) : AppcoinsBilling.Stub() {

  companion object {
    internal const val RESULT_BILLING_UNAVAILABLE =
      3 // this billing API version is not supported for the type requested
    internal const val RESULT_DEVELOPER_ERROR = 5 // invalid arguments provided to the API
    const val RESPONSE_CODE = "RESPONSE_CODE"
  }

  private var merchantName: String? = null

  override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
    merchantName = packageManager.getPackagesForUid(Binder.getCallingUid())?.firstOrNull()
    return super.onTransact(code, data, reply, flags)
  }

  override fun isBillingSupported(apiVersion: Int, packageName: String?, type: String?): Int {
    return RESULT_BILLING_UNAVAILABLE
  }

  override fun getSkuDetails(
    apiVersion: Int,
    packageName: String?,
    type: String?,
    skusBundle: Bundle?,
  ): Bundle {
    return Bundle().apply { putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR) }
  }

  override fun getBuyIntent(
    apiVersion: Int,
    packageName: String?,
    sku: String?,
    type: String?,
    developerPayload: String?,
  ): Bundle {
    return Bundle().apply { putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR) }
  }

  override fun getPurchases(
    apiVersion: Int,
    packageName: String?,
    type: String?,
    continuationToken: String?,
  ): Bundle {
    return Bundle().apply { putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR) }
  }

  override fun consumePurchase(apiVersion: Int, packageName: String?, purchaseToken: String?): Int {
    return RESULT_DEVELOPER_ERROR
  }
}
