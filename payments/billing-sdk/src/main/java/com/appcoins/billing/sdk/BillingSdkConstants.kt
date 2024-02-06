package com.appcoins.billing.sdk

internal object BillingSdkConstants {

  object ResultCode {
    const val RESULT_OK = 0 // success
    internal const val RESULT_USER_CANCELED = 1 // user pressed back or canceled a dialog
    internal const val RESULT_SERVICE_UNAVAILABLE = 2 // The network connection is down
    internal const val RESULT_BILLING_UNAVAILABLE =
      3 // this billing API version is not supported for the type requested
    internal const val RESULT_ITEM_UNAVAILABLE = 4 // requested SKU is not available for purchase
    internal const val RESULT_DEVELOPER_ERROR = 5 // invalid arguments provided to the API
    internal const val RESULT_ERROR = 6 // Fatal error during the API action
    internal const val RESULT_ITEM_ALREADY_OWNED =
      7 // Failure to purchase since item is already owned
  }

  object Bundle {
    const val RESPONSE_CODE = "RESPONSE_CODE"

    //Sku details
    const val DETAILS_LIST = "DETAILS_LIST"

    //Purchases
    const val INAPP_PURCHASE_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST"
    const val INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST"
    const val INAPP_DATA_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST"
    const val INAPP_PURCHASE_ID_LIST = "INAPP_PURCHASE_ID_LIST"

    //Buy intent
    const val BUY_INTENT = "BUY_INTENT"
    const val BUY_INTENT_RAW = "BUY_INTENT_RAW"
  }
}
