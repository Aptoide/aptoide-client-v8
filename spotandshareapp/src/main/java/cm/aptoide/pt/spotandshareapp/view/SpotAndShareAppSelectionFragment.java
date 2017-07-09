package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.Header;
import cm.aptoide.pt.spotandshareapp.InstalledRepositoryDummy;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.spotandshareapp.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareAppSelectionPresenter;
import cm.aptoide.pt.v8engine.view.BackButtonFragment;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareAppSelectionFragment extends BackButtonFragment
    implements SpotAndShareAppSelectionView {

  private RecyclerView recyclerView;
  private SpotAndShareAppSelectionAdapter adapter;
  private Toolbar toolbar;
  private PublishRelay<Void> backRelay;
  private ClickHandler clickHandler;
  private RxAlertDialog backDialog;

  private PublishSubject<AppModel> appSubject;

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareAppSelectionFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    backRelay = PublishRelay.create();
    appSubject = PublishSubject.create();
  }

  @Override public void finish() {
    getActivity().finish();
  }

  @Override public void setupRecyclerView(List<AppModel> installedApps) {
    adapter = new SpotAndShareAppSelectionAdapter(appSubject,
        new Header(getResources().getString(R.string.spotandshare_title_pick_apps_to_send)),
        installedApps);
    recyclerView.setAdapter(adapter);
    setupLayoutManager();
    recyclerView.setHasFixedSize(true);
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

  @Override public Observable<AppModel> appSelection() {
    return appSubject;
  }

  @Override public void openTransferRecord() {
    getFragmentNavigator().navigateToWithoutBackSave(
        SpotAndShareTransferRecordFragment.newInstance());
  }

  @Override public void openWaitingToSendScreen() {
    //// TODO: 07-07-2017 filipe create waiting to send screen
    getFragmentNavigator().navigateToWithoutBackSave(
        SpotAndShareWaitingToSendFragment.newInstance());
  }

  private void setupLayoutManager() {
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 3);
    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        if (adapter.isPositionHeader(position)) {
          return gridLayoutManager.getSpanCount();
        }
        return 1;
      }
    });
    recyclerView.setLayoutManager(gridLayoutManager);
  }

  @Override public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = (Toolbar) view.findViewById(R.id.spotandshare_toolbar);
    setupToolbar();
    recyclerView = (RecyclerView) view.findViewById(R.id.app_selection_recycler_view);
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
    attachPresenter(new SpotAndShareAppSelectionPresenter(this,
        new InstalledRepositoryDummy(getContext().getPackageManager()),
        SpotAndShare.getInstance(getContext())), savedInstanceState);
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
    adapter = null;
    recyclerView = null;
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    backRelay = null;
    appSubject = null;
    super.onDestroy();
  }

  @Nullable @Override
  public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spotandshare_app_selection, container, false);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backRelay.call(null);
    }
    return false;
  }
}
