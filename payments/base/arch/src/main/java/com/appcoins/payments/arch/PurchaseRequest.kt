package com.appcoins.payments.arch

import android.net.Uri

data class PurchaseRequest(
  val uri: Uri?,
  val type: String,
  val origin: String,
  val product: String?,
  val domain: String,
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

val emptyPurchaseRequest = PurchaseRequest(
  product = "PurchaseRequest product",
  type = "Type",
  origin = "Origin",
  domain = "PurchaseRequest domain",
  callbackUrl = "PurchaseRequest callback url",
  orderReference = "PurchaseRequest order reference",
  signature = "PurchaseRequest signature",
  value = 1.0,
  currency = "PurchaseRequest currency",
  oemId = "PurchaseRequest oemid",
  oemPackage = "PurchaseRequest oempackage",
  uri = Uri.EMPTY,
  metadata = "metadata",
  productToken = "Product Token",
  skills = false
)
