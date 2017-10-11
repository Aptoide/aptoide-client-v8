package cm.aptoide.pt.spotandshareapp.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.CompositePresenter;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.AppModelToAndroidAppInfoMapper;
import cm.aptoide.pt.spotandshareapp.DrawableBitmapMapper;
import cm.aptoide.pt.spotandshareapp.FileSizeConverter;
import cm.aptoide.pt.spotandshareapp.Header;
import cm.aptoide.pt.spotandshareapp.ObbsProvider;
import cm.aptoide.pt.spotandshareapp.SpotAndShareAppProvider;
import cm.aptoide.pt.spotandshareapp.SpotAndShareInstallManager;
import cm.aptoide.pt.spotandshareapp.SpotAndShareTransfer;
import cm.aptoide.pt.spotandshareapp.SpotAndShareTransferRecordManager;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUser;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserMapper;
import cm.aptoide.pt.spotandshareapp.TransferAppModel;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndSharePickAppsPresenter;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareTransferRecordPresenter;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxbinding.support.v7.widget.RxPopupMenu;
import com.jakewharton.rxbinding.view.RxView;
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
  private LinearLayout connectedFriendsLayout;
  private TextView connectedFriendsNumber;
  private PopupMenu friendsMenu;
  private PublishSubject<Void> clickedHeader;

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
    clickedHeader = PublishSubject.create();

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
    connectedFriendsNumber =
        (TextView) view.findViewById(R.id.spotandshare_toolbar_number_of_friends_connected);
    connectedFriendsLayout =
        (LinearLayout) view.findViewById(R.id.spotandshare_toolbar_friends_information_layout);
    setupBackClick();

    bottomSheet = (LinearLayout) view.findViewById(R.id.bottom_sheet);
    configureBottomSheet();
    pickAppsProgressBarContainer = view.findViewById(R.id.app_selection_progress_bar);
    pickAppsRecyclerView = (RecyclerView) view.findViewById(R.id.app_selection_recycler_view);
    pickAppsAdapter = new SpotAndSharePickAppsAdapter(
        new Header(getResources().getString(R.string.spotandshare_title_share_an_app)),
        clickedHeader, pickAppSubject);
    setupPickAppsRecyclerView();

    attachPresenters();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
  }

  private CharSequence menuIconWithText(Drawable r, String title) {

    r.setBounds(0, 0, 65, 65);
    SpannableString sb = new SpannableString("  " + title);
    ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BASELINE);
    sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    return sb;
  }

  private void configureBottomSheet() {
    bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    bottomSheetBehavior.setPeekHeight(
        AptoideUtils.ScreenU.getPixelsForDip(65, getContext().getResources()));
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
    transferRecordAdapter = new SpotAndShareTransferRecordAdapter(
        new SpotAndShareTransferRecordCardProvider(acceptApp, installApp));
    transferRecordRecyclerView.setAdapter(transferRecordAdapter);
  }

  private void attachPresenters() {

    SpotAndShareTransferRecordPresenter transferRecordPresenter =
        new SpotAndShareTransferRecordPresenter(this,
            ((AptoideApplication) getActivity().getApplicationContext()).getSpotAndShare(),
            new SpotAndShareTransferRecordManager(getContext(),
                new SpotAndShareUserMapper(new DrawableBitmapMapper(getContext())),
                new FileSizeConverter()),
            new SpotAndShareInstallManager(getActivity().getApplicationContext()),
            CrashReport.getInstance(),
            new SpotAndShareUserMapper(new DrawableBitmapMapper(getContext())));

    SpotAndSharePickAppsPresenter appSelectionPresenter =
        new SpotAndSharePickAppsPresenter(this, false,
            new SpotAndShareAppProvider(getActivity().getApplicationContext(),
                getContext().getPackageManager()),
            ((AptoideApplication) getActivity().getApplicationContext()).getSpotAndShare(),
            new AppModelToAndroidAppInfoMapper(new ObbsProvider()), CrashReport.getInstance());

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
    connectedFriendsNumber = null;
    connectedFriendsLayout = null;
    friendsMenu = null;

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
    clickedHeader = null;

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

  @Override public void back() {
    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    } else {
      backDialog.show();
    }
  }

  @Override public Observable<Void> exitEvent() {
    return backDialog.positiveClicks()
        .map(dialogInterface -> null);
  }

  @Override public void navigateBack() {
    getFragmentNavigator().cleanBackStack();
    getFragmentNavigator().navigateToWithoutBackSave(SpotAndShareMainFragment.newInstance(), true);
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
    pickAppsRecyclerView.scrollToPosition(0);
    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
  }

  @Override public void openWaitingToSendScreen(AppModel selectedApp) {
    getFragmentNavigator().cleanBackStack();
    getFragmentNavigator().navigateTo(
        SpotAndShareWaitingToSendFragment.newInstance(selectedApp, false), true);
  }

  @Override public void onCreateGroupError(Throwable throwable) {

  }

  @Override public void hideLoading() {
    pickAppsProgressBarContainer.setVisibility(View.GONE);
  }

  @Override public void showLoading() {
    pickAppsProgressBarContainer.setVisibility(View.VISIBLE);
  }

  @Override public void updateReceivedAppsList(List<SpotAndShareTransfer> transferAppModelList) {
    transferRecordAdapter.updateTransferList(transferAppModelList);
    if (transferAppModelList.size() > 0) {
      transferRecordRecyclerView.smoothScrollToPosition(transferAppModelList.size() - 1);
    }
  }

  @Override public Observable<TransferAppModel> installApp() {
    return installApp;
  }

  @Override public void updateTransferInstallStatus(TransferAppModel transferAppModel) {
    transferAppModel.setInstalledApp(true);
    transferRecordAdapter.notifyDataSetChanged();
  }

  @Override public Observable<Void> clickedFriendsInformationButton() {
    return RxView.clicks(connectedFriendsLayout);
  }

  @Override public void showFriendsNumber(int numberOfFriends) {
    connectedFriendsNumber.setText(String.valueOf(numberOfFriends));
    connectedFriendsLayout.setVisibility(View.VISIBLE);
  }

  @Override public void hideFriendsNumber() {
    connectedFriendsLayout.setVisibility(View.GONE);
  }

  @Override public void showFriendsOnMenu(List<SpotAndShareUser> friendsList) {
    this.friendsMenu = new PopupMenu(this.getContext(), connectedFriendsLayout);

    MenuInflater inflate = friendsMenu.getMenuInflater();
    inflate.inflate(R.menu.menu_spotandshare_transfer, friendsMenu.getMenu());
    for (int i = 0; i < friendsList.size(); i++) {
      friendsMenu.getMenu()
          .add(R.id.spotandshare_toolbar_friends_menu_group, i, i, menuIconWithText(
              friendsList.get(i)
                  .getAvatar(), friendsList.get(i)
                  .getUsername()));
    }
    friendsMenu.show();
  }

  @Override public Observable<Void> friendsMenuDismiss() {
    return RxPopupMenu.dismisses(friendsMenu);
  }

  @Override public void clearMenu() {
    friendsMenu = null;
  }

  @Override public Observable<Void> listenBottomSheetHeaderClicks() {
    return pickAppsAdapter.onBottomSheetHeaderClick();
  }

  @Override public void pressedBottomSheetHeader() {
    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    } else {
      bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      backRelay.call(null);
    }
    return false;
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }
}
