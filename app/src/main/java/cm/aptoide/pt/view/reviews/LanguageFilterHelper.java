package cm.aptoide.pt.view.reviews;

import android.content.res.Resources;
import android.support.annotation.StringRes;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.Arrays;
import java.util.Collections;
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
    all = new LanguageFilter(R.string.comments_filter_comments_by_language_all,
        Collections.emptyList());
    currentCountryCode = AptoideUtils.SystemU.getCountryCode(resources);

    List<String> countryCodes;
    if (currentCountryCode.startsWith("en")) {
      countryCodes = Arrays.asList(currentCountryCode);
    } else {
      countryCodes = Arrays.asList(currentCountryCode, LanguageCode.en_GB.toString());
    }

    currentLanguageFirst =
        new LanguageFilter(R.string.comments_filter_comments_by_language_current_language_first,
            countryCodes);
    english = new LanguageFilter(R.string.comments_filter_comments_by_language_english,
        LanguageCode.en_GB.toString());
  }

  @Getter static class LanguageFilter {

    @StringRes private final int stringId;
    private final List<String> countryCodes;
    private int position = 0;

    LanguageFilter(@StringRes int stringId, String countryCode) {
      this(stringId, Collections.singletonList(countryCode));
    }

    LanguageFilter(@StringRes int stringId, List<String> countryCodes) {
      this.stringId = stringId;
      this.countryCodes = countryCodes;
    }

    public LanguageFilter inc() {
      position++;
      return this;
    }

    public void setPosition(int position) {
      this.position = position;
    }

    public String getValue() {
      if (countryCodes.size() > 0) {
        return countryCodes.get(position);
      } else {
        return null;
      }
    }

    public boolean hasMoreCountryCodes() {
      return countryCodes.size() > position + 1;
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

  public enum LanguageCode {
    en_GB,
  }
}
