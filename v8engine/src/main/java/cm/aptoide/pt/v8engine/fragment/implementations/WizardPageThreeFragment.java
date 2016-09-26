package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by jdandrade on 18-07-2016.
 * This Fragment is responsible for setting up and inflating the Third page in the Wizard.
 */
public class WizardPageThreeFragment extends Fragment {

  public static Fragment newInstance() {
    return new WizardPageThreeFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.wizard_page_three, null);
    setUpListeners(view);
    return view;
  }

  public void setUpListeners(View view) {
    final Button registerButton = (Button) view.findViewById(R.id.registerButton);
    final TextView loginLink = (TextView) view.findViewById(R.id.login_link);

    registerButton.setTransformationMethod(null);

    registerButton.setOnClickListener(view1 -> {
      AptoideAccountManager.openAccountManager(getContext(), true, false);
      getActivity().onBackPressed();
    });

    loginLink.setOnClickListener(view1 -> {
      AptoideAccountManager.openAccountManager(getContext(), true, false);
      getActivity().onBackPressed();
    });
  }
}
