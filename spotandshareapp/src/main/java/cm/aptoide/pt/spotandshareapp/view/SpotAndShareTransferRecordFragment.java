package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareTransferRecordPresenter;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareTransferRecordFragment extends FragmentView
    implements SpotAndShareTransferRecordView {

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareTransferRecordFragment();
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spotandshare_transfer_record, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    attachPresenter(new SpotAndShareTransferRecordPresenter(this), savedInstanceState);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }

  @Override public void finish() {
    getActivity().finish();
  }
}
