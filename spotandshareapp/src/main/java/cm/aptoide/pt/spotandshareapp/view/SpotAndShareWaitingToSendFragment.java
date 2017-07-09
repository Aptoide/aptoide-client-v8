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
import android.widget.Toast;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.spotandshareapp.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareWaitingToSendPresenter;
import cm.aptoide.pt.v8engine.view.BackButton;
import cm.aptoide.pt.v8engine.view.BackButtonFragment;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created by filipe on 07-07-2017.
 */

public class SpotAndShareWaitingToSendFragment extends BackButtonFragment
    implements SpotAndShareWaitingToSendView {

  private Toolbar toolbar;
  private PublishRelay<Void> backRelay;
  private BackButton.ClickHandler clickHandler;
  private RxAlertDialog backDialog;

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareWaitingToSendFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    backRelay = PublishRelay.create();
  }

  @Override public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = (Toolbar) view.findViewById(R.id.spotandshare_toolbar);
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
    attachPresenter(
        new SpotAndShareWaitingToSendPresenter(this, SpotAndShare.getInstance(getContext())),
        savedInstanceState);
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
    getFragmentNavigator().popBackStack();
  }

  @Override public void onLeaveGroupError() {
    Toast.makeText(getContext(), "There was an error while trying to leave the group",
        Toast.LENGTH_SHORT)
        .show();
  }
}
