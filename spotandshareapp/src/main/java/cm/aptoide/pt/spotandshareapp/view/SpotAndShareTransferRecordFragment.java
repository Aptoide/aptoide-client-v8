package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.AppModelToAndroidAppInfoMapper;
import cm.aptoide.pt.spotandshareapp.DrawableBitmapMapper;
import cm.aptoide.pt.spotandshareapp.Header;
import cm.aptoide.pt.spotandshareapp.InstalledRepositoryDummy;
import cm.aptoide.pt.spotandshareapp.ObbsProvider;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.spotandshareapp.SpotAndShareApplication;
import cm.aptoide.pt.spotandshareapp.SpotAndShareInstallManager;
import cm.aptoide.pt.spotandshareapp.SpotAndShareTransferRecordManager;
import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndSharePickAppsPresenter;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareTransferRecordPresenter;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.presenter.CompositePresenter;
import cm.aptoide.pt.v8engine.view.BackButtonFragment;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Arrays;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareTransferRecordFragment extends BackButtonFragment
    implements SpotAndShareTransferRecordView {

  private final String TAG = getClass().getSimpleName();
  private Toolbar toolbar;
  private PublishRelay<Void> backRelay;
  private RxAlertDialog backDialog;
  private ClickHandler clickHandler;
  private RecyclerView transferRecordRecyclerView;
  private SpotAndShareTransferRecordAdapter transferRecordAdapter;
  private PublishSubject<TransferAppModel> acceptApp;
  private PublishSubject<TransferAppModel> installApp;

  private LinearLayout bottomSheet;
  private BottomSheetBehavior bottomSheetBehavior;
  private PublishSubject<AppModel> pickAppSubject;
  private SpotAndSharePickAppsAdapter pickAppsAdapter;
  private RecyclerView pickAppsRecyclerView;
  private View pickAppsProgressBarContainer;

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareTransferRecordFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    backRelay = PublishRelay.create();
    acceptApp = PublishSubject.create();
    installApp = PublishSubject.create();

    pickAppSubject = PublishSubject.create();
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
    setupTransferRecordRecyclerView();
    setupBackClick();

    bottomSheet = (LinearLayout) view.findViewById(R.id.bottom_sheet);
    configureBottomSheet();
    pickAppsProgressBarContainer = view.findViewById(R.id.app_selection_progress_bar);
    pickAppsRecyclerView = (RecyclerView) view.findViewById(R.id.app_selection_recycler_view);
    pickAppsAdapter = new SpotAndSharePickAppsAdapter(pickAppSubject,
        new Header(getResources().getString(R.string.spotandshare_title_pick_apps_to_send)));
    setupPickAppsRecyclerView();

    attachPresenters();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_spotandshare_transfer, menu);
  }

  private void configureBottomSheet() {
    bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    bottomSheetBehavior.setPeekHeight(
        AptoideUtils.ScreenU.getPixelsForDip(75, getContext().getResources()));
    bottomSheetBehavior.setHideable(true);
    bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

      @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {
        switch (newState) {
          case BottomSheetBehavior.STATE_HIDDEN:
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            break;
          case BottomSheetBehavior.STATE_EXPANDED:
            break;
          case BottomSheetBehavior.STATE_COLLAPSED:
            break;
          case BottomSheetBehavior.STATE_DRAGGING:
            break;
          case BottomSheetBehavior.STATE_SETTLING:
            break;
        }
      }

      @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {
      }
    });
  }

  private void setupPickAppsRecyclerView() {
    pickAppsRecyclerView.setAdapter(pickAppsAdapter);
    setupPickAppsRecyclerViewLayoutManager();
    pickAppsRecyclerView.setHasFixedSize(true);
  }

  private void setupPickAppsRecyclerViewLayoutManager() {
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

  private void setupTransferRecordRecyclerView() {
    transferRecordRecyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    transferRecordAdapter = new SpotAndShareTransferRecordAdapter(acceptApp, installApp);
    transferRecordRecyclerView.setAdapter(transferRecordAdapter);
  }

  private void attachPresenters() {

    SpotAndShareTransferRecordPresenter transferRecordPresenter =
        new SpotAndShareTransferRecordPresenter(this,
            ((SpotAndShareApplication) getActivity().getApplicationContext()).getSpotAndShare(),
            new SpotAndShareTransferRecordManager(getContext()),
            new SpotAndShareInstallManager(getActivity().getApplicationContext()));

    SpotAndSharePickAppsPresenter appSelectionPresenter =
        new SpotAndSharePickAppsPresenter(this, false,
            new InstalledRepositoryDummy(getActivity().getApplicationContext(),
                getContext().getPackageManager()),
            ((SpotAndShareApplication) getActivity().getApplicationContext()).getSpotAndShare(),
            new DrawableBitmapMapper(getActivity().getApplicationContext()),
            new AppModelToAndroidAppInfoMapper(new ObbsProvider()));

    attachPresenter(
        new CompositePresenter(Arrays.asList(transferRecordPresenter, appSelectionPresenter)),
        null);
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
    transferRecordAdapter.removeAll();
    transferRecordAdapter = null;
    transferRecordRecyclerView = null;

    pickAppsAdapter.removeAll();
    pickAppsAdapter = null;
    pickAppsRecyclerView = null;
    bottomSheetBehavior = null;
    bottomSheet = null;
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    backRelay = null;
    acceptApp = null;
    installApp = null;

    pickAppSubject = null;
    super.onDestroy();
  }

  @Override public void finish() {
    getActivity().finish();
  }

  @Override public void buildInstalledAppsList(List<AppModel> installedApps) {
    pickAppsAdapter.setInstalledAppsList(installedApps);
    hideLoading();
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
    getFragmentNavigator().cleanBackStack();
    getFragmentNavigator().navigateToWithoutBackSave(SpotAndShareMainFragment.newInstance());
  }

  @Override public void onLeaveGroupError() {
    Toast.makeText(getContext(), "There was an error while trying to leave the group",
        Toast.LENGTH_SHORT)
        .show();
  }

  @Override public Observable<AppModel> selectedApp() {
    return pickAppsAdapter.onSelectedApp();
  }

  @Override public void openTransferRecord() {
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
  }

  @Override public void openWaitingToSendScreen(AppModel selectedApp) {
    getFragmentNavigator().cleanBackStack();
    getFragmentNavigator().navigateTo(SpotAndShareWaitingToSendFragment.newInstance(selectedApp));
  }

  @Override public void onCreateGroupError(Throwable throwable) {

  }

  @Override public void hideLoading() {
    pickAppsProgressBarContainer.setVisibility(View.GONE);
  }

  @Override public void showLoading() {
    pickAppsProgressBarContainer.setVisibility(View.VISIBLE);
  }

  @Override public void updateReceivedAppsList(List<TransferAppModel> transferAppModelList) {
    transferRecordAdapter.updateTransferList(transferAppModelList);
  }

  @Override public void openAppSelectionFragment(boolean shouldCreateGroup) {
    getFragmentNavigator().cleanBackStack();
    getFragmentNavigator().navigateTo(
        SpotAndShareAppSelectionFragment.newInstance(shouldCreateGroup));
  }

  @Override public Observable<TransferAppModel> installApp() {
    return installApp;
  }

  @Override public void updateTransferInstallStatus(TransferAppModel transferAppModel) {
    transferAppModel.setInstalledApp(true);
    transferRecordAdapter.notifyDataSetChanged();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backRelay.call(null);
    }
    return false;
  }
}
