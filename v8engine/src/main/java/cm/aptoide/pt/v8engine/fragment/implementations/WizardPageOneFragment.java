package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by jdandrade on 18-07-2016.
 * This Fragment is responsible for setting up and inflating the First page in the Wizard.
 */
public class WizardPageOneFragment extends Fragment {

  public static Fragment newInstance() {
    return new WizardPageOneFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.wizard_page_one, null);
  }
}
