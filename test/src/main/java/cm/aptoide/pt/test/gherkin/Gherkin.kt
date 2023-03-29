package cm.aptoide.pt.test.gherkin

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

object Gherkin {
  var m: Step = Step()
    set(value) {
      require(busy.not()) { "Gherkin is busy" }
      field = value
    }
  internal var busy = false
}

fun scenario(test: Gherkin.() -> Unit) {
  require(Gherkin.busy.not()) { "scenarios nesting is forbidden" }
  try {
    Gherkin.m = Step()
    Gherkin.busy = true
    Gherkin.test()
    Gherkin.m.test()
  } finally {
    Gherkin.busy = false
  }
}

@ExperimentalCoroutinesApi
fun coScenario(test: suspend Gherkin.(scope: TestScope) -> Unit) {
  require(Gherkin.busy.not()) { "coScenarios nesting is forbidden" }
  try {
    Gherkin.m = Step()
    Gherkin.busy = true
    runTest {
      Gherkin.test(this)
    }
    Gherkin.m.test()
  } finally {
    Gherkin.busy = false
  }
}

class Step {
  private val calls: MutableSet<Int> = mutableSetOf()
  private val names: MutableSet<String> = mutableSetOf()

  infix fun Given(description: String) {
    require(description.isNotBlank()) { "'Given' description should not be blank" }
    require(calls.contains(0).not()) { "Only one 'Given' is allowed" }
    require(calls.isEmpty()) { "'Given' should be the first one to call" }
    require(names.contains(description).not()) { "Duplicated description: $description" }
    calls.add(0)
    names.add(description)
  }

  infix fun When(description: String) {
    require(description.isNotBlank()) { "'When' description should not be blank" }
    require(calls.contains(0)) { "'When' should be called after 'Given'" }
    require(calls.contains(1).not()) { "Only one 'When' is allowed" }
    require(names.contains(description).not()) { "Duplicated description: $description" }
    calls.add(1)
    names.add(description)
  }

  infix fun Then(description: String) {
    require(description.isNotBlank()) { "'Then' description should not be blank" }
    require(calls.contains(1)) { "'Then' should be called after 'When'" }
    require(calls.contains(2).not()) { "Only one 'Then' is allowed" }
    require(names.contains(description).not()) { "Duplicated description: $description" }
    calls.add(2)
    names.add(description)
  }

  infix fun And(description: String) {
    require(names.contains(description).not()) { "Duplicated description: $description" }
    require(calls.isNotEmpty()) { "'And' should be called after 'Given', 'When' or 'Then'" }
    names.add(description)
  }

  infix fun But(description: String) {
    require(names.contains(description).not()) { "Duplicated description: $description" }
    require(calls.isNotEmpty()) { "'But' should be called after 'Given', 'When' or 'Then'" }
    names.add(description)
  }

  internal fun test() {
    require(calls.contains(0)) { "'Given' is missing" }
    require(calls.contains(1)) { "'When' is missing" }
    require(calls.contains(2)) { "'Then' is missing" }
  }
}
