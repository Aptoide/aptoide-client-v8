package cm.aptoide.pt.app_games.terms_and_conditions

import androidx.annotation.StringRes
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import cm.aptoide.pt.app_games.R
import cm.aptoide.pt.app_games.UrlActivity
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.app_games.theme.richOrange
import cm.aptoide.pt.extensions.PreviewAll

@Composable
fun TermsAndConditions(
  modifier: Modifier = Modifier,
  textStyle: TextStyle,
  @StringRes textID: Int,
) {
  val context = LocalContext.current
  val open = stringResource(id = R.string.button_open_app_title)

  val privacyPolicyText = stringResource(id = R.string.notice_at_collection_and_privacy_policy)
  val termsText = stringResource(id = R.string.terms_and_conditions)
  val termsAndConditionsText: String = stringResource(id = textID, privacyPolicyText, termsText)

  val privacyPolicyStartIndex = termsAndConditionsText.indexOf(privacyPolicyText)
  val privacyPolicyEndIndex = privacyPolicyStartIndex + privacyPolicyText.length

  val termsStartIndex = termsAndConditionsText.indexOf(termsText)
  val termsEndIndex = termsStartIndex + termsText.length

  val annotatedString = buildAnnotatedString {
    append(termsAndConditionsText)
    addStyle(
      SpanStyle(
        color = richOrange,
        textDecoration = TextDecoration.Underline
      ),
      start = privacyPolicyStartIndex,
      end = privacyPolicyEndIndex
    )
    addStringAnnotation(
      tag = "policy",
      annotation = context.ppUrl,
      start = privacyPolicyStartIndex,
      end = privacyPolicyEndIndex
    )
    addStyle(
      SpanStyle(
        color = richOrange,
        textDecoration = TextDecoration.Underline
      ),
      start = termsStartIndex,
      end = termsEndIndex
    )
    addStringAnnotation(
      tag = "terms",
      annotation = context.tcUrl,
      start = termsStartIndex,
      end = termsEndIndex
    )
  }
  ClickableText(
    text = annotatedString,
    style = textStyle,
    modifier = modifier
      .semantics {
        customActions = listOf(
          CustomAccessibilityAction(
            label = "$open $privacyPolicyText",
            action = {
              UrlActivity.open(context, context.ppUrl)
              true
            }
          ),
          CustomAccessibilityAction(
            label = "$open $termsText",
            action = {
              UrlActivity.open(context, context.tcUrl)
              true
            }
          )
        )
      },
    onClick = { offset ->
      annotatedString
        .getStringAnnotations(offset, offset)
        .firstOrNull()
        ?.let { UrlActivity.open(context, it.item) }
    }
  )
}

@PreviewAll
@Composable
fun TermsAndConditionsPreview() {
  TermsAndConditions(
    modifier = Modifier,
    textStyle = AppTheme.typography.bodyCopyXS.copy(textAlign = TextAlign.Center),
    textID = R.string.continue_to_accept_tandc_and_pp_disclaimer
  )
}
