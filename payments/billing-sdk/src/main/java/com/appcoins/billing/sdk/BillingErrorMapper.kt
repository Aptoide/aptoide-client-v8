package com.appcoins.billing.sdk

import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

internal interface BillingErrorMapper {
  fun mapBillingSupportError(exception: Throwable): Int
  fun mapSkuDetailsError(exception: Throwable): Int
  fun mapPurchasesError(exception: Throwable): Int
  fun mapConsumePurchasesError(exception: Throwable): Int
}

@Singleton
internal class BillingErrorMapperImpl @Inject constructor() : BillingErrorMapper {

  override fun mapBillingSupportError(exception: Throwable): Int {
    return when {
      exception is HttpException && exception.code() in 500..599 ->
        BillingSdkConstants.ResultCode.RESULT_ERROR

      exception is UnknownHostException ->
        BillingSdkConstants.ResultCode.RESULT_SERVICE_UNAVAILABLE

      else -> {
        exception.printStackTrace()
        BillingSdkConstants.ResultCode.RESULT_BILLING_UNAVAILABLE
      }
    }
  }

  override fun mapSkuDetailsError(exception: Throwable): Int {
    return when (exception) {
      is UnknownHostException -> BillingSdkConstants.ResultCode.RESULT_SERVICE_UNAVAILABLE
      is IllegalArgumentException -> BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
      else -> BillingSdkConstants.ResultCode.RESULT_ERROR
    }
  }

  override fun mapPurchasesError(exception: Throwable): Int {
    return when (exception) {
      is UnknownHostException -> BillingSdkConstants.ResultCode.RESULT_SERVICE_UNAVAILABLE
      is IllegalArgumentException -> BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
      else -> BillingSdkConstants.ResultCode.RESULT_ERROR
    }
  }

  override fun mapConsumePurchasesError(exception: Throwable): Int {
    return when (exception) {
      is UnknownHostException -> BillingSdkConstants.ResultCode.RESULT_SERVICE_UNAVAILABLE
      is IllegalArgumentException -> BillingSdkConstants.ResultCode.RESULT_DEVELOPER_ERROR
      else -> BillingSdkConstants.ResultCode.RESULT_ERROR
    }
  }
}
