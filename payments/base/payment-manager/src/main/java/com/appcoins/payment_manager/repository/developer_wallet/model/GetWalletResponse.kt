package com.appcoins.payment_manager.repository.developer_wallet.model

import androidx.annotation.Keep

@Keep
data class GetWalletResponse(val data: Data)

@Keep
data class Data(val address: String)
