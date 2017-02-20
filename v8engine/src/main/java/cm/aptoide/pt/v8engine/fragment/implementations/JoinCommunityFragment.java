package cm.aptoide.pt.v8engine.fragment.implementations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.FragmentView;

public class JoinCommunityFragment extends FragmentView {

  public static Fragment newInstance() {
    return new JoinCommunityFragment();
  }

  protected int getLayoutId() {
    return R.layout.fragment_join_the_community;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(getLayoutId(), container, false);
    // nested fragments only work using dynamic fragment addition.
    getActivity().getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.login_signup_layout, LoginSignUpFragment.newInstance())
        .commit();

    return view;
  }

  @Override public boolean onBackPressed() {
    // TODO: 20/2/2017 sithengineer  
    return super.onBackPressed();
  }
}
