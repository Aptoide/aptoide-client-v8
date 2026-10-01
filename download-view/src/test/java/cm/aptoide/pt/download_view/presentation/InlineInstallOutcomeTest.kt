package cm.aptoide.pt.download_view.presentation

import android.content.Intent
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// What an install does once no external install could be started. Most apps continue
// through the regular path; the ones that may only ever install externally end as canceled
// or as an error the user can retry, and must never reach Aptoide's installer.
internal class InlineInstallOutcomeTest {

  private val app = randomApp

  @Test
  fun `Without a resolver the regular install runs`() = scenario {
    m Given "a build that binds no resolver"

    m When "no external install could be started"
    val outcome = inlineInstallOutcome(resolver = null, app = app, ladderExhausted = false)

    m Then "the regular install runs"
    assertEquals(InlineInstallOutcome.REGULAR_INSTALL, outcome)
  }

  @Test
  fun `An app allowed to fall back continues through the regular install`() = scenario {
    m Given "a resolver that lets the app fall back"
    val resolver = FakeResolver(requiresInline = false, allowsFallback = true)

    m When "the resolver started nothing"
    val outcome = inlineInstallOutcome(resolver, app, ladderExhausted = false)

    m Then "the regular install runs"
    assertEquals(InlineInstallOutcome.REGULAR_INSTALL, outcome)
  }

  @Test
  fun `An app that requires an external install ends as an error when none started`() =
    scenario {
      m Given "a resolver that requires the app to install externally"
      val resolver = FakeResolver(requiresInline = true, allowsFallback = true)

      m When "the resolver started nothing"
      val outcome = inlineInstallOutcome(resolver, app, ladderExhausted = false)

      m Then "the install ends as an error the user can retry, never through Aptoide"
      assertEquals(InlineInstallOutcome.ERROR, outcome)
    }

  @Test
  fun `An app that requires an external install ends as an error when the ladder is exhausted`() =
    scenario {
      m Given "a resolver that requires the app to install externally"
      val resolver = FakeResolver(requiresInline = true, allowsFallback = true)

      m When "every external stage was rejected"
      val outcome = inlineInstallOutcome(resolver, app, ladderExhausted = true)

      m Then "the install ends as an error the user can retry"
      assertEquals(InlineInstallOutcome.ERROR, outcome)
    }

  @Test
  fun `An app that may not fall back ends as canceled when the ladder is exhausted`() =
    scenario {
      m Given "a resolver that forbids the app to fall back, as for the overlay titles"
      val resolver = FakeResolver(requiresInline = false, allowsFallback = false)

      m When "every external stage was rejected"
      val outcome = inlineInstallOutcome(resolver, app, ladderExhausted = true)

      m Then "the install ends as canceled, as it did before"
      assertEquals(InlineInstallOutcome.CANCELED, outcome)
    }

  @Test
  fun `An app that may not fall back still runs the regular install before any stage ran`() =
    scenario {
      m Given "a resolver that forbids the app to fall back"
      val resolver = FakeResolver(requiresInline = false, allowsFallback = false)

      m When "the resolver started nothing at all"
      val outcome = inlineInstallOutcome(resolver, app, ladderExhausted = false)

      m Then "the regular install runs, as it did before"
      assertEquals(InlineInstallOutcome.REGULAR_INSTALL, outcome)
    }
}

private class FakeResolver(
  private val requiresInline: Boolean,
  private val allowsFallback: Boolean,
) : InlineInstallResolver {
  override suspend fun resolveInlineInstall(app: App): Intent? = null

  override fun requiresInlineInstall(app: App): Boolean = requiresInline

  override fun allowsRegularFallback(app: App): Boolean = allowsFallback
}
