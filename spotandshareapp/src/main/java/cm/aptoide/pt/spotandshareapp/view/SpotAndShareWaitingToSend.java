package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;

/**
 * Created by filipe on 07-07-2017.
 */

public class SpotAndShareWaitingToSend extends FragmentView
    implements SpotAndShareWaitingToSendView {

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareAppSelectionFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }

  @Nullable @Override
  public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spotandshare_app_selection, container, false);
  }
}
