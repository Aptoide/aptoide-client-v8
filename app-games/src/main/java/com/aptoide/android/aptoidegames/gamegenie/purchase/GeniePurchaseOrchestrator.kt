package com.aptoide.android.aptoidegames.gamegenie.purchase

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import cm.aptoide.pt.wallet.wallet_info.data.WalletInfoRepository
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.getInstallerPackageNameCompat
import com.aptoide.android.aptoidegames.play_and_earn.PlayAndEarnManager
import com.aptoide.android.aptoidegames.play_and_earn.data.UserAccountPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.math.RoundingMode
import java.util.UUID
import javax.inject.Inject

/** The chat the orchestrator writes Genie turns into (implemented by GameGenieViewModel). */
interface GenieChatHost {
  /** Appends a Genie turn and returns its index in the conversation. */
  fun appendGenieTurn(text: String, action: GenieAction? = null): Int

  /** Replaces the action of the turn at [index]; a null result from [transform] keeps it. */
  fun updateGenieAction(index: Int, transform: (GenieAction) -> GenieAction?)
}

/**
 * HACKATHON: drives "buy X in game Y" from the Genie chat, entirely on the client:
 * CheckLogin -> CheckGame -> Ready -> AwaitResult -> Done.
 *
 * Not a singleton: one instance per GameGenieViewModel (it holds that chat's flow state).
 */
class GeniePurchaseOrchestrator @Inject constructor(
  @ApplicationContext private val context: Context,
  private val playAndEarnManager: PlayAndEarnManager,
  private val walletCoreDataSource: WalletCoreDataSource,
  private val userAccountPreferencesRepository: UserAccountPreferencesRepository,
  private val walletInfoRepository: WalletInfoRepository,
  private val resultBus: GeniePurchaseResultBus,
) {

  private enum class Step { IDLE, CHECK_LOGIN, CHECK_GAME, READY, AWAIT_RESULT, DONE }

  private data class Pending(val ref: String, val cardIndex: Int, val item: GeniePurchaseItem)

  private lateinit var scope: CoroutineScope
  private lateinit var host: GenieChatHost

  private var step = Step.IDLE
  private var flowJob: Job? = null
  private val demoSignIn = MutableStateFlow(false)
  private var readyCardIndex: Int? = null
  private var readyItem: GeniePurchaseItem? = null
  private var pending: Pending? = null

  fun attach(scope: CoroutineScope, host: GenieChatHost) {
    this.scope = scope
    this.host = host
    scope.launch { resultBus.results.collect(::onCallback) }
  }

  fun start(item: GeniePurchaseItem) {
    cancel()
    flowJob = scope.launch {
      try {
        run(item)
      } catch (e: CancellationException) {
        throw e
      } catch (e: Exception) {
        Timber.w(e, "Genie purchase flow failed")
        step = Step.DONE
        host.appendGenieTurn("Something went wrong while preparing your purchase. Try again?")
      }
    }
  }

  /** Drops the current flow, e.g. when the conversation it writes into is replaced. */
  fun cancel() {
    flowJob?.cancel()
    flowJob = null
    demoSignIn.value = false
    readyCardIndex = null
    readyItem = null
    pending = null
    step = Step.IDLE
  }

  /** DEBUG-only escape hatch on the login card: continue without a Google sign-in. */
  fun continueAsDemoUser() {
    if (BuildConfig.DEBUG && step == Step.CHECK_LOGIN) demoSignIn.value = true
  }

  private suspend fun run(item: GeniePurchaseItem) {
    // CheckLogin
    step = Step.CHECK_LOGIN
    var demoUser = false
    if (!playAndEarnManager.isSignedIn()) {
      val loginIndex = host.appendGenieTurn(
        "To buy ${item.title} in ${item.gameName} you need to sign in to your Aptoide wallet " +
          "first. I'll pick up right where we left off.",
        GenieAction.LoginRequired(appName = item.gameName)
      )
      demoUser = merge(
        playAndEarnManager.observeIsSignedIn().filter { it }.map { false },
        demoSignIn.filter { it }.map { true },
      ).first()
      host.updateGenieAction(loginIndex) {
        (it as? GenieAction.LoginRequired)?.copy(completed = true)
      }
    }

    // CheckGame
    step = Step.CHECK_GAME
    var readiness = GenieAction.Readiness(item.gameName, null, null, null)
    val readinessIndex = host.appendGenieTurn(
      "Let me check ${item.gameName} on this device.",
      readiness
    )
    fun tick(update: GenieAction.Readiness) {
      readiness = update
      host.updateGenieAction(readinessIndex) { readiness }
    }

    delay(TICK_DELAY_MS)
    val packageName = withContext(Dispatchers.IO) {
      item.packageCandidates.firstOrNull(::isInstalled)
    }
    if (packageName == null) {
      tick(readiness.copy(installed = false, aptoideBuild = false, upToDate = false))
      step = Step.DONE
      host.appendGenieTurn(
        "${item.gameName} isn't installed on this device yet. Install it and ask me again."
      )
      return
    }
    tick(readiness.copy(installed = true))

    delay(TICK_DELAY_MS)
    val aptoideBuild = withContext(Dispatchers.IO) { isAptoideBuild(packageName) }
    tick(readiness.copy(aptoideBuild = aptoideBuild))

    delay(TICK_DELAY_MS)
    // HACKATHON MOCK: no catalogue lookup of the latest version; an installed game is up to date.
    tick(readiness.copy(upToDate = true))

    if (!aptoideBuild) {
      step = Step.DONE
      host.appendGenieTurn(
        "This copy of ${item.gameName} wasn't installed from Aptoide, so I can't pay for it " +
          "from here."
      )
      return
    }

    // Ready
    delay(TICK_DELAY_MS)
    val wallet = walletLabels(demoUser)
    readyItem = item
    readyCardIndex = host.appendGenieTurn(
      "All set. Tap Pay and ${item.gameName} will open to finish the purchase.",
      GenieAction.PurchaseReady(
        packageName = packageName,
        appName = item.gameName,
        sku = item.sku,
        type = item.type,
        title = item.title,
        price = item.price,
        ref = UUID.randomUUID().toString(),
        walletLabel = wallet.first,
        balanceLabel = wallet.second,
      )
    )
    step = Step.READY
  }

  /** Pay button: fires the purchase deep link into the game and waits for its callback. */
  fun pay(card: GenieAction.PurchaseReady) {
    val index = readyCardIndex ?: return
    val item = readyItem ?: return
    if (step != Step.READY && step != Step.AWAIT_RESULT) return
    val ref = UUID.randomUUID().toString()
    val uri = GeniePurchaseContract.buildPurchaseUri(sku = card.sku, type = card.type, ref = ref)
    val intent = Intent(Intent.ACTION_VIEW, uri)
      .setPackage(card.packageName)
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    pending = Pending(ref = ref, cardIndex = index, item = item)
    step = Step.AWAIT_RESULT
    host.updateGenieAction(index) {
      (it as? GenieAction.PurchaseReady)?.copy(ref = ref, state = PayState.WAITING_FOR_GAME)
    }
    Timber.d("Genie purchase link to ${card.packageName}: $uri")
    try {
      context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
      Timber.w(e, "No purchase handler in ${card.packageName}")
      pending = null
      step = Step.READY
      host.updateGenieAction(index) {
        (it as? GenieAction.PurchaseReady)?.copy(state = PayState.IDLE)
      }
      host.appendGenieTurn(
        "I couldn't open ${card.appName} to finish the purchase. This version of the game " +
          "doesn't accept purchases from Genie yet."
      )
    }
  }

  private fun onCallback(callback: GeniePurchaseCallback) {
    val waiting = pending ?: return
    if (callback.ref != waiting.ref) return // unknown or stale ref: ignore silently
    pending = null
    val item = waiting.item

    if (callback.status == PurchaseStatus.SUCCESS) {
      step = Step.DONE
      host.updateGenieAction(waiting.cardIndex) {
        (it as? GenieAction.PurchaseReady)?.copy(state = PayState.PAID)
      }
      val message = "Done — ${item.title} added to ${item.gameName}."
      host.appendGenieTurn(
        message,
        GenieAction.PurchaseResult(ref = waiting.ref, status = PurchaseStatus.SUCCESS, message)
      )
      return
    }

    // Not paid: the card goes back to Pay so the user can try again.
    step = Step.READY
    host.updateGenieAction(waiting.cardIndex) {
      (it as? GenieAction.PurchaseReady)?.copy(state = PayState.IDLE)
    }
    host.appendGenieTurn(
      when (callback.status) {
        PurchaseStatus.CANCELED ->
          "No problem, the purchase was canceled. You weren't charged."

        PurchaseStatus.LOGIN_REQUIRED ->
          "${item.gameName} needs you signed in before buying. Sign in there and tap Pay again."

        PurchaseStatus.UNKNOWN_SKU ->
          "${item.gameName} doesn't sell ${item.title} right now, so nothing was charged."

        PurchaseStatus.FAILED, PurchaseStatus.SUCCESS, null ->
          "The payment didn't go through in ${item.gameName}. You weren't charged. " +
            "You can tap Pay to try again."
      }
    )
  }

  private fun isInstalled(packageName: String): Boolean = runCatching {
    context.packageManager.getPackageInfo(packageName, 0)
  }.isSuccess

  private fun isAptoideBuild(packageName: String): Boolean {
    val installer = context.packageManager.getInstallerPackageNameCompat(packageName)
    if (installer == context.packageName) return true
    // HACKATHON MOCK: debug builds accept a sideloaded game (adb / package installer), since the
    // demo device gets Diceroll from Android Studio, not from the store.
    return BuildConfig.DEBUG && installer in SIDELOAD_INSTALLERS
  }

  /** (wallet label, balance label or null) */
  private suspend fun walletLabels(demoUser: Boolean): Pair<String, String?> {
    if (demoUser) return DEMO_WALLET_LABEL to null
    val address = walletCoreDataSource.getCurrentWalletAddress()
    val email = runCatching {
      withTimeoutOrNull(500) { userAccountPreferencesRepository.getUserInfo().firstOrNull() }
    }.getOrNull()?.email?.takeIf { it.isNotBlank() }
    val label = email ?: address?.let(::shortAddress) ?: DEMO_WALLET_LABEL
    val balance = address?.let {
      withTimeoutOrNull(BALANCE_TIMEOUT_MS) {
        runCatching { walletInfoRepository.getWalletInfo(it).walletBalance }
          .onFailure { e -> Timber.w(e, "Genie purchase: no wallet balance") }
          .getOrNull()
      }
    }?.let { fiat ->
      val amount = fiat.amount.setScale(2, RoundingMode.HALF_UP).toPlainString()
      if (fiat.symbol.isNotBlank()) "${fiat.symbol}$amount" else "$amount ${fiat.currency}"
    }
    return label to balance
  }

  companion object {
    private const val TICK_DELAY_MS = 500L
    private const val BALANCE_TIMEOUT_MS = 2_000L
    const val DEMO_WALLET_LABEL = "demo wallet"
    private val SIDELOAD_INSTALLERS = setOf(
      null,
      "com.android.shell",
      "com.android.packageinstaller",
      "com.google.android.packageinstaller",
    )

    fun shortAddress(address: String): String =
      if (address.length > 12) "${address.take(6)}…${address.takeLast(4)}" else address
  }
}
