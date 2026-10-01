package cm.aptoide.pt.feature_home.di

import cm.aptoide.pt.feature_home.data.WidgetsRepository
import cm.aptoide.pt.feature_home.domain.Widget
import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import java.util.Optional

// Every build shares this module, and only one of them binds an override. The ones that do not
// must keep getting the very same v7 repository they always had.
internal class BackendSelectionTest {

  @Test
  fun `A build that binds no override keeps the v7 repository`() = scenario {
    m Given "the v7 repository and no override"
    val v7 = StubWidgetsRepository()

    m When "the widgets repository is provided"
    val provided = RepositoryModule.providesWidgetsRepository(
      override = Optional.empty(),
      v7 = { v7 },
    )

    m Then "it is the v7 one"
    assertSame(v7, provided)
  }

  @Test
  fun `A build that binds an override gets it`() = scenario {
    m Given "the v7 repository and an override"
    val v7 = StubWidgetsRepository()
    val override = StubWidgetsRepository()

    m When "the widgets repository is provided"
    val provided = RepositoryModule.providesWidgetsRepository(
      override = Optional.of(override),
      v7 = { v7 },
    )

    m Then "it is the override"
    assertSame(override, provided)
  }
}

private class StubWidgetsRepository : WidgetsRepository {
  override suspend fun getStoreWidgets(context: String?, bypassCache: Boolean): List<Widget> =
    emptyList()
}
