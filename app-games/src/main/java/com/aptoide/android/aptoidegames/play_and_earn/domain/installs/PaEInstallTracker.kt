package com.aptoide.android.aptoidegames.play_and_earn.domain.installs

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.toPaERefusal
import cm.aptoide.pt.campaigns.domain.GetAvailablePaEPackagesUseCase
import cm.aptoide.pt.play_and_earn.sessions.data.PaEInstallsRepository
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingInstall
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingInstall.Click
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingInstalls
import com.aptoide.android.aptoidegames.play_and_earn.domain.mmp.PaEMmpClickSender
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/** The day's Play & Earn install limit was reached; installs count again at [retryAt]. */
data class PaEInstallLimit(val retryAt: Instant?)

/**
 * Tells the Play & Earn backend when an install of a Play & Earn game starts (the click) and
 * when it finished (the report) (AND-954), so it can count the day's installs and tell a first
 * install from another account installing the same game on this phone.
 *
 * One click id per install, kept in [PaEPendingInstalls] from the moment the click is tried:
 * the backend click carries it, and so does the developer's MMP click when the install starts
 * from a Play & Earn install view. Neither waits for the other, and neither holds the install.
 *
 * A start is told for a signed-in user, a game that is not on the phone (not an update, a
 * migration or a retry of an installed game), and a Play & Earn game. The report goes out when
 * the game is on the phone: as its install finishes, or at the next app start. Its click goes
 * again first when it never reached the backend, as the backend stores no report without one.
 */
@Singleton
class PaEInstallTracker internal constructor(
  private val repository: PaEInstallsRepository,
  private val pendingInstalls: PaEPendingInstalls,
  private val walletAddress: suspend () -> String?,
  private val isPaEGame: suspend (packageName: String) -> Boolean,
  private val installedGames: PaEInstalledGames,
  private val mmpClick: suspend (packageName: String, clickId: String) -> Unit,
  private val scope: CoroutineScope,
) {

  @Inject
  constructor(
    repository: PaEInstallsRepository,
    pendingInstalls: PaEPendingInstalls,
    walletCoreDataSource: WalletCoreDataSource,
    getAvailablePaEPackages: GetAvailablePaEPackagesUseCase,
    installedGames: AndroidPaEInstalledGames,
    mmpClickSender: PaEMmpClickSender,
  ) : this(
    repository = repository,
    pendingInstalls = pendingInstalls,
    walletAddress = walletCoreDataSource::getCurrentWalletAddress,
    // The games of the last campaigns read; none read yet, none known.
    isPaEGame = { getAvailablePaEPackages().getOrNull()?.contains(it) == true },
    installedGames = installedGames,
    mmpClick = mmpClickSender::sendNow,
    // Outlives the screen: the user may leave right after tapping Install.
    scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
  )

  private val _installLimitReached = MutableSharedFlow<PaEInstallLimit>(
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST,
  )

  /** Each click the day's limit refused, as the user starts that install. */
  val installLimitReached: SharedFlow<PaEInstallLimit> = _installLimitReached.asSharedFlow()

  /** Guards [clicksInFlight] and the creation of the pending installs. */
  private val startMutex = Mutex()
  private val clicksInFlight = mutableSetOf<String>()

  /** One reporting pass at a time: a report is never sent twice at once. */
  private val reportMutex = Mutex()

  /** An install started from a Play & Earn install view: the MMP click goes too. */
  fun onPaEViewInstallStarted(packageName: String) {
    scope.launch { contained("install start") { start(packageName, fromPaEView = true) } }
  }

  /** An install started anywhere (the install manager queued it, or Google Play took it). */
  fun onInstallStarted(packageName: String) {
    scope.launch { contained("install start") { start(packageName, fromPaEView = false) } }
  }

  /**
   * Reports what is due, then each game as its install finishes. Never returns.
   * @param packagesInstalled the packages being installed on the phone, as they are.
   */
  suspend fun initialize(packagesInstalled: Flow<String>) {
    contained("pending reports") { reportPending() }
    packagesInstalled.collect { packageName ->
      contained("report") { reportPending(packageName) }
    }
  }

  internal suspend fun start(packageName: String, fromPaEView: Boolean) {
    if (!signedIn()) return
    // An update, a migration or a retry of an installed game: not a new install.
    if (installedGames.isInstalled(packageName)) return
    // The install views only show Play & Earn games.
    if (!fromPaEView && !isPaEGame(packageName)) return

    val (install, sendClick) = startMutex.withLock {
      val install = pendingInstalls.startOrReuse(packageName)
      install to (install.click == Click.NOT_SENT && clicksInFlight.add(install.clickId))
    }
    coroutineScope {
      if (fromPaEView) {
        launch { contained("MMP click") { mmpClick(packageName, install.clickId) } }
      }
      if (sendClick) {
        launch {
          contained("click") {
            // A game installed before the answer came (a very fast install) is reported now.
            val click = sendClick(install, tellLimit = true)
            if (click == Click.RECORDED) reportPending(packageName)
          }
        }
      }
    }
  }

  /**
   * Reports the finished installs: every one, or [packageName]'s only. One that is not on the
   * phone is left for later; one that fails is tried again at the next app start.
   */
  internal suspend fun reportPending(packageName: String? = null) = reportMutex.withLock {
    if (!signedIn()) return@withLock
    pendingInstalls.all()
      .filter { packageName == null || it.packageName == packageName }
      .forEach { install -> report(install) }
  }

  private suspend fun report(install: PaEPendingInstall) {
    val evidence = installedGames.evidence(install.packageName) ?: return
    when (install.click) {
      // Not on record: a report would only be flagged. Its install is over.
      Click.REFUSED -> return pendingInstalls.remove(install.clickId)
      Click.NOT_SENT -> {
        // Already on its way from the start: its answer reports it.
        if (!startMutex.withLock { clicksInFlight.add(install.clickId) }) return
        when (sendClick(install, tellLimit = false)) {
          Click.RECORDED -> Unit
          // Not on record: never reported, and its install is over.
          Click.REFUSED -> return pendingInstalls.remove(install.clickId)
          // Still unreachable: tried again at the next start.
          Click.NOT_SENT -> return
        }
      }

      Click.RECORDED -> Unit
    }
    val result = repository.report(
      packageName = install.packageName,
      clickId = install.clickId,
      installer = evidence.installer,
      signature = evidence.signature,
    )
    val error = result.exceptionOrNull()
    // A refused or rejected report would be refused again.
    if (error == null || error.isFinal()) pendingInstalls.remove(install.clickId)
  }

  /** @return what became of the click, as it is now saved. */
  private suspend fun sendClick(install: PaEPendingInstall, tellLimit: Boolean): Click = try {
    val result = repository.click(install.packageName, install.clickId)
    val error = result.exceptionOrNull()
    when {
      error == null -> {
        // Not eligible needs nothing here: the game's missions already say so.
        pendingInstalls.setClick(install.clickId, Click.RECORDED)
        Click.RECORDED
      }

      error.isFinal() -> {
        pendingInstalls.setClick(install.clickId, Click.REFUSED)
        val refusal = error.toPaERefusal()
        if (tellLimit && refusal?.code == PaERefusalCode.INSTALL_LIMIT) {
          _installLimitReached.tryEmit(PaEInstallLimit(refusal.retryAt))
        }
        Click.REFUSED
      }

      // Offline or 5xx: the click goes again before the report.
      else -> Click.NOT_SENT
    }
  } finally {
    startMutex.withLock { clicksInFlight.remove(install.clickId) }
  }

  private suspend fun signedIn(): Boolean = !walletAddress().isNullOrBlank()

  private suspend fun contained(what: String, block: suspend () -> Unit) {
    // The install goes on whatever happens here.
    try {
      block()
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Timber.w(e, "Play & Earn $what failed")
    }
  }
}

/** A refusal, or a request the backend rejected as malformed: sending it again changes nothing. */
private fun Throwable.isFinal(): Boolean =
  toPaERefusal() != null || (this as? HttpException)?.code() == HTTP_UNPROCESSABLE

private const val HTTP_UNPROCESSABLE = 422
