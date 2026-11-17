package com.aptoide.android.aptoidegames.gamegenie.purchase

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HACKATHON: starts a purchase inside a game through the `aptoide-iap://purchase` deep link and
 * hands back the game's callback. Shared by Genie and the AppView Shop tab.
 *
 * Every launch gets a fresh ref (the SDK refuses replayed refs). Only callbacks for refs this
 * launcher started are delivered; unknown or stale refs are dropped.
 */
@Singleton
class GamePurchaseLauncher @Inject constructor(
  @ApplicationContext private val context: Context,
  private val walletSessionGrants: GenieWalletSessionGrants,
) {

  sealed interface Launch {
    data class Started(val ref: String) : Launch

    /** The game has no activity for the purchase link (an older build, for example). */
    data object NoHandler : Launch
  }

  private val pendingRefs = ConcurrentHashMap.newKeySet<String>()
  private val _results = MutableSharedFlow<GamePurchaseCallback>(extraBufferCapacity = 8)

  /** Callbacks for purchases this launcher started, each delivered once. */
  val results: SharedFlow<GamePurchaseCallback> = _results.asSharedFlow()

  /**
   * [offerWalletSession]: true only when the purchase runs on the store's real wallet session;
   * the game's SDK may then redeem it once (see [GenieWalletSessionProvider]).
   */
  fun launch(
    packageName: String,
    sku: String,
    type: String,
    target: PurchaseCallbackTarget,
    offerWalletSession: Boolean,
  ): Launch {
    val ref = UUID.randomUUID().toString()
    if (offerWalletSession) walletSessionGrants.offer(ref = ref, targetPackage = packageName)
    val uri = GamePurchaseContract.buildPurchaseUri(
      sku = sku,
      type = type,
      ref = ref,
      target = target,
      offersSession = offerWalletSession,
    )
    val intent = Intent(Intent.ACTION_VIEW, uri)
      .setPackage(packageName)
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    pendingRefs += ref
    Timber.d("Purchase link to $packageName: $uri")
    return try {
      context.startActivity(intent)
      Launch.Started(ref)
    } catch (e: ActivityNotFoundException) {
      Timber.w(e, "No purchase handler in $packageName")
      pendingRefs -= ref
      Launch.NoHandler
    }
  }

  /** Called with every parsed callback deep link. Returns true when it answered a pending ref. */
  fun deliver(callback: GamePurchaseCallback): Boolean {
    if (!pendingRefs.remove(callback.ref)) return false
    _results.tryEmit(callback)
    return true
  }
}
