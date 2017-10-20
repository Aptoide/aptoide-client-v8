package cm.aptoide.pt.spotandshareapp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.spotandshareapp.AppModel;
import cm.aptoide.pt.spotandshareapp.AppModelToAndroidAppInfoMapper;
import cm.aptoide.pt.spotandshareapp.DrawableBitmapMapper;
import cm.aptoide.pt.spotandshareapp.ObbsProvider;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareWaitingToSendPresenter;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.BackButton;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created by filipe on 07-07-2017.
 */

public class SpotAndShareWaitingToSendFragment extends BackButtonFragment
    implements SpotAndShareWaitingToSendView {

  private final static String APP_NAME = "appName";
  private final static String PACKAGE_NAME = "packageName";
  private final static String FILE_PATH = "filePath";
  private final static String OBBS_FILEPATH = "obbsFilePath";
  private final static String APP_ICON = "appIcon";
  private final static String SHOULD_CREATE_GROUP = "shouldCreateGroup";
  private Toolbar toolbar;
  private PublishRelay<Void> backRelay;
  private BackButton.ClickHandler clickHandler;
  private RxAlertDialog backDialog;
  private ImageView appIcon;
  private TextView appName;
  private AppModel selectedApp;
  private boolean shouldCreateGroup;

  public static Fragment newInstance(AppModel appModel, boolean shouldCreateGroup) {
    Bundle args = new Bundle();
    args.putString(APP_NAME, appModel.getAppName());
    args.putString(PACKAGE_NAME, appModel.getPackageName());
    args.putString(FILE_PATH, appModel.getFilePath());
    args.putString(OBBS_FILEPATH, appModel.getObbsFilePath());
    args.putByteArray(APP_ICON, appModel.getAppIconAsByteArray());
    args.putBoolean(SHOULD_CREATE_GROUP, shouldCreateGroup);
    Fragment fragment = new SpotAndShareWaitingToSendFragment();
    Bundle arguments = fragment.getArguments();
    if (arguments != null) {
      args.putAll(arguments);
    }
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String appName = getArguments().getString(APP_NAME);
    String packageName = getArguments().getString(PACKAGE_NAME);
    String filePath = getArguments().getString(FILE_PATH);
    String obbsFilePath = getArguments().getString(OBBS_FILEPATH);
    byte[] appIcon = getArguments().getByteArray(APP_ICON);
    shouldCreateGroup = getArguments().getBoolean(SHOULD_CREATE_GROUP);

    selectedApp = new AppModel(appName, packageName, filePath, obbsFilePath, appIcon,
        new DrawableBitmapMapper(getActivity().getApplicationContext()));
    backRelay = PublishRelay.create();
  }

  @Override public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = (Toolbar) view.findViewById(R.id.spotandshare_toolbar);
    appIcon = (ImageView) view.findViewById(R.id.sending_app_avatar);
    appName = (TextView) view.findViewById(R.id.sending_app_name);
    setupSendingAppInfo();
    setupToolbar();
    backDialog = new RxAlertDialog.Builder(getContext()).setMessage(
        R.string.spotandshare_message_leave_group_warning)
        .setPositiveButton(R.string.spotandshare_button_leave_group)
        .setNegativeButton(R.string.spotandshare_button_cancel_leave_group)
        .build();
    clickHandler = () -> {
      backRelay.call(null);
      return true;
    };
    registerClickHandler(clickHandler);

    attachPresenter(new SpotAndShareWaitingToSendPresenter(shouldCreateGroup, this,
        ((AptoideApplication) getActivity().getApplicationContext()).getSpotAndShare(),
        new AppModelToAndroidAppInfoMapper(new ObbsProvider()), new PermissionManager(),
        (PermissionService) getContext(), CrashReport.getInstance()), savedInstanceState);
  }

  private void setupSendingAppInfo() {
    appIcon.setImageDrawable(selectedApp.getAppIconAsDrawable());
    appName.setText(selectedApp.getAppName());
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
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    backRelay = null;
    super.onDestroy();
  }

  @Nullable @Override
  public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spotandshare_waiting_to_send, container, false);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backRelay.call(null);
    }
    return false;
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
    getFragmentNavigator().navigateToWithoutBackSave(SpotAndShareMainFragment.newInstance(), true);
  }

  @Override public void navigateBackWithStateLoss() {
    getFragmentNavigator().navigateToAllowingStateLoss(SpotAndShareMainFragment.newInstance(),
        true);
  }

  @Override public void onLeaveGroupError() {
    ShowMessage.asSnack(this, R.string.spotandshare_message_waiting_to_send_leave_group);
  }

  @Override public void openTransferRecord() {
    getActivity().runOnUiThread(new Runnable() {
      @Override public void run() {
        getFragmentNavigator().cleanBackStack();
        getFragmentNavigator().navigateTo(SpotAndShareTransferRecordFragment.newInstance(), true);
      }
    });
  }

  @Override public AppModel getSelectedApp() {
    return selectedApp;
  }

  @Override public void showTimeoutCreateGroupError() {
    getActivity().runOnUiThread(new Runnable() {
      @Override public void run() {
        ShowMessage.asSnack(getActivity(), R.string.spotandshare_message_timeout_creating_hotspot);
      }
    });
  }

  @Override public void showGeneralCreateGroupError() {
    getActivity().runOnUiThread(new Runnable() {
      @Override public void run() {
        ShowMessage.asSnack(getActivity(), R.string.spotandshare_message_error_create_group);
      }
    });
  }

  @Override public void finish() {
    getActivity().finish();
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }
}
