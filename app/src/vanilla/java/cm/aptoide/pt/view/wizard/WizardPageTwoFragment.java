package cm.aptoide.pt.view.wizard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.view.BackButton;
import cm.aptoide.pt.view.BackButtonFragment;

/**
 * Created by jdandrade on 18-07-2016.
 * This Fragment is responsible for setting up and inflating the Second page in the Wizard.
 */
public class WizardPageTwoFragment extends BackButtonFragment {

  private BackButton.ClickHandler clickHandler;

  public static Fragment newInstance() {
    return new WizardPageTwoFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_wizard_model_page, container, false);
    setText(view);
    return view;
  }

  private void setText(View view) {
    ((TextView) view.findViewById(R.id.title)).setText(R.string.wizard_title_viewpager_two);
    ((TextView) view.findViewById(R.id.description)).setText(
        R.string.wizard_sub_title_viewpager_two);
    ((ImageView) view.findViewById(android.R.id.icon)).setImageResource(R.drawable.wizard_two);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    clickHandler = new BackButton.ClickHandler() {
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

  @Override public void onDestroyView() {
    unregisterClickHandler(clickHandler);
    super.onDestroyView();
  }
}
