package com.appcoins.billing.sdk

import com.appcoins.payments.arch.error.ServerErrorException
import java.net.UnknownHostException

internal interface BillingErrorMapper {
  fun mapBillingSupportError(exception: Throwable): Int
  fun mapSkuDetailsError(exception: Throwable): Int
  fun mapPurchasesError(exception: Throwable): Int
  fun mapConsumePurchasesError(exception: Throwable): Int
}

internal class BillingErrorMapperImpl : BillingErrorMapper {

  override fun mapBillingSupportError(exception: Throwable): Int {
    return when (exception) {
      is ServerErrorException -> BillingSdkConstants.ResultCode.RESULT_ERROR
      is UnknownHostException -> BillingSdkConstants.ResultCode.RESULT_SERVICE_UNAVAILABLE
      else -> BillingSdkConstants.ResultCode.RESULT_BILLING_UNAVAILABLE
    }
  }

  override fun mapSkuDetailsError(exception: Throwable) = mapException(exception)

  override fun mapPurchasesError(exception: Throwable) = mapException(exception)

  override fun mapConsumePurchasesError(exception: Throwable) = mapException(exception)

  private fun mapException(exception: Throwable): Int {
    return when (exception) {
      is UnknownHostException -> BillingSdkConstants.ResultCode.RESULT_SERVICE_UNAVAILABLE
      is IllegalArgumentException -> BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
      else -> BillingSdkConstants.ResultCode.RESULT_ERROR
    }
  }
}
