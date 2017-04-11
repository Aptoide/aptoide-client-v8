package cm.aptoide.pt.v8engine.view.wizard;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;

/**
 * Created by jdandrade on 18-07-2016.
 * This Fragment is responsible for setting up and inflating the First page in the Wizard.
 */
public class WizardPageOneFragment extends FragmentView {

  public static Fragment newInstance() {
    return new WizardPageOneFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.wizard_page_one, null);
  }

  @Override public boolean onBackPressed() {
    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    return super.onBackPressed();
  }
}
