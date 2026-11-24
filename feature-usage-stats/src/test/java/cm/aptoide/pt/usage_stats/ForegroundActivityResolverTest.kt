package cm.aptoide.pt.usage_stats

import cm.aptoide.pt.test.gherkin.scenario
import cm.aptoide.pt.usage_stats.ActivityEvent.Type.PAUSED
import cm.aptoide.pt.usage_stats.ActivityEvent.Type.RESUMED
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ForegroundActivityResolverTest {

  private fun event(pkg: String, type: ActivityEvent.Type, cls: String = "$pkg.Main") =
    ActivityEvent(packageName = pkg, className = cls, type = type)

  private fun resolve(vararg events: ActivityEvent) =
    ForegroundActivityResolver.resolve(events.asSequence())

  @Test
  fun `The only resumed app is the foreground and shares nothing`() = scenario {
    m Given "the launcher left for the game"
    val events = arrayOf(event(LAUNCHER, RESUMED), event(LAUNCHER, PAUSED), event(GAME, RESUMED))

    m When "the state is resolved"
    val state = resolve(*events)

    m Then "the game is the foreground and alone"
    assertEquals(PackageUsageState.ForegroundPackage(GAME, sharesScreen = false), state)
  }

  @Test
  fun `Two resumed apps mean multi-window, so the screen is shared`() = scenario {
    m Given "split-screen with multi-resume (Android 10+): both halves resumed, the game last"
    val events = arrayOf(event(OTHER, RESUMED), event(GAME, RESUMED))

    m When "the state is resolved"
    val state = resolve(*events)

    m Then "the game is the foreground but shares the screen"
    assertEquals(PackageUsageState.ForegroundPackage(GAME, sharesScreen = true), state)
  }

  @Test
  fun `A paused app never stopped does not count as sharing the screen`() = scenario {
    m Given "a sign-in sheet opened and closed from the game: paused, no stop ever reported"
    val events = arrayOf(
      event(GAME, RESUMED),
      event(GAME, PAUSED),
      event(OTHER, RESUMED),
      event(OTHER, PAUSED),
      event(GAME, RESUMED),
    )

    m When "the state is resolved"
    val state = resolve(*events)

    m Then "the game is alone: a stale pause must not stop it earning for the whole session"
    assertEquals(PackageUsageState.ForegroundPackage(GAME, sharesScreen = false), state)
  }

  @Test
  fun `A game put in picture-in-picture is not the foreground`() = scenario {
    m Given "the game went to picture-in-picture: paused, and the launcher resumed"
    val events = arrayOf(
      event(GAME, RESUMED),
      event(GAME, PAUSED),
      event(LAUNCHER, RESUMED),
    )

    m When "the state is resolved"
    val state = resolve(*events)

    m Then "the launcher is the foreground"
    assertEquals(PackageUsageState.ForegroundPackage(LAUNCHER, sharesScreen = false), state)
  }

  @Test
  fun `A package with one of two activities paused is still resumed`() = scenario {
    m Given "the game with two resumed activities, one of them paused"
    val events = arrayOf(
      event(GAME, RESUMED, cls = "A"),
      event(GAME, RESUMED, cls = "B"),
      event(GAME, PAUSED, cls = "B"),
    )

    m When "the state is resolved"
    val state = resolve(*events)

    m Then "the game is still the foreground"
    assertEquals(PackageUsageState.ForegroundPackage(GAME, sharesScreen = false), state)
  }

  @Test
  fun `Everything paused means no foreground app`() = scenario {
    m Given "the screen locked: the game paused"
    val events = arrayOf(event(GAME, RESUMED), event(GAME, PAUSED))

    m When "the state is resolved"
    val state = resolve(*events)

    m Then "there is no foreground app"
    assertEquals(PackageUsageState.NoForegroundPackage, state)
  }

  @Test
  fun `No events at all is an error, not a foreground app`() = scenario {
    m Given "an empty query window"

    m When "the state is resolved"
    val state = resolve()

    m Then "it is an error, so no time is counted"
    assertEquals(PackageUsageState.Error, state)
  }

  private companion object {
    const val GAME = "com.game"
    const val OTHER = "com.signin"
    const val LAUNCHER = "com.launcher"
  }
}
