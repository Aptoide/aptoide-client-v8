package cm.aptoide.pt.test.gherkin

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GherkinTests {

  @Nested
  inner class ScenarioTest {

    @Test
    fun `Minimal test`() = scenario {
      m Given "Given persists"
      m When "When persists"
      m Then "Then persists"
    }

    @Test
    fun `Maximal test`() = scenario {
      m Given "Given persists"
      m And "Given And persists"
      m But "Given But persists"
      m When "When persists"
      m And "When And persists"
      m But "When But persists"
      m Then "Then persists"
      m And "Then And persists"
      m But "Then But persists"
    }

    @Test
    fun `Double Given in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m Given "Given persists"
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("Only one 'Given' is allowed", thrown.message)
    }

    @Test
    fun `Double When in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m Given "Given persists"
          m When "When persists"
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("Only one 'When' is allowed", thrown.message)
    }

    @Test
    fun `Double Then in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("Only one 'Then' is allowed", thrown.message)
    }

    @Test
    fun `Missing Given in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("'When' should be called after 'Given'", thrown.message)
    }

    @Test
    fun `Missing When in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m Given "Given persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("'Then' should be called after 'When'", thrown.message)
    }

    @Test
    fun `Missing Then in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m Given "Given persists"
          m When "When persists"
        }
      }
      Assertions.assertEquals("'Then' is missing", thrown.message)
    }

    @Test
    fun `Only Given in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m Given "Given persists"
        }
      }
      Assertions.assertEquals("'When' is missing", thrown.message)
    }

    @Test
    fun `Only When in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m When "When persists"
        }
      }
      Assertions.assertEquals("'When' should be called after 'Given'", thrown.message)
    }

    @Test
    fun `Only Then in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("'Then' should be called after 'When'", thrown.message)
    }

    @Test
    fun `First And in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m And "And persists"
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals(
        "'And' should be called after 'Given', 'When' or 'Then'",
        thrown.message
      )
    }

    @Test
    fun `First But in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m But "And persists"
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals(
        "'But' should be called after 'Given', 'When' or 'Then'",
        thrown.message
      )
    }

    @Test
    fun `Duplicated docs in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m Given "Given persists"
          m When "When persists"
          m Then "Given persists"
        }
      }
      Assertions.assertEquals("Duplicated description: Given persists", thrown.message)
    }

    @Test
    fun `Folded scenarios in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
          scenario {
            m Given "Given persists"
            m When "When persists"
            m Then "Then persists"
          }
        }
      }
      Assertions.assertEquals("scenarios nesting is forbidden", thrown.message)
    }

    @Test
    fun `Step overriding in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        scenario {
          m = Step()
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("Gherkin is busy", thrown.message)
    }
  }

  @ExperimentalCoroutinesApi
  @Nested
  inner class CoScenarioTest {

    @Test
    fun `Minimal test`() = coScenario {
      m Given "Given persists"
      m When "When persists"
      m Then "Then persists"
    }

    @Test
    fun `Maximal test`() = coScenario {
      m Given "Given persists"
      m And "Given And persists"
      m But "Given But persists"
      m When "When persists"
      m And "When And persists"
      m But "When But persists"
      m Then "Then persists"
      m And "Then And persists"
      m But "Then But persists"
    }

    @Test
    fun `Double Given in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m Given "Given persists"
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("Only one 'Given' is allowed", thrown.message)
    }

    @Test
    fun `Double When in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m Given "Given persists"
          m When "When persists"
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("Only one 'When' is allowed", thrown.message)
    }

    @Test
    fun `Double Then in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("Only one 'Then' is allowed", thrown.message)
    }

    @Test
    fun `Missing Given in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("'When' should be called after 'Given'", thrown.message)
    }

    @Test
    fun `Missing When in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m Given "Given persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("'Then' should be called after 'When'", thrown.message)
    }

    @Test
    fun `Missing Then in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m Given "Given persists"
          m When "When persists"
        }
      }
      Assertions.assertEquals("'Then' is missing", thrown.message)
    }

    @Test
    fun `Only Given in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m Given "Given persists"
        }
      }
      Assertions.assertEquals("'When' is missing", thrown.message)
    }

    @Test
    fun `Only When in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m When "When persists"
        }
      }
      Assertions.assertEquals("'When' should be called after 'Given'", thrown.message)
    }

    @Test
    fun `Only Then in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("'Then' should be called after 'When'", thrown.message)
    }

    @Test
    fun `First And in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m And "And persists"
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals(
        "'And' should be called after 'Given', 'When' or 'Then'",
        thrown.message
      )
    }

    @Test
    fun `First But in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m But "And persists"
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals(
        "'But' should be called after 'Given', 'When' or 'Then'",
        thrown.message
      )
    }

    @Test
    fun `Duplicated docs in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m Given "Given persists"
          m When "When persists"
          m Then "Given persists"
        }
      }
      Assertions.assertEquals("Duplicated description: Given persists", thrown.message)
    }

    @Test
    fun `Folded coScenarios in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
          coScenario {
            m Given "Given persists"
            m When "When persists"
            m Then "Then persists"
          }
        }
      }
      Assertions.assertEquals("coScenarios nesting is forbidden", thrown.message)
    }

    @Test
    fun `Step overriding in a test`() {
      val thrown: IllegalArgumentException = assertThrows {
        coScenario {
          m = Step()
          m Given "Given persists"
          m When "When persists"
          m Then "Then persists"
        }
      }
      Assertions.assertEquals("Gherkin is busy", thrown.message)
    }
  }
}
