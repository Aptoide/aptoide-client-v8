package com.appcoins.billing.sdk

import android.content.pm.PackageManager
import android.os.Binder
import android.os.Bundle
import android.os.Parcel
import com.appcoins.billing.AppcoinsBilling
import com.appcoins.billing.sdk.sku_details.ProductSerializer
import com.appcoins.payments.arch.WalletProvider
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.product_inventory.ProductInventoryRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppcoinsBillingBinder @Inject internal constructor(
  private val packageManager: PackageManager,
  private val productInventoryRepository: ProductInventoryRepository,
  private val billingErrorMapper: BillingErrorMapper,
  private val productSerializer: ProductSerializer,
  private val walletProvider: WalletProvider,
) : AppcoinsBilling.Stub() {

  companion object {
    internal const val ITEM_ID_LIST = "ITEM_ID_LIST"
  }

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
        billingErrorMapper.mapBillingSupportError(exception)
      }

    return billingSupportResult
  }

  override fun getSkuDetails(
    apiVersion: Int,
    packageName: String?,
    type: String?,
    skusBundle: Bundle?,
  ): Bundle {
    val result = Bundle()
    val billingType = type?.toBillingType()
    val skus = skusBundle?.getStringArrayList(ITEM_ID_LIST)
    val merchantName = this.merchantName.takeIf { it != null }

    if (apiVersion != supportedApiVersion || merchantName.isNullOrEmpty() || skus.isNullOrEmpty() || billingType != BillingType.INAPP) {
      result.putInt(
        BillingSdkConstants.Bundle.RESPONSE_CODE,
        BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
      )
      return result
    }

    try {
      val products = runBlocking { getConsumables(merchantName, skus) }
      val serializedProducts = productSerializer.serialize(products)
      result.run {
        putInt(
          BillingSdkConstants.Bundle.RESPONSE_CODE,
          BillingSdkConstants.ResultCode.RESULT_OK
        )
        putStringArrayList(
          BillingSdkConstants.Bundle.DETAILS_LIST,
          serializedProducts
        )
      }
    } catch (exception: Throwable) {
      result.putInt(
        BillingSdkConstants.Bundle.RESPONSE_CODE,
        billingErrorMapper.mapSkuDetailsError(exception)
      )
    }

    return result
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
    val result = Bundle()
    val billingType = type?.toBillingType()
    val merchantName = this.merchantName.takeIf { it != null }

    if (apiVersion != supportedApiVersion || merchantName.isNullOrEmpty() || billingType != BillingType.INAPP) {
      result.putInt(
        BillingSdkConstants.Bundle.RESPONSE_CODE,
        BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
      )
      return result
    }

    val idsList = ArrayList<String>()
    val dataList = ArrayList<String>()
    val signatureList = ArrayList<String>()
    val skuList = ArrayList<String>()

    try {
      val purchases = runBlocking {
        val walletEwt = walletProvider.getWallet().ewt
        productInventoryRepository.getPurchases(merchantName, walletEwt)
      }

      purchases.forEach { purchase ->
        idsList.add(purchase.uid)
        dataList.add(purchase.signatureValue)
        signatureList.add(purchase.signatureMessage)
        skuList.add(purchase.productName)
      }

      result.apply {
        putInt(
          BillingSdkConstants.Bundle.RESPONSE_CODE,
          BillingSdkConstants.ResultCode.RESULT_OK
        )
        putStringArrayList(BillingSdkConstants.Bundle.INAPP_PURCHASE_ID_LIST, idsList)
        putStringArrayList(BillingSdkConstants.Bundle.INAPP_PURCHASE_DATA_LIST, dataList)
        putStringArrayList(BillingSdkConstants.Bundle.INAPP_PURCHASE_ITEM_LIST, skuList)
        putStringArrayList(BillingSdkConstants.Bundle.INAPP_DATA_SIGNATURE_LIST, signatureList)
      }
    } catch (e: Throwable) {
      result.putInt(
        BillingSdkConstants.Bundle.RESPONSE_CODE,
        billingErrorMapper.mapPurchasesError(e)
      )
    }

    return result
  }

  override fun consumePurchase(apiVersion: Int, packageName: String?, purchaseToken: String?): Int {
    return BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
  }

  private suspend fun getConsumables(merchantName: String, skus: List<String>): MutableList<ProductInfoData> {
    val result = mutableListOf<ProductInfoData>()
    for (i in skus.indices step 100) {
      val tempSkus = skus.subList(i, minOf(i + 100, skus.size))
      val consumables = productInventoryRepository.getConsumables(merchantName, tempSkus.joinToString(","))
      result.addAll(consumables)
    }
    return result
  }
}
