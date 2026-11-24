package com.aptoide.android.aptoidegames.bottom_bar

import cm.aptoide.pt.test.gherkin.scenario
import com.aptoide.android.aptoidegames.gamegenie.presentation.genieRoute
import com.aptoide.android.aptoidegames.home.BottomBarMenus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// APT-247: GameGenie is no longer a bottom bar destination, whatever the remote flags say.
internal class BottomBarItemsTest {

  @Test
  fun `The bar holds the four plain tabs`() = scenario {
    m Given "the search tab not replaced by GameGenie search"
    m When "the bar items are computed"
    val items = bottomNavigationItems.filter(shouldSearchGameGenie = false)

    m Then "they are Games, Search, Categories and Updates"
    assertEquals(
      listOf(
        BottomBarMenus.Games,
        BottomBarMenus.Search,
        BottomBarMenus.Categories,
        BottomBarMenus.Updates,
      ),
      items,
    )
  }

  @Test
  fun `The chat is never a bar destination`() = scenario {
    m Given "every value of the search flag"
    m When "the bar items are computed"
    val items = listOf(true, false).flatMap { bottomNavigationItems.filter(it) }

    m Then "none of them opens the chat route"
    assertTrue(items.none { it.route.substringBefore("?") == genieRoute })
  }
}
