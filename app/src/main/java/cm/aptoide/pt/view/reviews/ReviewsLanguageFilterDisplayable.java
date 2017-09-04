package cm.aptoide.pt.view.reviews;

import android.widget.Spinner;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import lombok.Getter;

/**
 * Created by neuro on 04-09-2017.
 */

public class ReviewsLanguageFilterDisplayable extends Displayable {

  @Getter private LanguageFilterSpinnerWrapper.OnItemSelected onItemSelected;

  public ReviewsLanguageFilterDisplayable() {
  }

  public ReviewsLanguageFilterDisplayable(
      LanguageFilterSpinnerWrapper.OnItemSelected onItemSelected) {
    this.onItemSelected = onItemSelected;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.reviews_language_filter;
  }

  private LanguageFilterSpinnerWrapper languageFilterSpinnerWrapper;

  public void setup(Spinner spinner) {
    if (languageFilterSpinnerWrapper == null) {
      languageFilterSpinnerWrapper = new LanguageFilterSpinnerWrapper(spinner);
      languageFilterSpinnerWrapper.setup(
          languageFilter -> onItemSelected.onItemSelected(languageFilter));
    }
  }
}
