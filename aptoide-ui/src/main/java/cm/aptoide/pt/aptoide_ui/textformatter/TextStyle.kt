package cm.aptoide.pt.aptoide_ui.textformatter

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle

@Composable
fun getMultiStyleString(
  @StringRes string: Int,
  placeholder: String,
  style: SpanStyle
): AnnotatedString {
  val totalText = stringResource(
    id = string,
    placeholder
  )
  val start = totalText.indexOf(placeholder)
  val spanStyles = listOf(
    AnnotatedString.Range(
      item = style,
      start = start,
      end = start + placeholder.length
    )
  )
  return AnnotatedString(
    text = totalText,
    spanStyles = spanStyles
  )
}
