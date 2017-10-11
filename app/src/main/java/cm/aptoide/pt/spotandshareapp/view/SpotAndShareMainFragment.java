package cm.aptoide.pt.spotandshareapp.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalUser;
import cm.aptoide.pt.spotandshareapp.WriteSettingsPermissionProvider;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareMainFragmentPresenter;
import cm.aptoide.pt.view.fragment.FragmentView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created by filipe on 08-06-2017.
 */

public class SpotAndShareMainFragment extends FragmentView
    implements SpotAndShareMainFragmentView, WriteSettingsPermissionProvider {

  private Button receiveButton;
  private Button sendButton;
  private ImageView editProfileButton;
  private ImageView userAvatar;
  private TextView username;
  private Toolbar toolbar;
  private TextView shareAptoideTextView;
  private SpotAndShareMainFragmentPresenter presenter;
  private PublishRelay<Integer> writeSettingsPermissionRelay;
  private AptoideNavigationTracker aptoideNavigationTracker;

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareMainFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    writeSettingsPermissionRelay = PublishRelay.create();
    aptoideNavigationTracker =
        ((AptoideApplication) getContext().getApplicationContext()).getAptoideNavigationTracker();
  }

  @Override public void onResume() {
    super.onResume();
    aptoideNavigationTracker.registerScreen(ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName()));
  }

  @Override public void finish() {
    getActivity().finish();
  }

  @Override public Observable<Void> startSend() {
    return RxView.clicks(sendButton);
  }

  @Override public Observable<Void> startReceive() {
    return RxView.clicks(receiveButton);
  }

  @Override public Observable<Void> editProfile() {
    return RxView.clicks(editProfileButton);
  }

  @Override public void openWaitingToReceiveFragment() {
    getFragmentNavigator().navigateTo(SpotAndShareWaitingToReceiveFragment.newInstance(), true);
  }

  @Override public void openAppSelectionFragment(boolean shouldCreateGroup) {
    getFragmentNavigator().navigateTo(
        SpotAndShareAppSelectionFragment.newInstance(shouldCreateGroup), true);
  }

  @Override public void openEditProfile() {
    getFragmentNavigator().navigateTo(SpotAndShareEditProfileFragment.newInstance(), true);
  }

  @Override public void loadProfileInformation(SpotAndShareLocalUser user) {
    username.setText(user.getUsername());
    setAvatar(user.getAvatar());
  }

  @Override public Observable<Void> shareAptoideApk() {
    return RxView.clicks(shareAptoideTextView);
  }

  @Override public void openShareAptoideFragment() {
    getFragmentNavigator().navigateTo(ShareAptoideFragment.newInstance(), true);
  }

  private void setAvatar(SpotAndShareAvatar avatar) {
    userAvatar.setSelected(true);
    ImageLoader.with(getContext())
        .load(avatar.getString(), userAvatar);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    receiveButton = (Button) view.findViewById(R.id.receive_button);
    sendButton = (Button) view.findViewById(R.id.send_button);
    editProfileButton = (ImageView) view.findViewById(R.id.edit_profile_button);
    userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
    username = (TextView) view.findViewById(R.id.username);
    toolbar = (Toolbar) view.findViewById(R.id.spotandshare_toolbar);
    setupToolbar();
    shareAptoideTextView = (TextView) view.findViewById(R.id.share_aptoide_apk_button);
    presenter = new SpotAndShareMainFragmentPresenter(this,
        ((AptoideApplication) getActivity().getApplicationContext()).getSpotAndShareUserManager(),
        CrashReport.getInstance());
    attachPresenter(presenter, savedInstanceState);
  }

  private void setupToolbar() {
    setHasOptionsMenu(true);
    toolbar.setTitle(R.string.spotandshare_title_toolbar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

  @Override public void onDestroyView() {
    receiveButton = null;
    sendButton = null;
    toolbar = null;
    shareAptoideTextView = null;
    super.onDestroyView();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spotandshare_main_fragment, container, false);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    finish();
    return super.onOptionsItemSelected(item);
  }

  @Override public void requestWriteSettingsPermission(int requestCode) {
    if (isWriteSettingsPermissionGranted()) {
      writeSettingsPermissionRelay.call(requestCode);
    } else {
      Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
      intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
      startActivityForResult(intent, requestCode);
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case SpotAndShareMainFragmentPresenter.WRITE_SETTINGS_REQUEST_CODE_RECEIVE:
      case SpotAndShareMainFragmentPresenter.WRITE_SETTINGS_REQUEST_CODE_SEND:
      case SpotAndShareMainFragmentPresenter.WRITE_SETTINGS_REQUEST_CODE_SHARE_APTOIDE:
        if (isWriteSettingsPermissionGranted()) {
          writeSettingsPermissionRelay.call(requestCode);
        }
        break;
      default:
        break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private boolean isWriteSettingsPermissionGranted() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return Settings.System.canWrite(getContext());
    } else {
      return true;
    }
  }

  @Override public Observable<Integer> permissionResult() {
    return writeSettingsPermissionRelay;
  }
}
