package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareTransferRecordPresenter;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareTransferRecordFragment extends FragmentView
    implements SpotAndShareTransferRecordView {

  private Toolbar toolbar;
  private RecyclerView transferRecordRecyclerView;
  private SpotAndShareTransferRecordAdapter adapter;
  private PublishSubject<AndroidAppInfo> acceptApp;

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
    toolbar = (Toolbar) view.findViewById(R.id.spotandshare_toolbar);
    setupToolbar();
    transferRecordRecyclerView =
        (RecyclerView) view.findViewById(R.id.transfer_record_recycler_view);
    setupRecyclerView();
    attachPresenter(new SpotAndShareTransferRecordPresenter(this), savedInstanceState);
  }

  private void setupRecyclerView() {
    acceptApp = PublishSubject.create();
    adapter = new SpotAndShareTransferRecordAdapter(null, acceptApp);
    transferRecordRecyclerView.setAdapter(adapter);
  }

  private void setupToolbar() {
    setHasOptionsMenu(true);
    toolbar.setTitle(R.string.spotandshare_title_toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

  @Override public void onDestroyView() {
    toolbar = null;
    transferRecordRecyclerView = null;
    super.onDestroyView();
  }

  @Override public void finish() {
    getActivity().finish();
  }

  @Override public Observable<AndroidAppInfo> acceptApp() {
    return acceptApp;
  }
}
