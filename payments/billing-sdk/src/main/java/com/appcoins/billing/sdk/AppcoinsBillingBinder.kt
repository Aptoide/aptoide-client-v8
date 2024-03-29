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
import android.os.IBinder
import android.os.Parcel
import com.appcoins.billing.AppcoinsBilling
import com.appcoins.billing.sdk.sku_details.ProductSerializer
import com.appcoins.billing.sdk.sku_details.ProductSerializerImpl
import com.appcoins.payments.arch.Logger
import com.appcoins.payments.arch.PURCHASE_URI_PATH
import com.appcoins.payments.arch.PURCHASE_URI_SDK_SCHEME
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.PurchaseUriParameters
import com.appcoins.payments.arch.WalletProvider
import com.appcoins.product_inventory.ProductInventoryRepository
import com.appcoins.product_inventory.model.BillingSupport
import kotlinx.coroutines.runBlocking

internal class AppcoinsBillingBinder(
  private val context: Context,
  private val packageManager: PackageManager,
  private val productInventoryRepository: ProductInventoryRepository,
  private val billingErrorMapper: BillingErrorMapper,
  private val billingSupportMapper: BillingSupportMapper,
  private val productSerializer: ProductSerializer,
  private val walletProvider: WalletProvider,
  private val logger: Logger,
) : AppcoinsBilling.Stub() {

  companion object {
    internal const val ITEM_ID_LIST = "ITEM_ID_LIST"

    fun with(
      context: Context,
      packageManager: PackageManager,
      productInventoryRepository: ProductInventoryRepository,
      walletProvider: WalletProvider,
      logger: Logger,
    ): IBinder = AppcoinsBillingBinder(
      context = context,
      packageManager = packageManager,
      productInventoryRepository = productInventoryRepository,
      billingErrorMapper = BillingErrorMapperImpl(),
      productSerializer = ProductSerializerImpl(),
      walletProvider = walletProvider,
      billingSupportMapper = BillingSupportMapper(),
      logger = logger,
    )
  }

  private val supportedApiVersion = BuildConfig.SUPPORTED_API_VERSION

  private var merchantName: String? = null

  override fun onTransact(
    code: Int,
    data: Parcel,
    reply: Parcel?,
    flags: Int,
  ): Boolean {
    merchantName = packageManager.getPackagesForUid(Binder.getCallingUid())?.firstOrNull()
    return super.onTransact(code, data, reply, flags)
  }

  override fun isBillingSupported(
    apiVersion: Int,
    packageName: String?,
    type: String?,
  ): Int {
    val billingType = type?.toBillingType()
    val merchantName = this.merchantName

    val result =
      if (apiVersion != supportedApiVersion || merchantName.isNullOrBlank() || billingType != BillingType.INAPP) {
        BillingSupport.NOT_SUPPORTED
      } else {
        runBlocking { productInventoryRepository.isInAppBillingSupported(merchantName) }
      }

    logger.logBillingEvent(
      message = "is_billing_supported",
      data = emptyMap<String, Any?>()
        .putVersion(
          apiVersion = apiVersion,
          supportedApiVersion = supportedApiVersion
        )
        .putBillingSupport(result)
        .putMerchant(merchantName)
        .putPackageName(packageName)
        .putBillingType(billingType)
    )

    return billingSupportMapper.mapBillingSupport(result)
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

    val eventDataMap = emptyMap<String, Any?>()
      .putVersion(
        apiVersion = apiVersion,
        supportedApiVersion = supportedApiVersion,
      )
      .putPackageName(packageName)
      .putMerchant(merchantName)
      .putBillingType(billingType)
      .putSkus(skus)

    if (apiVersion != supportedApiVersion || merchantName.isNullOrEmpty() || skus.isNullOrEmpty() || billingType != BillingType.INAPP) {
      logger.logBillingEvent(
        message = "sku_details",
        data = eventDataMap.putResult(false)
      )
      logger.logBillingError(UnsupportedOperationException("getSkuDetails is not supported"))
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
      logger.logBillingEvent(
        message = "sku_details",
        data = eventDataMap.putResult(true)
      )
    } catch (exception: Throwable) {
      logger.logBillingEvent(
        message = "sku_details",
        data = eventDataMap.putResult(false)
      )
      logger.logBillingError(exception)
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

    val eventDataMap = emptyMap<String, Any?>()
      .putVersion(
        apiVersion = apiVersion,
        supportedApiVersion = supportedApiVersion,
      )
      .putPackageName(packageName)
      .putMerchant(merchantName)
      .putBillingType(billingType)
      .putSku(sku)
      .putDeveloperPayload(developerPayload)

    if (apiVersion != supportedApiVersion || merchantName.isNullOrEmpty()
      || sku.isNullOrEmpty() || billingType != BillingType.INAPP
    ) {
      logger.logBillingEvent(
        message = "buy_intent",
        data = eventDataMap.putResult(false)
      )
      logger.logBillingError(UnsupportedOperationException("getBuyIntent is not supported"))
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

    logger.logBillingEvent(
      message = "buy_intent",
      data = eventDataMap.putResult(true)
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

    val eventDataMap = emptyMap<String, Any?>()
      .putVersion(
        apiVersion = apiVersion,
        supportedApiVersion = supportedApiVersion,
      )
      .putPackageName(packageName)
      .putMerchant(merchantName)
      .putBillingType(billingType)

    if (apiVersion != supportedApiVersion || merchantName.isNullOrEmpty() || billingType != BillingType.INAPP) {
      logger.logBillingEvent(
        message = "buy_intent",
        data = eventDataMap.putResult(false)
      )
      logger.logBillingError(UnsupportedOperationException("getPurchases is not supported"))
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
      val wallet = runBlocking { walletProvider.getWallet() }
      val purchases = runBlocking {
        wallet ?: return@runBlocking emptyList()
        productInventoryRepository.getPurchases(merchantName, wallet.ewt)
      }
      logger.logBillingEvent(
        message = "buy_intent",
        data = eventDataMap.putWallet(wallet?.address).putResult(false)
      )

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
      logger.logBillingEvent(
        message = "buy_intent",
        data = eventDataMap.putResult(false)
      )
      logger.logBillingError(e)
      result.putInt(
        BillingSdkConstants.Bundle.RESPONSE_CODE,
        billingErrorMapper.mapPurchasesError(e)
      )
    }

    return result
  }

  override fun consumePurchase(
    apiVersion: Int,
    packageName: String?,
    purchaseToken: String?,
  ): Int {
    val merchantName = this.merchantName

    val eventDataMap = emptyMap<String, Any?>()
      .putVersion(
        apiVersion = apiVersion,
        supportedApiVersion = supportedApiVersion,
      )
      .putPackageName(packageName)
      .putMerchant(merchantName)
      .putToken(purchaseToken)

    if (apiVersion != supportedApiVersion || merchantName.isNullOrEmpty() || purchaseToken.isNullOrEmpty()) {
      logger.logBillingEvent(
        message = "consume_purchase",
        data = eventDataMap.putResult(false)
      )
      logger.logBillingError(UnsupportedOperationException("consumePurchase is not supported"))
      return BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
    }

    return try {
      val wallet = runBlocking { walletProvider.getWallet() }
      val purchaseConsumed = runBlocking {
        wallet ?: return@runBlocking false
        productInventoryRepository.consumePurchase(
          domain = merchantName,
          uid = purchaseToken,
          authorization = wallet.ewt
        )
      }
      logger.logBillingEvent(
        message = "consume_purchase",
        data = eventDataMap.putWallet(wallet?.address).putResult(purchaseConsumed)
      )
      if (purchaseConsumed) {
        BillingSdkConstants.ResultCode.RESULT_OK
      } else {
        BillingSdkConstants.ResultCode.RESULT_ERROR
      }
    } catch (exception: Throwable) {
      logger.logBillingEvent(
        message = "consume_purchase",
        data = eventDataMap.putResult(false)
      )
      logger.logBillingError(exception)
      billingErrorMapper.mapConsumePurchasesError(exception)
    }
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

private fun Logger.logBillingError(throwable: Throwable) = logError(
  tag = "billing_sdk",
  throwable = throwable,
)

private fun Logger.logBillingEvent(
  message: String,
  data: Map<String, Any?>,
) = logEvent(
  tag = "billing_sdk",
  message = message,
  data = data
)

private fun Map<String, Any?>.putMerchant(merchantName: String?) =
  this + mapOf("merchant" to merchantName)

private fun Map<String, Any?>.putWallet(wallet: String?) =
  this + mapOf("wallet" to wallet)

private fun Map<String, Any?>.putToken(token: String?) =
  this + mapOf("token" to token)

private fun Map<String, Any?>.putBillingType(billingType: BillingType?) =
  this + mapOf("billingType" to billingType)

private fun Map<String, Any?>.putSkus(skus: List<String>?) =
  this + mapOf("skus" to skus)

private fun Map<String, Any?>.putSku(sku: String?) =
  this + mapOf("sku" to sku)

private fun Map<String, Any?>.putDeveloperPayload(developerPayload: String?) =
  this + mapOf("developerPayload" to developerPayload)

private fun Map<String, Any?>.putPackageName(packageName: String?) =
  this + mapOf("packageName" to packageName)

private fun Map<String, Any?>.putBillingSupport(billingSupport: BillingSupport) =
  this + mapOf("billingSupport" to billingSupport)

private fun Map<String, Any?>.putVersion(apiVersion: Int, supportedApiVersion: Int) =
  this + mapOf(
    "version" to mapOf(
      "api" to apiVersion,
      "supported" to supportedApiVersion
    )
  )

private fun Map<String, Any?>.putResult(success: Boolean) =
  this + mapOf("result" to if (success) "success" else "fail")
