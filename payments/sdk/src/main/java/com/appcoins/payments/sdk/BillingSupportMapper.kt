package com.appcoins.payments.sdk

import com.appcoins.payments.products.model.BillingSupport

class BillingSupportMapper {

  fun mapBillingSupport(type: BillingSupport) =
    when (type) {
      BillingSupport.NO_INTERNET_CONNECTION -> BillingSdkConstants.ResultCode.RESULT_SERVICE_UNAVAILABLE
      BillingSupport.SERVER_ERROR -> BillingSdkConstants.ResultCode.RESULT_ERROR
      BillingSupport.SUPPORTED -> BillingSdkConstants.ResultCode.RESULT_OK
      BillingSupport.NOT_SUPPORTED,
      BillingSupport.UNKNOWN_ERROR,
      -> BillingSdkConstants.ResultCode.RESULT_BILLING_UNAVAILABLE
    }
}
