package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class SpannableFactory {

  @NonNull public Spannable createStyleSpan(String text, int style, String... spanTexts) {
    final Spannable result = new SpannableString(text);
    for (String spanText : spanTexts) {
      int spanTextStart = text.indexOf(spanText);
      result.setSpan(new StyleSpan(style), spanTextStart, (spanTextStart + spanText.length()),
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    return result;
  }

  @NonNull public Spannable createColorSpan(String text, int color, String... spanTexts) {
    final Spannable result = new SpannableString(text);
    for (String spanText : spanTexts) {
      int spanTextStart = text.indexOf(spanText);
      result.setSpan(new ForegroundColorSpan(color), spanTextStart,
          (spanTextStart + spanText.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    return result;
  }
}