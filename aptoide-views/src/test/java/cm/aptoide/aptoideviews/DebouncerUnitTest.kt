package cm.aptoide.aptoideviews

import cm.aptoide.aptoideviews.common.Debouncer
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DebouncerUnitTest {

  private val debouncer = Debouncer(500)

  /**
   *  Timings may differ from machine to machine. Please check test time to expected result.
   *  We should ideally use a more compliant test solution, such as awaitility
   */

  @Test
  fun beforeTimeInputTest() {
    var changed = false
    debouncer.reset()
    debouncer.execute { changed = true }
    Thread.sleep(100)
    debouncer.execute { changed = true }
    Thread.sleep(100)
    debouncer.execute { changed = true }
    Thread.sleep(100)
    debouncer.execute { changed = true }

    assertEquals(changed, false)
  }

  @Test
  fun afterTimeInputTest() {
    var changed = false
    debouncer.reset()
    Thread.sleep(500)
    debouncer.execute { changed = true }

    assertEquals(changed, true)
  }

  @Test
  fun repeatedInputTest() {
    var changed = false
    debouncer.reset()
    Thread.sleep(100)
    debouncer.execute { changed = true }
    assertEquals(changed, false)
    Thread.sleep(100)
    debouncer.execute { changed = true }
    assertEquals(changed, false)
    Thread.sleep(100)
    debouncer.execute { changed = true }
    assertEquals(changed, false)
    Thread.sleep(50)
    debouncer.execute { changed = true }
    assertEquals(changed, false)
    Thread.sleep(150)
    debouncer.execute { changed = true }

    Thread.sleep(100)
    debouncer.execute { changed = false }
    assertEquals(changed, true)
    Thread.sleep(100)
    debouncer.execute { changed = false }
    assertEquals(changed, true)
    Thread.sleep(100)
    debouncer.execute { changed = false }
    assertEquals(changed, true)
    Thread.sleep(50)
    debouncer.execute { changed = false }
    assertEquals(changed, true)
    Thread.sleep(150)
    debouncer.execute { changed = false }

    assertEquals(changed, false)
  }
}
