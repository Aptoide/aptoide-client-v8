package cm.aptoide.pt.v8engine.view.wizard;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.BackButtonFragment;

/**
 * Created by jdandrade on 18-07-2016.
 * This Fragment is responsible for setting up and inflating the First page in the Wizard.
 */
public class WizardPageOneFragment extends BackButtonFragment {

  private ClickHandler clickHandler;

  public static Fragment newInstance() {
    return new WizardPageOneFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.wizard_page_one, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    clickHandler = new ClickHandler() {
      @Override public boolean handle() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        return false;
      }
    };
    registerBackClickHandler(clickHandler);
  }

  @Override public void onDestroyView() {
    unregisterBackClickHandler(clickHandler);
    super.onDestroyView();
  }
}
