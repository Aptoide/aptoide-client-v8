package com.appcoins.payments.arch

import android.net.Uri

data class PurchaseRequest(
  val uri: Uri?,
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

val emptyPurchaseRequest = PurchaseRequest(
  scheme = "PurchaseRequest scheme",
  host = "PurchaseRequest host",
  path = "PurchaseRequest path",
  product = "PurchaseRequest product",
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
  to = "To",
  productToken = "Product Token",
  skills = false
)
