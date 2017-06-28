package cm.aptoide.pt.spotandshareapp.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshareandroid.SpotAndShare;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.spotandshareapp.SpotAndShareImpl;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUser;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserManager;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserPersister;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareMainFragmentPresenter;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

/**
 * Created by filipe on 08-06-2017.
 */

public class SpotAndShareMainFragment extends FragmentView implements SpotAndShareMainFragmentView {

  private Button receiveButton;
  private Button sendButton;
  private ImageView editProfileButton;
  private ImageView userAvatar;
  private TextView username;
  private Toolbar toolbar;
  private SpotAndShareMainFragmentPresenter presenter;

  private SpotAndShare spotAndShare;

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareMainFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    spotAndShare = new SpotAndShareImpl(getContext());
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
    getFragmentNavigator().navigateTo(SpotAndShareWaitingToReceiveFragment.newInstance());
  }

  @Override public void openAppSelectionFragment() {
    getFragmentNavigator().navigateTo(SpotAndShareAppSelectionFragment.newInstance());
  }

  @Override public boolean requestPermissionToReceiveApp(AndroidAppInfo androidAppInfo) {
    // TODO: 19-06-2017 filipe
    return true;
  }

  @Override public void onCreateGroupError(Throwable throwable) {
    // TODO: 19-06-2017 filipe
    Toast.makeText(getContext(), R.string.spotandshare_message_error_create_group,
        Toast.LENGTH_SHORT)
        .show();
  }

  @Override public void onJoinGroupError(Throwable throwable) {
    // TODO: 19-06-2017 filipe
  }

  @Override public void openEditProfile() {
    getFragmentNavigator().navigateTo(SpotAndShareEditProfileFragment.newInstance());
  }

  @Override public void loadProfileInformation(SpotAndShareUser user) {
    username.setText(user.getUsername());
    setAvatar(user.getAvatar()
        .getResourceID());
  }

  private void setAvatar(int resourceID) {
    switch (resourceID) {
      case 0:
        userAvatar.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.green)));
        break;
      case 1:
        userAvatar.setImageDrawable(
            new ColorDrawable(getResources().getColor(R.color.aptoide_orange)));
        break;
      case 2:
        userAvatar.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.light_blue)));
        break;
      case 3:
        userAvatar.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.amber)));
        break;
      case 4:
        userAvatar.setImageDrawable(
            new ColorDrawable(getResources().getColor(R.color.grey_fog_normal)));
        break;
      case 5:
        userAvatar.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.teal_700)));
        break;
      default:
        userAvatar.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.green)));
        break;
    }
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
    presenter = new SpotAndShareMainFragmentPresenter(this, spotAndShare,
        new SpotAndShareUserManager(new SpotAndShareUserPersister(
            getContext().getSharedPreferences(SpotAndShareUserPersister.SHARED_PREFERENCES_NAME,
                Context.MODE_PRIVATE))));
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
}
