package cm.aptoide.pt.view.reviews;

import android.content.res.Resources;
import android.support.annotation.StringRes;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Delegate;

/**
 * Created by neuro on 28-08-2017.
 */
@Getter class LanguageFilterHelper {

  private final LanguageFilter all;
  private final LanguageFilter currentLanguageFirst;
  private final LanguageFilter english;

  private final String currentCountryCode;

  LanguageFilterHelper(Resources resources) {
    all = new LanguageFilter(R.string.comments_filter_comments_by_language_all,
        Collections.emptyList());
    currentCountryCode = AptoideUtils.SystemU.getCountryCode(resources);
    currentLanguageFirst =
        new LanguageFilter(R.string.comments_filter_comments_by_language_current_language_first,
            currentCountryCode + ",en_GB");
    english = new LanguageFilter(R.string.comments_filter_comments_by_language_english, "en_GB");
  }

  @Getter static class LanguageFilter {

    @StringRes private final int stringId;
    private final List<String> countryCodes;
    @Delegate private final String value = computeLazyValue();

    LanguageFilter(@StringRes int stringId, String countryCode) {
      this(stringId, Collections.singletonList(countryCode));
    }

    LanguageFilter(@StringRes int stringId, List<String> countryCodes) {
      this.stringId = stringId;
      this.countryCodes = countryCodes;
    }

    private String computeLazyValue() {
      String result = null;

      for (String countryCode : countryCodes) {
        if (result == null) {
          result = countryCode;
        } else {
          result += "," + countryCode;
        }
      }

      return result;
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
