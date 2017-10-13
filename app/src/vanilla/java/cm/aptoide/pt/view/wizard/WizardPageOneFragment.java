package cm.aptoide.pt.view.wizard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.view.BackButtonFragment;

/**
 * Created by jdandrade on 18-07-2016.
 * This Fragment is responsible for setting up and inflating the First page in the Wizard.
 */
public class WizardPageOneFragment extends BackButtonFragment {

  private ClickHandler clickHandler;

  public static Fragment newInstance() {
    return new WizardPageOneFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    pageViewsAnalytics.sendPageViewedEvent();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    clickHandler = new ClickHandler() {
      @Override public boolean handle() {
        return false;
      }
    };
    registerClickHandler(clickHandler);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_wizard_model_page, container, false);
  }

  @Override public void onDestroyView() {
    unregisterClickHandler(clickHandler);
    super.onDestroyView();
  }
}
