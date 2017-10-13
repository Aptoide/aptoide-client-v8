package cm.aptoide.pt.spotandshareapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.spotandshareapp.JoinGroupView;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareWaitingToReceivePresenter;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created by filipe on 12-06-2017.
 */

public class SpotAndShareWaitingToReceiveFragment extends BackButtonFragment
    implements SpotAndShareWaitingToReceiveView {

  private Toolbar toolbar;
  private PublishRelay<Void> backRelay;
  private ClickHandler clickHandler;
  private RxAlertDialog backDialog;
  private JoinGroupView joinGroupView;

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareWaitingToReceiveFragment();
    return fragment;
  }

  @Override public void onAttach(Activity activity) {
    if (activity instanceof JoinGroupView) {
      joinGroupView = (JoinGroupView) activity;
    } else {
      throw new IllegalArgumentException(
          "In order to use this framgent, you need to implement JoinGroupView");
    }
    super.onAttach(activity);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    backRelay = PublishRelay.create();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = (Toolbar) view.findViewById(R.id.spotandshare_toolbar);
    setupToolbar();
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

    joinGroupView.registerJoinGroupSuccessCallback(
        onSuccess -> openSpotandShareTransferRecordFragment());

    attachPresenter(new SpotAndShareWaitingToReceivePresenter(this,
            ((AptoideApplication) getActivity().getApplicationContext()).getSpotAndShare(),
            new PermissionManager(), (PermissionService) getContext(), CrashReport.getInstance()),
        savedInstanceState);
  }

  @Override public void onDestroyView() {
    toolbar = null;
    unregisterClickHandler(clickHandler);
    clickHandler = null;
    backDialog = null;
    joinGroupView.unregisterJoinGroupSuccessCallback();
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    backRelay = null;
    super.onDestroy();
  }

  private void setupToolbar() {
    setHasOptionsMenu(true);
    toolbar.setTitle(R.string.spotandshare_title_toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
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

  @Override public void openSpotandShareTransferRecordFragment() {
    getFragmentNavigator().cleanBackStack();
    getFragmentNavigator().navigateTo(SpotAndShareTransferRecordFragment.newInstance(), true);
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

  @Override public void onLeaveGroupError() {
    ShowMessage.asSnack(this, R.string.spotandshare_message_waiting_to_receive_leave_group_error);
  }

  @Override public void joinGroup() {
    joinGroupView.joinGroup();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      backRelay.call(null);
    }
    return false;
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }
}
