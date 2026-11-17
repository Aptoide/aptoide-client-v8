package com.aptoide.android.aptoidegames.gamegenie.purchase

/**
 * HACKATHON: a rich card Genie attaches to one of its chat turns while it drives a purchase in
 * another app. Produced locally by [GeniePurchaseOrchestrator]; never persisted nor sent to the
 * Genie backend.
 */
sealed interface GenieAction {

  /** A plain local Genie message (no card); still marks the turn as client-only. */
  data object Note : GenieAction

  /** The store wallet is not signed in. [completed] once the flow moved past the login gate. */
  data class LoginRequired(
    val appName: String,
    val completed: Boolean = false,
  ) : GenieAction

  /** Readiness checklist. A null check is still running, so the card can tick progressively. */
  data class Readiness(
    val appName: String,
    val installed: Boolean?,
    val aptoideBuild: Boolean?,
    val upToDate: Boolean?,
  ) : GenieAction

  data class PurchaseReady(
    val packageName: String,
    val appName: String,
    val sku: String,
    val type: String,
    val title: String,
    val price: String,
    val ref: String,
    val walletLabel: String,
    val balanceLabel: String?,
    val state: PayState = PayState.IDLE,
  ) : GenieAction

  data class PurchaseResult(
    val ref: String,
    val status: PurchaseStatus,
    val message: String,
  ) : GenieAction
}

enum class PayState { IDLE, WAITING_FOR_GAME, PAID }

/** Status values of the purchase-result callback contract. */
enum class PurchaseStatus(val wireValue: String) {
  SUCCESS("success"),
  CANCELED("canceled"),
  FAILED("failed"),
  LOGIN_REQUIRED("login_required"),
  UNKNOWN_SKU("unknown_sku");

  companion object {
    fun fromWire(value: String?): PurchaseStatus? =
      entries.firstOrNull { it.wireValue.equals(value, ignoreCase = true) }
  }
}
