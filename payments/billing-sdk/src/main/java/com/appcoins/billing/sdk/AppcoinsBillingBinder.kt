package com.appcoins.billing.sdk

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import com.appcoins.billing.AppcoinsBilling
import com.appcoins.billing.sdk.sku_details.ProductSerializer
import com.appcoins.payments.arch.PURCHASE_URI_PATH
import com.appcoins.payments.arch.PURCHASE_URI_SDK_SCHEME
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseUriParameters
import com.appcoins.payments.arch.WalletProvider
import com.appcoins.product_inventory.ProductInventoryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppcoinsBillingBinder @Inject internal constructor(
  @ApplicationContext private val context: Context,
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
        val result =
          runBlocking { productInventoryRepository.isInAppBillingSupported(merchantName) }
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

  @SuppressLint("ObsoleteSdkInt")
  override fun getBuyIntent(
    apiVersion: Int,
    packageName: String?,
    sku: String?,
    type: String?,
    developerPayload: String?,
  ): Bundle {
    val result = Bundle()
    val billingType = type?.toBillingType()
    val merchantName = this.merchantName

    if (apiVersion != supportedApiVersion || merchantName.isNullOrEmpty()
      || sku.isNullOrEmpty() || billingType != BillingType.INAPP
    ) {
      return Bundle().apply {
        putInt(
          BillingSdkConstants.Bundle.RESPONSE_CODE,
          BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
        )
      }
    }

    val uri = buildPurchaseUri(
      packageName = merchantName,
      type = billingType.type.uppercase(),
      sku = sku,
      origin = PayloadHelper.getOrigin(developerPayload),
      orderReference = PayloadHelper.getOrderReference(developerPayload),
      payload = PayloadHelper.getPayload(developerPayload)
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
      data = uri
      setPackage(context.packageName)
    }

    val pendingIntent = PendingIntent.getActivity(
      context,
      0,
      intent,
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      else
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    return result.apply {
      putInt(BillingSdkConstants.Bundle.RESPONSE_CODE, BillingSdkConstants.ResultCode.RESULT_OK)
      putParcelable(BillingSdkConstants.Bundle.BUY_INTENT, pendingIntent)
      putParcelable(BillingSdkConstants.Bundle.BUY_INTENT_RAW, intent)
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

  private suspend fun getConsumables(
    merchantName: String,
    skus: List<String>,
  ): MutableList<ProductInfoData> {
    val result = mutableListOf<ProductInfoData>()
    for (i in skus.indices step 100) {
      val tempSkus = skus.subList(i, minOf(i + 100, skus.size))
      val consumables =
        productInventoryRepository.getConsumables(merchantName, tempSkus.joinToString(","))
      result.addAll(consumables)
    }
    return result
  }

  private fun buildPurchaseUri(
    packageName: String,
    type: String,
    sku: String,
    origin: String?,
    orderReference: String?,
    payload: String?,
  ): Uri = Uri.Builder()
    .apply {
      scheme(PURCHASE_URI_SDK_SCHEME)
      path(PURCHASE_URI_PATH)
      appendQueryParameter(PurchaseUriParameters.DOMAIN, packageName)
      appendQueryParameter(PurchaseUriParameters.TYPE, type)
      appendQueryParameter(PurchaseUriParameters.PRODUCT, sku)
      origin?.let { appendQueryParameter(PurchaseUriParameters.ORIGIN, it) }
      orderReference?.let { appendQueryParameter(PurchaseUriParameters.ORDER_REFERENCE, it) }
      payload?.let { appendQueryParameter(PurchaseUriParameters.METADATA, it) }
    }
    .build()
}
