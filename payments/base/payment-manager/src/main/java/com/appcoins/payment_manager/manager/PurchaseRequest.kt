package com.appcoins.payment_manager.manager

import android.net.Uri

data class PurchaseRequest(
  val ospUri: Uri?,
  val scheme: String,
  val host: String,
  val path: String,
  val product: String?,
  val domain: String,
  val to: String?,
  val productToken: String?,
  val skills: Boolean,
  val metadata: String?,
  val callbackUrl: String?,
  val orderReference: String?,
  val signature: String?,
  val value: Double?,
  val currency: String?,
  val oemId: String?,
  val oemPackage: String,
)
