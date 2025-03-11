package com.aptoide.android.aptoidegames.feature_payments.analytics

import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.ProductInfoData
import com.appcoins.payments.arch.Transaction
import com.aptoide.android.aptoidegames.analytics.BIAnalytics
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.analytics.dto.AnalyticsUIContext
import com.aptoide.android.aptoidegames.analytics.mapOfNonNull
import com.aptoide.android.aptoidegames.analytics.toGenericParameters
import com.aptoide.android.aptoidegames.feature_payments.analytics.PaymentAnalytics.Companion.P_CURRENCY
import com.aptoide.android.aptoidegames.feature_payments.analytics.PaymentAnalytics.Companion.P_PAYMENT_METHOD
import com.aptoide.android.aptoidegames.feature_payments.analytics.PaymentAnalytics.Companion.P_PRICE
import com.aptoide.android.aptoidegames.feature_payments.analytics.PaymentAnalytics.Companion.P_SKU_ID
import com.aptoide.android.aptoidegames.feature_payments.analytics.PaymentAnalytics.Companion.P_SKU_NAME

class PaymentAnalytics(
  private val genericAnalytics: GenericAnalytics,
  private val biAnalytics: BIAnalytics,
) {

  fun sendPaymentStartEvent(
    packageName: String,
    productInfoData: ProductInfoData?,
  ) {
    val params = productInfoData.toGenericParameters(GenericAnalytics.P_PACKAGE_NAME to packageName)
    biAnalytics.logEvent(
      name = "iap_payment_start",
      params = params
    )
    // TODO: removed from generic analytics - will have to add again as soon as we stop sending BiAnalytics it to multiple platforms
  }

  fun sendPaymentMethodsDismissedEvent(
    packageName: String,
    productInfoData: ProductInfoData?,
  ) {
    val params = productInfoData.toGenericParameters(
      GenericAnalytics.P_PACKAGE_NAME to packageName,
      P_PAYMENT_METHOD to "list",
      GenericAnalytics.P_CONTEXT to "start"
    )
    logPaymentDismissed(params)
  }

  fun sendPaymentDismissedEvent(
    paymentMethod: PaymentMethod<*>,
    context: String?,
  ) {
    val params = paymentMethod.toGenericParameters(GenericAnalytics.P_CONTEXT to context)
    logPaymentDismissed(params)
  }

  fun sendPaymentDismissedEvent(
    transaction: Transaction?,
    context: String?,
  ) {
    val params = transaction
      ?.toGenericParameters(GenericAnalytics.P_CONTEXT to context)
      ?: mapOfNonNull(
        P_PAYMENT_METHOD to "unknown",
        GenericAnalytics.P_CONTEXT to context
      )
    logPaymentDismissed(params)
  }

  fun sendPaymentBackEvent(paymentMethod: PaymentMethod<*>) {
    val params = paymentMethod.toGenericParameters()
    genericAnalytics.logEvent(
      name = "iap_payment_back",
      params = params
    )
    biAnalytics.logEvent(
      name = "iap_payment_other_payment",
      params = params
    )
  }

  fun sendPaymentBuyEvent(paymentMethod: PaymentMethod<*>) {
    val params = paymentMethod.toGenericParameters()
    biAnalytics.logEvent(
      name = "iap_payment_buy",
      params = params
    )
    // TODO: removed from generic analytics - will have to add again as soon as we stop sending BiAnalytics it to multiple platforms
  }

  fun sendPaymentTryAgainEvent(paymentMethod: PaymentMethod<*>) {
    val params = paymentMethod.toGenericParameters()
    biAnalytics.logEvent(
      name = "iap_payment_try_again",
      params = params
    )
    // TODO: removed from generic analytics - will have to add again as soon as we stop sending BiAnalytics it to multiple platforms
  }

  fun sendPaymentSuccessEvent(paymentMethod: PaymentMethod<*>) {
    val params = paymentMethod.toGenericParameters(P_STATUS to "success")
    logPaymentConclusion(params)
  }

  fun sendPaymentErrorEvent(
    paymentMethod: PaymentMethod<*>,
    errorCode: String? = null,
  ) {
    val params = paymentMethod.toGenericParameters(
      P_STATUS to "error",
      P_ERROR_CODE to errorCode
    )
    logPaymentConclusion(params)
  }

  fun sendPaymentSuccessEvent(transaction: Transaction?) {
    val params = transaction
      ?.toGenericParameters(P_STATUS to "success")
      ?: mapOf(
        P_PAYMENT_METHOD to "unknown",
        P_STATUS to "success"
      )
    logPaymentConclusion(params)
  }

  fun sendPaymentErrorEvent(
    transaction: Transaction?,
    errorCode: String? = null,
  ) {
    val params = transaction?.toGenericParameters(
      P_STATUS to "error",
      P_ERROR_CODE to errorCode
    ) ?: mapOfNonNull(
      P_PAYMENT_METHOD to "unknown",
      P_STATUS to "error",
      P_ERROR_CODE to errorCode
    )
    logPaymentConclusion(params)
  }

  fun sendPaymentMethodsEvent(paymentMethod: PaymentMethod<*>) {
    val params = paymentMethod.productInfo.toGenericParameters(
      GenericAnalytics.P_PACKAGE_NAME to paymentMethod.purchaseRequest.domain,
      P_PAYMENT_METHOD to paymentMethod.id
    )
    biAnalytics.logEvent(
      name = "iap_payment_methods",
      params = params
    )
    // TODO: removed from generic analytics - will have to add again as soon as we stop sending BiAnalytics it to multiple platforms
  }

  fun sendAppCoinsInstallStarted(
    packageName: String,
    analyticsContext: AnalyticsUIContext,
  ) = genericAnalytics.logEvent(
    name = "appcoins_install_initiated",
    params = analyticsContext.toGenericParameters(GenericAnalytics.P_PACKAGE_NAME to packageName)
  )

  private fun logPaymentDismissed(params: Map<String, Any>) {
    genericAnalytics.logEvent(
      name = "iap_payment_dismissed",
      params = params
    )
    biAnalytics.logEvent(
      name = "iap_payment_exit",
      params = params
    )
  }

  private fun logPaymentConclusion(params: Map<String, Any>) {
    biAnalytics.logEvent(
      name = "iap_payment_conclusion",
      params = params
    )
    // TODO: removed from generic analytics - will have to add again as soon as we stop sending BiAnalytics it to multiple platforms
  }

  companion object {
    private const val P_ERROR_CODE = "error_code"
    private const val P_STATUS = "status"
    internal const val P_CURRENCY = "currency"
    internal const val P_SKU_ID = "sku_id"
    internal const val P_SKU_NAME = "sku_name"
    internal const val P_PAYMENT_METHOD = "payment_method"
    internal const val P_PRICE = "price"
  }
}

fun PaymentMethod<*>.toGenericParameters(vararg pairs: Pair<String, Any?>) =
  productInfo.toGenericParameters(
    *pairs,
    GenericAnalytics.P_PACKAGE_NAME to purchaseRequest.domain,
    P_PAYMENT_METHOD to id
  )

fun ProductInfoData?.toGenericParameters(vararg pairs: Pair<String, Any?>) = this?.run {
  mapOfNonNull(
    *pairs,
    P_SKU_ID to sku,
    P_SKU_NAME to title,
    P_PRICE to priceValue,
    P_CURRENCY to priceCurrency
  )
} ?: mapOfNonNull(*pairs)

fun Transaction.toGenericParameters(vararg pairs: Pair<String, Any?>) = mapOfNonNull(
  *pairs,
  GenericAnalytics.P_PACKAGE_NAME to domain,
  P_PAYMENT_METHOD to method,
  P_SKU_ID to product,
  P_SKU_NAME to product,
  P_PRICE to price.value,
  P_CURRENCY to price.currency
)
