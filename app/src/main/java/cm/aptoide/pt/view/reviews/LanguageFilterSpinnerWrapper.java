package cm.aptoide.pt.view.reviews;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 28-08-2017.
 */

class LanguageFilterSpinnerWrapper {

  private final Spinner spinner;
  private final LanguageFilterHelper languageFilterHelper;
  private final LanguageFilterSpinnerWrapper.onItemSelected onItemSelected;
  private final Resources resources;
  private final Context context;

  LanguageFilterSpinnerWrapper(Spinner spinner, onItemSelected onItemSelected) {
    this.spinner = spinner;
    this.resources = spinner.getResources();
    this.context = spinner.getContext();
    languageFilterHelper = new LanguageFilterHelper(resources);
    this.onItemSelected = onItemSelected;
  }

  private void setupOnItemSelectedListener() {
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        LanguageFilterHelper.LanguageFilter languageFilter = null;

        if (view instanceof TextView) {
          CharSequence text = ((TextView) view).getText();
          for (LanguageFilterHelper.LanguageFilter filter : languageFilterHelper.getLanguageFilterList()) {
            if (text.equals(resources.getString(filter.getStringId()))) {
              languageFilter = filter;
            }
          }
        }

        LanguageFilterSpinnerWrapper.this.onItemSelected.onItemSelected(languageFilter);
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  private void setSelection(String string) {
    for (int i = 0; i < spinner.getAdapter()
        .getCount(); i++) {
      Object item = spinner.getAdapter()
          .getItem(i);
      if (string.equals(item)) {
        spinner.setSelection(i);
      }
    }
  }

  private void setAdapter(SpinnerAdapter adapter) {
    spinner.setAdapter(adapter);
    setupOnItemSelectedListener();
    setSelection(resources.getString(getDefaultSelectionId()));
  }

  private @StringRes int getDefaultSelectionId() {
    return languageFilterHelper.getCurrentLanguageFirst()
        .getStringId();
  }

  void setup() {
    setAdapter(setupCommentsFilterLanguageSpinnerAdapter());
    setupLanguageSpinnerClickListener((View) spinner.getParent());
  }

  private SpinnerAdapter setupCommentsFilterLanguageSpinnerAdapter() {
    return new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,
        createSpinnerAdapterRowsList());
  }

  private List<String> createSpinnerAdapterRowsList() {
    List<LanguageFilterHelper.LanguageFilter> languageFilterList =
        languageFilterHelper.getLanguageFilterList();

    List<String> strings = new LinkedList<>();

    for (LanguageFilterHelper.LanguageFilter languageFilter : languageFilterList) {
      strings.add(resources.getString(languageFilter.getStringId()));
    }

    return strings;
  }

  private void setupLanguageSpinnerClickListener(View itemView) {
    itemView.setOnClickListener(v -> spinner.performClick());
  }

  interface onItemSelected {
    void onItemSelected(LanguageFilterHelper.LanguageFilter languageFilter);
  }
}
