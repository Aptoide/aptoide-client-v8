package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// Bundles reach their apps through an url kept by tag, which shared code passes around and
// adds to without knowing what it points at. The listings of the new services are written as
// such an url, so what shared code does to one has to keep it readable.
internal class NewServicesListingUrlTest {

  @Test
  fun `A listing survives being written and read back`() = scenario {
    m Given "a listing of nine action games by downloads"
    val listing = NewServicesListing(category = "game_action", sort = "downloads", limit = 9)

    m When "it is written as an url and read back"
    val read = NewServicesListingUrl.parse(NewServicesListingUrl.build(listing))

    m Then "it is the same listing"
    assertEquals(listing, read)
  }

  @Test
  fun `A listing without a limit is written without one`() = scenario {
    m Given "a listing with no limit"
    val listing = NewServicesListing(category = "games", sort = "downloads", limit = null)

    m When "it is written as an url and read back"
    val url = NewServicesListingUrl.build(listing)

    m Then "no limit is made up"
    assertEquals("newservices/listApps/category=games/sort=downloads", url)
    assertEquals(listing, NewServicesListingUrl.parse(url))
  }

  @Test
  fun `The url is one shared code offers to see all of`() = scenario {
    m Given "any listing"
    val listing = NewServicesListing(category = "games", sort = "downloads", limit = 9)

    m When "it is written as an url"
    val url = NewServicesListingUrl.build(listing)

    m Then "it holds the marker bundles look for before offering to see all"
    assertTrue("listApps/" in url)
  }

  @Test
  fun `The limit added for see all wins over the one of the row`() = scenario {
    m Given "the url of a row of nine, with the limit bundles add to see all"
    val url = NewServicesListingUrl.build(
      NewServicesListing(category = "game_action", sort = "downloads", limit = 9)
    ) + "/limit=50"

    m When "it is read"
    val listing = NewServicesListingUrl.parse(url)

    m Then "the last limit is the one that counts"
    assertEquals(NewServicesListing("game_action", "downloads", limit = 50), listing)
  }

  @Test
  fun `A limit added as an encoded path segment is still read`() = scenario {
    m Given "the url of a row, with a limit appended as an encoded path segment"
    val url = "newservices/listApps/category=game_action/sort=downloads/limit%3D12"

    m When "it is read"
    val listing = NewServicesListingUrl.parse(url)

    m Then "the limit is read"
    assertEquals(12, listing?.limit)
  }

  @Test
  fun `A listing without a sort is by downloads`() = scenario {
    m Given "an url that names only a category"
    val url = "newservices/listApps/category=game_puzzle"

    m When "it is read"
    val listing = NewServicesListingUrl.parse(url)

    m Then "it is sorted by downloads"
    assertEquals(NewServicesListing("game_puzzle", "downloads", limit = null), listing)
  }

  @Test
  fun `A v7 url is not a listing of the new services`() = scenario {
    m Given "the url of a v7 listing"
    val url = "https://ws75.aptoide.com/api/7/listApps/store_id=15/sort=downloads/limit=9"

    m When "it is read"
    val listing = NewServicesListingUrl.parse(url)

    m Then "there is no listing, so that it goes to v7"
    assertNull(listing)
  }

  @Test
  fun `An url without a category is not a listing`() = scenario {
    m Given "an url of the new services that names no category"
    val url = "newservices/listApps/sort=downloads"

    m When "it is read"
    val listing = NewServicesListingUrl.parse(url)

    m Then "there is no listing, as the service would answer with the whole catalog"
    assertNull(listing)
  }

  @Test
  fun `A limit that is not a number is ignored`() = scenario {
    m Given "an url whose limit is not a number"
    val url = "newservices/listApps/category=games/limit=many"

    m When "it is read"
    val listing = NewServicesListingUrl.parse(url)

    m Then "the listing has no limit"
    assertEquals(NewServicesListing("games", "downloads", limit = null), listing)
  }

  @Test
  fun `A segment that cannot be decoded is ignored`() = scenario {
    m Given "an url with a segment holding a broken percent escape"
    val url = "newservices/listApps/category=game_action/limit=%"

    m When "it is read"
    val listing = NewServicesListingUrl.parse(url)

    m Then "the rest of the listing is still read, instead of the read failing"
    assertEquals(NewServicesListing("game_action", "downloads", limit = null), listing)
  }
}
