package com.appcoins.billing.sdk.billing_support

import com.appcoins.billing.sdk.BillingSdkConstants
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

internal interface BillingSupportErrorMapper {
  fun mapBillingSupportError(exception: Throwable): Int
}

@Singleton
internal class BillingSupportErrorMapperImpl @Inject constructor() : BillingSupportErrorMapper {

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
}
