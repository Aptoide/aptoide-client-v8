package cm.aptoide.pt.extensions

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import java.util.Locale
import kotlin.random.Random.Default
import kotlin.random.nextInt

val loremIpsum by lazy { LoremIpsum() }

fun getRandomString(
  range: IntRange? = null,
  separator: String = " ",
  capitalize: Boolean = false,
): String = loremIpsum.values
  // Join and split again to overcome probable error in the LoremIpsum itself: currently it returns
  // the whole string as a single value in a sequence rather than sequence of words ¯\_(ツ)_/¯
  .joinToString(" ")
  .split(" ")
  .shuffled()
  .run {
    take(range?.let(Default::nextInt) ?: size)
  }
  .joinToString(separator) { word ->
    word.replaceFirstChar {
      if (capitalize && it.isLowerCase()) {
        it.titlecase(Locale.getDefault())
      } else {
        "$it"
      }
    }
  }
