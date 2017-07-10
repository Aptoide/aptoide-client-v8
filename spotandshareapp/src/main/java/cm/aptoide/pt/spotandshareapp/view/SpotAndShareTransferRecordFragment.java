package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.spotandshareapp.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.SpotAndShareTransferRecordManager;
import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareTransferRecordPresenter;
import cm.aptoide.pt.v8engine.view.BackButtonFragment;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareTransferRecordFragment extends BackButtonFragment
    implements SpotAndShareTransferRecordView {

  private Toolbar toolbar;
  private PublishRelay<Void> backRelay;
  private RxAlertDialog backDialog;
  private ClickHandler clickHandler;
  private RecyclerView transferRecordRecyclerView;
  private SpotAndShareTransferRecordAdapter adapter;
  private PublishSubject<TransferAppModel> acceptApp;

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareTransferRecordFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    backRelay = PublishRelay.create();
    acceptApp = PublishSubject.create();
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
    clickHandler = () -> {
      backRelay.call(null);
      return true;
    };
    registerClickHandler(clickHandler);
    backDialog = new RxAlertDialog.Builder(getContext()).setMessage(
        R.string.spotandshare_message_leave_group_warning)
        .setPositiveButton(R.string.spotandshare_button_leave_group)
        .setNegativeButton(R.string.spotandshare_button_cancel_leave_group)
        .build();
    attachPresenter(
        new SpotAndShareTransferRecordPresenter(this, SpotAndShare.getInstance(getContext()),
            new SpotAndShareTransferRecordManager(getContext())), savedInstanceState);
  }

  private void setupRecyclerView() {
    transferRecordRecyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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
    unregisterClickHandler(clickHandler);
    clickHandler = null;
    backDialog = null;
    transferRecordRecyclerView = null;
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    backRelay = null;
    acceptApp = null;
    super.onDestroy();
  }

  @Override public void finish() {
    getActivity().finish();
  }

  @Override public Observable<TransferAppModel> acceptApp() {
    return acceptApp;
  }

  @Override public Observable<Void> backButtonEvent() {
    return backRelay;
  }

  @Override public void showExitWarning() {
    backDialog.show();
  }

  @Override public Observable<Void> exitEvent() {
    return backDialog.positiveClicks()
        .map(dialogInterface -> null);
  }

  @Override public void navigateBack() {
    getFragmentNavigator().popBackStack();
  }

  @Override public void onLeaveGroupError() {
    Toast.makeText(getContext(), "There was an error while trying to leave the group",
        Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void updateReceivedAppsList(List<TransferAppModel> transferAppModelList) {
    adapter.updateTransferList(transferAppModelList);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backRelay.call(null);
    }
    return false;
  }
}
