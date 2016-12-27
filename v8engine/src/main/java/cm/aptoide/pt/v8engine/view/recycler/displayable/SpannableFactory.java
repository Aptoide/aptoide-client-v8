package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.support.annotation.NonNull;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class SpannableFactory {

  @NonNull public Spannable createStyleSpan(String text, int style, String... spanTexts) {
    final Spannable result = new SpannableString(text);
    for (String spanText : spanTexts) {
      int spanTextStart = text.indexOf(spanText);
      if (spanTextStart >= 0
          && spanTextStart < text.length()
          && spanText.length() <= text.length()) {
        result.setSpan(new StyleSpan(style), spanTextStart, (spanTextStart + spanText.length()),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      }
    }
    return result;
  }

  @NonNull public Spannable createColorSpan(String text, int color, String... spanTexts) {
    Spannable result = new SpannableString(text);
    for (String spanText : spanTexts) {
      int spanTextStart = text.indexOf(spanText);
      if (spanTextStart >= 0
          && spanTextStart < text.length()
          && spanText.length() <= text.length()) {
        result.setSpan(new ForegroundColorSpan(color), spanTextStart,
            (spanTextStart + spanText.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      }
    }
    return result;
  }

  /**
   * Returns a {@link Spannable} which attaches the specified {@link ParcelableSpan} to the given
   * substring
   *
   * @param text Text
   * @param span array of {@link ParcelableSpan} that will be applied
   * @param spanTexts array of substrings that will be modified
   * @return {@link Spannable}
   */
  @NonNull public Spannable createSpan(String text, ParcelableSpan[] span, String... spanTexts) {

    Spannable result = new SpannableString(text);

    if (span != null && spanTexts != null && span.length <= spanTexts.length) {

      for (int i = 0; i < span.length; i++) {
        String spanText = spanTexts[i];
        int spanTextStart = text.indexOf(spanText);
        if (spanTextStart >= 0
            && spanTextStart < text.length()
            && spanText.length() <= text.length()) {
          result.setSpan(span[i], spanTextStart, (spanTextStart + spanText.length()),
              Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
      }
    }
    return result;
  }
}