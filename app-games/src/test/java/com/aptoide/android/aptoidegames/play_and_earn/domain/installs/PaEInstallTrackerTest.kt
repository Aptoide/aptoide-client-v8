package com.aptoide.android.aptoidegames.play_and_earn.domain.installs

import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException
import cm.aptoide.pt.play_and_earn.sessions.data.PaEInstallAnswer
import cm.aptoide.pt.play_and_earn.sessions.data.PaEInstallsRepository
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.play_and_earn.data.InMemoryPreferencesDataStore
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingInstall
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingInstall.Click
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPendingInstalls
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.time.Instant

// AND-954: every Play & Earn install start is told to the backend (the click) and every finished
// one is reported, with one click id that the MMP link carries too.
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaEInstallTrackerTest {

  private class FakeInstallsRepository : PaEInstallsRepository {
    val calls = mutableListOf<String>()
    val reports = mutableListOf<List<String?>>()
    var clickResult: Result<PaEInstallAnswer> = Result.success(PaEInstallAnswer(eligible = true))
    var reportResult: Result<Unit> = Result.success(Unit)
    var clickGate: CompletableDeferred<Unit>? = null

    override suspend fun click(packageName: String, clickId: String): Result<PaEInstallAnswer> {
      calls += "click $packageName $clickId"
      clickGate?.await()
      return clickResult
    }

    override suspend fun report(
      packageName: String,
      clickId: String,
      installer: String?,
      signature: String?,
    ): Result<Unit> {
      calls += "report $packageName $clickId"
      reports += listOf(packageName, clickId, installer, signature)
      return reportResult
    }
  }

  private class FakeInstalledGames : PaEInstalledGames {
    val installed = mutableMapOf<String, PaEInstallEvidence>()
    override fun isInstalled(packageName: String) = packageName in installed
    override fun evidence(packageName: String) = installed[packageName]
  }

  private val repository = FakeInstallsRepository()
  private val installedGames = FakeInstalledGames()
  private val dataStore = InMemoryPreferencesDataStore()
  private var nextId = 0
  private val pendingInstalls = PaEPendingInstalls(
    dataStore = dataStore,
    nowMillis = { 1_000_000L },
    newClickId = { "click-${++nextId}" },
  )
  private val mmpClicks = mutableListOf<String>()
  private var mmpFailure: Exception? = null
  private var wallet: String? = "0xWallet"
  private val paeGames = mutableSetOf("com.game")
  private val evidence = PaEInstallEvidence(installer = "com.aptoide.store", signature = "ab12")

  private fun tracker(scope: TestScope = TestScope()) = PaEInstallTracker(
    repository = repository,
    pendingInstalls = pendingInstalls,
    walletAddress = { wallet },
    isPaEGame = { it in paeGames },
    installedGames = installedGames,
    mmpClick = { packageName, clickId ->
      mmpFailure?.let { throw it }
      mmpClicks += "$packageName $clickId"
    },
    scope = scope,
  )

  @Test
  fun `The MMP link and the backend click carry the same id`() = coScenario {
    m Given "a signed-in user and a game that is not installed"
    val tracker = tracker()

    m When "the install starts from a Play & Earn install view"
    tracker.start("com.game", fromPaEView = true)

    m Then "the backend click and the MMP click carry one id, which is saved as recorded"
    assertEquals(listOf("click com.game click-1"), repository.calls)
    assertEquals(listOf("com.game click-1"), mmpClicks)
    assertEquals(listOf(Click.RECORDED), pendingInstalls.all().map { it.click })
  }

  @Test
  fun `An install from anywhere else sends the backend click only`() = coScenario {
    m Given "a signed-in user and a Play & Earn game that is not installed"
    val tracker = tracker()

    m When "its install starts from search or the plain game page"
    tracker.start("com.game", fromPaEView = false)

    m Then "the backend click is sent and no MMP click, as before for those screens"
    assertEquals(listOf("click com.game click-1"), repository.calls)
    assertEquals(emptyList<String>(), mmpClicks)
  }

  @Test
  fun `A game with no tracking link still sends the backend click`() = coScenario {
    m Given "a game whose campaign has no MMP link: the MMP sender sends nothing"
    val tracker = tracker()
    mmpFailure = null

    m When "the install starts from a Play & Earn install view"
    tracker.start("com.other", fromPaEView = true)

    m Then "the backend click is sent all the same"
    assertEquals(listOf("click com.other click-1"), repository.calls)
  }

  @Test
  fun `A failing MMP click does not stop the backend click`() = coScenario {
    m Given "an MMP click that fails"
    mmpFailure = IllegalArgumentException("bad url")
    val tracker = tracker()

    m When "the install starts from a Play & Earn install view"
    tracker.start("com.game", fromPaEView = true)

    m Then "the backend click is sent and recorded"
    assertEquals(listOf("click com.game click-1"), repository.calls)
    assertEquals(listOf(Click.RECORDED), pendingInstalls.all().map { it.click })
  }

  @Test
  fun `A failing backend click does not stop the MMP click`() = coScenario {
    m Given "a backend that cannot be reached"
    repository.clickResult = Result.failure(IOException("offline"))
    val tracker = tracker()

    m When "the install starts from a Play & Earn install view"
    tracker.start("com.game", fromPaEView = true)

    m Then "the MMP click is sent, and the install stays pending with its click not sent"
    assertEquals(listOf("com.game click-1"), mmpClicks)
    assertEquals(
      listOf(PaEPendingInstall("com.game", "click-1", 1_000_000L, Click.NOT_SENT)),
      pendingInstalls.all()
    )
  }

  @ParameterizedTest(name = "wallet \"{0}\"")
  @ValueSource(strings = ["", " "])
  fun `Nothing is sent for a signed-out user`(blank: String) = coScenario {
    m Given "no wallet: \"$blank\" and then none at all"
    val tracker = tracker()

    m When "an install starts each time"
    wallet = blank
    tracker.start("com.game", fromPaEView = true)
    wallet = null
    tracker.start("com.game", fromPaEView = true)

    m Then "no click is sent and nothing is pending"
    assertEquals(emptyList<String>(), repository.calls + mmpClicks)
    assertEquals(emptyList<PaEPendingInstall>(), pendingInstalls.all())
  }

  @Test
  fun `An update, migration or retry of an installed game sends nothing`() = coScenario {
    m Given "the game is already on the phone"
    installedGames.installed["com.game"] = evidence
    val tracker = tracker()

    m When "its install starts again, from a Play & Earn view and from the install manager"
    tracker.start("com.game", fromPaEView = true)
    tracker.start("com.game", fromPaEView = false)

    m Then "no click is sent and nothing is pending"
    assertEquals(emptyList<String>(), repository.calls + mmpClicks)
    assertEquals(emptyList<PaEPendingInstall>(), pendingInstalls.all())
  }

  @Test
  fun `A game that is not a Play & Earn game sends nothing`() = coScenario {
    m Given "a game outside Play & Earn"
    val tracker = tracker()

    m When "its install starts from search"
    tracker.start("com.not.pae", fromPaEView = false)

    m Then "no click is sent and nothing is pending"
    assertEquals(emptyList<String>(), repository.calls)
    assertEquals(emptyList<PaEPendingInstall>(), pendingInstalls.all())
  }

  @Test
  fun `The same start told twice sends one click`() = coScenario { scope ->
    m Given "a backend click still on its way"
    repository.clickGate = CompletableDeferred()
    val tracker = tracker()
    scope.launch { tracker.start("com.game", fromPaEView = true) }
    scope.runCurrent()

    m When "the install manager tells the same start, then the answer arrives"
    scope.launch { tracker.start("com.game", fromPaEView = false) }
    scope.runCurrent()
    repository.clickGate?.complete(Unit)
    scope.advanceUntilIdle()

    m Then "one backend click was sent"
    assertEquals(listOf("click com.game click-1"), repository.calls)
  }

  @Test
  fun `A finished install is reported with its evidence and forgotten`() = coScenario {
    m Given "a recorded click for a game that is now installed"
    val tracker = tracker()
    tracker.start("com.game", fromPaEView = true)
    installedGames.installed["com.game"] = evidence

    m When "the game's install finishes"
    tracker.reportPending("com.game")

    m Then "the report carries the click id, the installer and the signature, and is done"
    assertEquals(
      listOf(listOf("com.game", "click-1", "com.aptoide.store", "ab12")),
      repository.reports
    )
    assertEquals(emptyList<PaEPendingInstall>(), pendingInstalls.all())
  }

  @Test
  fun `A report without readable evidence is still sent`() = coScenario {
    m Given "a recorded click for an installed game whose installer and signature can't be read"
    val tracker = tracker()
    tracker.start("com.game", fromPaEView = true)
    installedGames.installed["com.game"] = PaEInstallEvidence(installer = null, signature = null)

    m When "the game's install finishes"
    tracker.reportPending("com.game")

    m Then "the report is sent without them"
    assertEquals(listOf(listOf<String?>("com.game", "click-1", null, null)), repository.reports)
  }

  @Test
  fun `A report that fails is kept and sent at the next start`() = coScenario {
    m Given "a recorded click for an installed game, and a backend that fails the report once"
    val tracker = tracker()
    tracker.start("com.game", fromPaEView = true)
    installedGames.installed["com.game"] = evidence
    repository.reportResult = Result.failure(IOException("offline"))
    tracker.reportPending("com.game")
    val keptAfterFailure = pendingInstalls.all().map { it.clickId }

    m When "the app starts again with the backend back"
    repository.reportResult = Result.success(Unit)
    tracker().reportPending()

    m Then "it was kept after the failure, sent once more, and is now done"
    assertEquals(listOf("click-1"), keptAfterFailure)
    assertEquals(2, repository.reports.size)
    assertEquals(emptyList<PaEPendingInstall>(), pendingInstalls.all())
  }

  @Test
  fun `A click that failed is sent again before its report`() = coScenario {
    m Given "a click that never reached the backend, for a game now installed"
    repository.clickResult = Result.failure(IOException("offline"))
    tracker().start("com.game", fromPaEView = true)
    installedGames.installed["com.game"] = evidence
    repository.clickResult = Result.success(PaEInstallAnswer(eligible = true))

    m When "the app starts again"
    tracker().reportPending()

    m Then "the same click is sent again, then the report"
    assertEquals(
      listOf("click com.game click-1", "click com.game click-1", "report com.game click-1"),
      repository.calls
    )
    assertEquals(emptyList<PaEPendingInstall>(), pendingInstalls.all())
  }

  @Test
  fun `A click that fails again holds its report back`() = coScenario {
    m Given "a click that never reached the backend, for a game now installed"
    repository.clickResult = Result.failure(IOException("offline"))
    tracker().start("com.game", fromPaEView = true)
    installedGames.installed["com.game"] = evidence

    m When "the app starts again, still offline"
    tracker().reportPending()

    m Then "no report is sent, and the install waits for the next start"
    assertEquals(emptyList<List<String?>>(), repository.reports)
    assertEquals(listOf("click-1"), pendingInstalls.all().map { it.clickId })
  }

  @Test
  fun `A game that is not installed is never reported`() = coScenario {
    m Given "a recorded click for a game whose install has not finished, or was cancelled"
    val tracker = tracker()
    tracker.start("com.game", fromPaEView = true)

    m When "the app starts again"
    tracker.reportPending()

    m Then "no report is sent, and the install is still pending"
    assertEquals(emptyList<List<String?>>(), repository.reports)
    assertEquals(listOf("click-1"), pendingInstalls.all().map { it.clickId })
  }

  @Test
  fun `Another game finishing reports nothing for this one`() = coScenario {
    m Given "a recorded click for a game, now installed"
    val tracker = tracker()
    tracker.start("com.game", fromPaEView = true)
    installedGames.installed["com.game"] = evidence

    m When "another package is installed"
    tracker.reportPending("com.another")

    m Then "nothing is reported"
    assertEquals(emptyList<List<String?>>(), repository.reports)
  }

  @Test
  fun `The day's limit is told once, with its end, and the install is never reported`() =
    coScenario { scope ->
      m Given "a backend that answers the click with the day's install limit"
      val retryAt = Instant.parse("2026-11-28T00:00:00Z")
      repository.clickResult = Result.failure(
        PaERefusalException(PaERefusal(PaERefusalCode.INSTALL_LIMIT, retryAt))
      )
      val tracker = tracker()
      val told = mutableListOf<PaEInstallLimit>()
      scope.backgroundScope.launch(UnconfinedTestDispatcher(scope.testScheduler)) {
        tracker.installLimitReached.collect { told += it }
      }

      m When "the install starts from the view and the install manager, then finishes"
      tracker.start("com.game", fromPaEView = true)
      tracker.start("com.game", fromPaEView = false)
      installedGames.installed["com.game"] = evidence
      tracker.reportPending("com.game")

      m Then "the limit is told once with its end, one click went out and nothing is reported"
      assertEquals(listOf(PaEInstallLimit(retryAt)), told)
      assertEquals(listOf("click com.game click-1"), repository.calls)
      assertEquals(emptyList<PaEPendingInstall>(), pendingInstalls.all())
    }

  @Test
  fun `Other answers tell nothing`() = coScenario { scope ->
    m Given "someone listening for the limit"
    val tracker = tracker()
    val told = mutableListOf<PaEInstallLimit>()
    scope.backgroundScope.launch(UnconfinedTestDispatcher(scope.testScheduler)) {
      tracker.installLimitReached.collect { told += it }
    }

    m When "clicks are answered not eligible, restricted, unprocessable and unreachable"
    listOf(
      Result.success(PaEInstallAnswer(eligible = false)),
      Result.failure(PaERefusalException(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))),
      Result.failure(httpError(422)),
      Result.failure(httpError(503)),
      Result.failure(IOException("offline")),
    ).forEachIndexed { index, result ->
      repository.clickResult = result
      paeGames += "com.game$index"
      tracker.start("com.game$index", fromPaEView = false)
    }

    m Then "no limit is told; only the unreachable ones stay to be clicked again"
    assertEquals(emptyList<PaEInstallLimit>(), told)
    assertEquals(
      listOf(Click.RECORDED, Click.REFUSED, Click.REFUSED, Click.NOT_SENT, Click.NOT_SENT),
      pendingInstalls.all().map { it.click }
    )
  }

  @Test
  fun `A refused report is not tried again`() = coScenario {
    m Given "a recorded click for an installed game, and an account restricted since"
    val tracker = tracker()
    tracker.start("com.game", fromPaEView = true)
    installedGames.installed["com.game"] = evidence
    repository.reportResult =
      Result.failure(PaERefusalException(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED)))

    m When "the game's install finishes"
    tracker.reportPending("com.game")

    m Then "the install is forgotten"
    assertEquals(emptyList<PaEPendingInstall>(), pendingInstalls.all())
  }

  @Test
  fun `An account change forgets every pending install`() = coScenario {
    m Given "two pending installs"
    val tracker = tracker()
    paeGames += "com.other"
    tracker.start("com.game", fromPaEView = true)
    tracker.start("com.other", fromPaEView = false)

    m When "the account changes"
    tracker.forgetAccount()

    m Then "nothing is pending"
    assertEquals(emptyList<PaEPendingInstall>(), pendingInstalls.all())
  }

  @Test
  fun `A start told in the background runs off the caller`() = coScenario { scope ->
    m Given "a tracker working on its own scope"
    val tracker = tracker(scope)

    m When "an install start is told and the background work runs"
    tracker.onInstallStarted("com.game")
    scope.advanceUntilIdle()

    m Then "the click was sent"
    assertEquals(listOf("click com.game click-1"), repository.calls)
  }

  @Test
  fun `The installs finishing are reported as they come`() = coScenario { scope ->
    m Given "a recorded click and the stream of packages being installed"
    val tracker = tracker()
    tracker.start("com.game", fromPaEView = true)
    val installed = MutableSharedFlow<String>()
    scope.backgroundScope.launch(UnconfinedTestDispatcher(scope.testScheduler)) {
      tracker.initialize(installed)
    }

    m When "the game is installed"
    installedGames.installed["com.game"] = evidence
    installed.emit("com.game")

    m Then "its report is sent"
    assertEquals(listOf("click-1"), repository.reports.map { it[1] })
  }

  private fun httpError(code: Int) = HttpException(
    Response.error<Any>(code, "{}".toResponseBody("application/json".toMediaType()))
  )
}
