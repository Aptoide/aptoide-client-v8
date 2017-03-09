package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by jdandrade on 18-07-2016.
 * This Fragment is responsible for setting up and inflating the Second page in the Wizard.
 */
public class WizardPageTwoFragment extends Fragment {

  public static Fragment newInstance() {
    return new WizardPageTwoFragment();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.wizard_page_one, null);
    setText(view);
    return view;
  }

  private void setText(View view) {
    ((TextView) view.findViewById(android.R.id.text1)).setText(R.string.wizard_title_viewpager_two);
    ((TextView) view.findViewById(android.R.id.text2)).setText(
        R.string.wizard_sub_title_viewpager_two);
    ((ImageView) view.findViewById(android.R.id.icon)).setImageResource(R.drawable.wizard_two);
  }
}
