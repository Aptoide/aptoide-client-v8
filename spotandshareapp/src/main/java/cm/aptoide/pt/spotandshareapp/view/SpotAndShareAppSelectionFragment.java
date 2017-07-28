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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.AppModelToAndroidAppInfoMapper;
import cm.aptoide.pt.spotandshareapp.DrawableBitmapMapper;
import cm.aptoide.pt.spotandshareapp.Header;
import cm.aptoide.pt.spotandshareapp.InstalledRepositoryDummy;
import cm.aptoide.pt.spotandshareapp.ObbsProvider;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.spotandshareapp.SpotAndShareApplication;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareAppSelectionPresenter;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndSharePickAppsPresenter;
import cm.aptoide.pt.v8engine.presenter.CompositePresenter;
import cm.aptoide.pt.v8engine.view.BackButtonFragment;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Arrays;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipe on 28-07-2017.
 */

public class SpotAndShareAppSelectionFragment extends BackButtonFragment
    implements SpotAndShareAppSelectionView {

  private Toolbar toolbar;
  private PublishRelay<Void> backRelay;
  private RxAlertDialog backDialog;
  private ClickHandler clickHandler;

  private static String CREATE_GROUP_KEY = "CREATE_GROUP_KEY";
  private boolean shouldCreateGroup;
  private RecyclerView pickAppsRecyclerView;
  private SpotAndSharePickAppsAdapter pickAppsAdapter;
  private PublishSubject<AppModel> pickAppSubject;
  private View progressBarContainer;

  public static Fragment newInstance(boolean shouldCreateGroup) {
    Bundle args = new Bundle();
    args.putBoolean(CREATE_GROUP_KEY, shouldCreateGroup);
    Fragment fragment = new SpotAndShareAppSelectionFragment();
    Bundle arguments = fragment.getArguments();
    if (arguments != null) {
      args.putAll(arguments);
    }
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    backRelay = PublishRelay.create();

    shouldCreateGroup = getArguments().getBoolean(CREATE_GROUP_KEY);
    pickAppSubject = PublishSubject.create();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spotandshare_app_selection, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = (Toolbar) view.findViewById(R.id.spotandshare_toolbar);
    setupToolbar();
    setupBackClick();

    pickAppsRecyclerView = (RecyclerView) view.findViewById(R.id.app_selection_recycler_view);
    progressBarContainer = view.findViewById(R.id.app_selection_progress_bar);
    pickAppsAdapter = new SpotAndSharePickAppsAdapter(pickAppSubject,
        new Header(getResources().getString(R.string.spotandshare_title_pick_apps_to_send)));
    pickAppsRecyclerView.setAdapter(pickAppsAdapter);
    setupLayoutManager();
    pickAppsRecyclerView.setHasFixedSize(true);

    attachPresenters();
  }

  private void attachPresenters() {
    SpotAndShareAppSelectionPresenter spotAndShareAppSelectionPresenter =
        new SpotAndShareAppSelectionPresenter(this,
            ((SpotAndShareApplication) getActivity().getApplicationContext()).getSpotAndShare());

    SpotAndSharePickAppsPresenter spotAndSharePickAppsPresenter =
        new SpotAndSharePickAppsPresenter(this, shouldCreateGroup,
            new InstalledRepositoryDummy(getActivity().getApplicationContext(),
                getContext().getPackageManager()),
            ((SpotAndShareApplication) getActivity().getApplicationContext()).getSpotAndShare(),
            new DrawableBitmapMapper(getActivity().getApplicationContext()),
            new AppModelToAndroidAppInfoMapper(new ObbsProvider()));

    attachPresenter(new CompositePresenter(
        Arrays.asList(spotAndShareAppSelectionPresenter, spotAndSharePickAppsPresenter)), null);
  }

  private void setupBackClick() {
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

    pickAppsAdapter = null;
    pickAppsRecyclerView = null;
    progressBarContainer = null;
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    pickAppSubject = null;
    super.onDestroy();
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
    getFragmentNavigator().cleanBackStack();
    getFragmentNavigator().navigateToWithoutBackSave(SpotAndShareMainFragment.newInstance());
  }

  @Override public void finish() {
    getActivity().finish();
  }

  @Override public void buildInstalledAppsList(List<AppModel> installedApps) {
    pickAppsAdapter.setInstalledAppsList(installedApps);
    hideLoading();
  }

  @Override public void onLeaveGroupError() {
    Toast.makeText(getContext(), "There was an error while trying to leave the group",
        Toast.LENGTH_SHORT)
        .show();
  }

  @Override public Observable<AppModel> selectedApp() {
    return pickAppSubject;
  }

  @Override public void openTransferRecord() {
    getFragmentNavigator().cleanBackStack();
    getFragmentNavigator().navigateTo(SpotAndShareTransferRecordFragment.newInstance());
  }

  @Override public void openWaitingToSendScreen(AppModel selectedApp) {
    getFragmentNavigator().cleanBackStack();
    getFragmentNavigator().navigateTo(SpotAndShareWaitingToSendFragment.newInstance(selectedApp));
  }

  @Override public void onCreateGroupError(Throwable throwable) {
    getActivity().runOnUiThread(new Runnable() {
      @Override public void run() {
        Toast.makeText(getContext(), R.string.spotandshare_message_error_create_group,
            Toast.LENGTH_SHORT)
            .show();
      }
    });
  }

  private void setupLayoutManager() {
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 3);
    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override public int getSpanSize(int position) {
        if (pickAppsAdapter.isPositionHeader(position)) {
          return gridLayoutManager.getSpanCount();
        }
        return 1;
      }
    });
    pickAppsRecyclerView.setLayoutManager(gridLayoutManager);
  }

  @Override public void hideLoading() {
    progressBarContainer.setVisibility(View.GONE);
  }

  @Override public void showLoading() {
    progressBarContainer.setVisibility(View.VISIBLE);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backRelay.call(null);
    }
    return false;
  }
}
