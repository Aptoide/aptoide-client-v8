package cm.aptoide.pt.view.reviews;

import android.content.res.Resources;
import android.support.annotation.StringRes;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;

/**
 * Created by neuro on 28-08-2017.
 */
@Getter class LanguageFilterHelper {

  private final LanguageFilter all;
  private final LanguageFilter currentLanguageFirst;
  private final LanguageFilter english;

  private final String currentCountryCode;

  LanguageFilterHelper(Resources resources) {
    all = new LanguageFilter(R.string.comments_filter_comments_by_language_all, null);
    currentCountryCode = AptoideUtils.SystemU.getCountryCode(resources);
    currentLanguageFirst =
        new LanguageFilter(R.string.comments_filter_comments_by_language_current_language_first,
            currentCountryCode + ",en_GB");
    english = new LanguageFilter(R.string.comments_filter_comments_by_language_english, "en_GB");
  }

  @Getter static class LanguageFilter {

    @StringRes private final int stringId;
    private final String value;

    LanguageFilter(@StringRes int stringId, String value) {
      this.stringId = stringId;
      this.value = value;
    }
  }

  List<LanguageFilter> getLanguageFilterList() {
    List<LanguageFilter> languageFilterList = new LinkedList<>();

    languageFilterList.add(all);
    languageFilterList.add(currentLanguageFirst);

    if (!currentCountryCode.startsWith("en")) {
      languageFilterList.add(english);
    }

    return languageFilterList;
  }
}
