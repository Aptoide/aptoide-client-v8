package com.appcoins.billing.sdk

import android.content.pm.PackageManager
import android.os.Binder
import android.os.Bundle
import android.os.Parcel
import com.appcoins.billing.AppcoinsBilling
import com.appcoins.billing.sdk.billing_support.BillingSupportErrorMapper
import com.appcoins.product_inventory.ProductInventoryRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppcoinsBillingBinder @Inject internal constructor(
  private val packageManager: PackageManager,
  private val productInventoryRepository: ProductInventoryRepository,
  private val billingSupportErrorMapper: BillingSupportErrorMapper,
) : AppcoinsBilling.Stub() {

  private val supportedApiVersion = BuildConfig.SUPPORTED_API_VERSION

  private var merchantName: String? = null

  override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
    merchantName = packageManager.getPackagesForUid(Binder.getCallingUid())?.firstOrNull()
    return super.onTransact(code, data, reply, flags)
  }

  override fun isBillingSupported(apiVersion: Int, packageName: String?, type: String?): Int {
    val billingType = type?.toBillingType()
    val merchantName = this.merchantName

    if (apiVersion != supportedApiVersion || merchantName.isNullOrEmpty() || billingType != BillingType.INAPP) {
      return BillingSdkConstants.ResultCode.RESULT_BILLING_UNAVAILABLE
    }

    val billingSupportResult =
      try {
        val result = runBlocking { productInventoryRepository.isInAppBillingSupported(merchantName) }
        BillingSdkConstants.ResultCode.RESULT_OK.takeIf { result }
          ?: BillingSdkConstants.ResultCode.RESULT_BILLING_UNAVAILABLE
      } catch (exception: Throwable) {
        billingSupportErrorMapper.mapBillingSupportError(exception)
      }

    return billingSupportResult
  }

  override fun getSkuDetails(
    apiVersion: Int,
    packageName: String?,
    type: String?,
    skusBundle: Bundle?,
  ): Bundle {
    return Bundle().apply {
      putInt(
        BillingSdkConstants.Bundle.RESPONSE_CODE,
        BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
      )
    }
  }

  override fun getBuyIntent(
    apiVersion: Int,
    packageName: String?,
    sku: String?,
    type: String?,
    developerPayload: String?,
  ): Bundle {
    return Bundle().apply {
      putInt(
        BillingSdkConstants.Bundle.RESPONSE_CODE,
        BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
      )
    }
  }

  override fun getPurchases(
    apiVersion: Int,
    packageName: String?,
    type: String?,
    continuationToken: String?,
  ): Bundle {
    return Bundle().apply {
      putInt(
        BillingSdkConstants.Bundle.RESPONSE_CODE,
        BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
      )
    }
  }

  override fun consumePurchase(apiVersion: Int, packageName: String?, purchaseToken: String?): Int {
    return BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
  }
}
