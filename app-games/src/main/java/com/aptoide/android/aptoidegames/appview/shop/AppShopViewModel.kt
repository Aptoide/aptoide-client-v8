package com.aptoide.android.aptoidegames.appview.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.install_manager.InstallManager
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.gamegenie.purchase.GamePurchaseCallback
import com.aptoide.android.aptoidegames.gamegenie.purchase.GamePurchaseLauncher
import com.aptoide.android.aptoidegames.gamegenie.purchase.PurchaseCallbackTarget
import com.aptoide.android.aptoidegames.gamegenie.purchase.PurchaseStatus
import com.aptoide.android.aptoidegames.gamegenie.purchase.purchaseNotCompletedMessage
import com.aptoide.android.aptoidegames.play_and_earn.PlayAndEarnManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** The Shop tab shows for an installed game that bills through AppCoins (Catappult). */
fun shouldShowShopTab(isInstalled: Boolean, isAppCoins: Boolean, isInCatappult: Boolean?) =
  isInstalled && (isAppCoins || isInCatappult == true)

enum class ShopRowState { IDLE, WAITING_FOR_GAME, PAID }

data class ShopRow(
  val product: ShopProduct,
  val state: ShopRowState = ShopRowState.IDLE,
  val message: String? = null,
)

sealed interface ShopUiState {
  data object Loading : ShopUiState
  data class Loaded(
    val rows: List<ShopRow>,
    val fromDemoCatalogue: Boolean,
    val signedIn: Boolean,
  ) : ShopUiState
}

/**
 * HACKATHON: the AppView Shop tab. Buy fires the purchase link through [GamePurchaseLauncher]
 * (source=appview, callback ag://shop/purchase-result) and shows the outcome in the row.
 */
@HiltViewModel
class AppShopViewModel @Inject constructor(
  private val installManager: InstallManager,
  private val shopRepository: AppShopRepository,
  private val purchaseLauncher: GamePurchaseLauncher,
  private val playAndEarnManager: PlayAndEarnManager,
) : ViewModel() {

  private var packageName: String? = null
  private var appName: String = ""

  private val rows = MutableStateFlow<List<ShopRow>?>(null)
  private val fromDemoCatalogue = MutableStateFlow(false)
  private val demoUser = MutableStateFlow(false)
  private val _uiState = MutableStateFlow<ShopUiState>(ShopUiState.Loading)
  val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

  /** sku waiting for the wallet sign-in to finish before its purchase link is fired. */
  private var skuAfterSignIn: String? = null

  /** ref -> sku of purchases sent to the game and not answered yet. */
  private val pendingRefs = mutableMapOf<String, String>()

  init {
    viewModelScope.launch {
      purchaseLauncher.results.collect { if (it.target == PurchaseCallbackTarget.SHOP) onResult(it) }
    }
    viewModelScope.launch {
      playAndEarnManager.observeIsSignedIn().collect { signedIn ->
        publish()
        val sku = skuAfterSignIn
        if (signedIn && sku != null) {
          skuAfterSignIn = null
          launchPurchase(sku, offerWalletSession = true)
        }
      }
    }
  }

  fun isInstalled(packageName: String): Flow<Boolean> =
    runCatching { installManager.getApp(packageName).packageInfoFlow }
      .getOrElse { flowOf(null) }
      .map { it != null }
      .distinctUntilChanged()

  fun load(packageName: String, appName: String) {
    if (this.packageName == packageName) return
    this.packageName = packageName
    this.appName = appName
    viewModelScope.launch {
      val products = shopRepository.getProducts(packageName)
      fromDemoCatalogue.value = products.fromDemoCatalogue
      rows.value = products.products.map { ShopRow(it) }
      publish()
    }
  }

  /**
   * Buy: a signed-in wallet (or the debug demo user) fires the purchase link now; otherwise
   * [requestSignIn] is called and the purchase fires once the wallet is signed in.
   */
  fun buy(sku: String, requestSignIn: () -> Unit) {
    viewModelScope.launch {
      when {
        playAndEarnManager.isSignedIn() -> launchPurchase(sku, offerWalletSession = true)
        demoUser.value -> launchPurchase(sku, offerWalletSession = false)
        else -> {
          skuAfterSignIn = sku
          requestSignIn()
        }
      }
    }
  }

  /** DEBUG builds only: buy without a wallet session (the game's checkout asks to sign in). */
  fun continueAsDemoUser() {
    if (!BuildConfig.DEBUG) return
    demoUser.value = true
    publishNow()
  }

  private suspend fun publish() {
    val current = rows.value ?: return
    _uiState.value = ShopUiState.Loaded(
      rows = current,
      fromDemoCatalogue = fromDemoCatalogue.value,
      signedIn = demoUser.value || playAndEarnManager.isSignedIn(),
    )
  }

  private fun publishNow() {
    viewModelScope.launch { publish() }
  }

  private fun updateRow(sku: String, transform: (ShopRow) -> ShopRow) {
    rows.update { list -> list?.map { if (it.product.sku == sku) transform(it) else it } }
    publishNow()
  }

  private fun launchPurchase(sku: String, offerWalletSession: Boolean) {
    val packageName = packageName ?: return
    val product = rows.value?.firstOrNull { it.product.sku == sku }?.product ?: return
    val launch = purchaseLauncher.launch(
      packageName = packageName,
      sku = product.sku,
      type = product.type,
      target = PurchaseCallbackTarget.SHOP,
      offerWalletSession = offerWalletSession,
    )
    when (launch) {
      is GamePurchaseLauncher.Launch.Started -> {
        pendingRefs[launch.ref] = sku
        updateRow(sku) { it.copy(state = ShopRowState.WAITING_FOR_GAME, message = null) }
      }

      GamePurchaseLauncher.Launch.NoHandler -> updateRow(sku) {
        it.copy(
          state = ShopRowState.IDLE,
          message = "This version of $appName doesn't accept purchases from the store yet.",
        )
      }
    }
  }

  private fun onResult(callback: GamePurchaseCallback) {
    val sku = pendingRefs.remove(callback.ref) ?: return
    updateRow(sku) { row ->
      if (callback.status == PurchaseStatus.SUCCESS) {
        row.copy(
          state = ShopRowState.PAID,
          message = "Done — ${row.product.title} added to $appName.",
        )
      } else {
        row.copy(
          state = ShopRowState.IDLE,
          message = purchaseNotCompletedMessage(
            status = callback.status,
            gameName = appName,
            itemTitle = row.product.title,
            retryAction = "Buy",
          ),
        )
      }
    }
  }
}
