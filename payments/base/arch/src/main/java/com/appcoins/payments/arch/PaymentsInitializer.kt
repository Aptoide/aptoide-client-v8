package com.appcoins.payments.arch

import android.app.Application
import android.content.Context

object PaymentsInitializer {

  lateinit var application: Application
  lateinit var environment: Environment
  lateinit var getUserAgent: GetUserAgent
  var getAllowedIds: GetAllowedIds? = null
  lateinit var walletProvider: WalletProvider

  val context: Context
    get() = application.applicationContext

  fun initializeWith(
    application: Application,
    environment: Environment,
    getUserAgent: GetUserAgent,
    getAllowedIds: GetAllowedIds? = null,
    getWalletProvider: () -> WalletProvider,
  ) {
    this.application = application
    this.environment = environment
    this.getUserAgent = getUserAgent
    this.getAllowedIds = getAllowedIds
    this.walletProvider = getWalletProvider()
  }
}
