package cm.aptoide.pt.device_api.paging

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PageTest {

  @Test
  fun `A page with a cursor has more to load`() = scenario {
    m Given "a page that came with a cursor"
    val page = Page(items = listOf(1, 2), nextCursor = "next")

    m When "it is asked whether there is more"
    val hasMore = page.hasMore

    m Then "there is"
    assertTrue(hasMore)
  }

  @Test
  fun `A page without a cursor is the last one`() = scenario {
    m Given "a page that came without a cursor"
    val page = Page(items = listOf(1, 2), nextCursor = null)

    m When "it is asked whether there is more"
    val hasMore = page.hasMore

    m Then "there is not"
    assertFalse(hasMore)
  }

  @Test
  fun `Mapping a page keeps its order and its cursor`() = scenario {
    m Given "a page of numbers with a cursor"
    val page = Page(items = listOf(1, 2, 3), nextCursor = "next")

    m When "its items are mapped"
    val mapped = page.map { "item $it" }

    m Then "the items are transformed in order and the cursor is the same"
    assertEquals(Page(listOf("item 1", "item 2", "item 3"), "next"), mapped)
  }
}
