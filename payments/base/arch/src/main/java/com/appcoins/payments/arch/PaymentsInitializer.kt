package com.appcoins.payments.arch

import android.app.Application
import android.content.Context

object PaymentsInitializer {

  lateinit var application: Application
  lateinit var environment: Environment
  lateinit var logger: Logger
  lateinit var getUserAgent: GetUserAgent
  var getAllowedIds: GetAllowedIds? = null
  lateinit var walletProvider: WalletProvider
  lateinit var channel: String

  val context: Context
    get() = application.applicationContext

  fun initializeWith(
    application: Application,
    environment: Environment,
    logger: Logger = MutedLogger,
    getUserAgent: GetUserAgent,
    getAllowedIds: GetAllowedIds? = null,
    getWalletProvider: () -> WalletProvider,
    channel: String
  ) {
    this.application = application
    this.environment = environment
    this.logger = logger
    this.getUserAgent = getUserAgent
    this.getAllowedIds = getAllowedIds
    this.walletProvider = getWalletProvider()
    this.channel = channel
  }
}
