package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareWaitingToReceivePresenter;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareWaitingToReceiveFragment extends FragmentView
    implements SpotAndShareWaitingToReceiveView {

  private ImageView refreshButton;

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareWaitingToReceiveFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    refreshButton = (ImageView) view.findViewById(R.id.sync_image);
    attachPresenter(new SpotAndShareWaitingToReceivePresenter(this), savedInstanceState);
  }

  @Override public void onDestroyView() {
    refreshButton = null;
    super.onDestroyView();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spotandshare_waiting_to_receive, container, false);
  }

  @Override public void finish() {
    getActivity().finish();
  }

  @Override public Observable<Void> startSearch() {
    return RxView.clicks(refreshButton);
  }

  @Override public void openSpotandShareTransferRecordFragment() {
    getFragmentNavigator().navigateTo(SpotAndShareTransferRecordFragment.newInstance());
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    finish();
    return super.onOptionsItemSelected(item);
  }
}
