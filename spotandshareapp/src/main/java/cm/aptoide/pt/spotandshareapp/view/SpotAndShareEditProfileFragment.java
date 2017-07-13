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
import android.widget.EditText;
import android.widget.ImageView;
import cm.aptoide.pt.spotandshareapp.R;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUser;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserAvatar;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserManager;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserPersister;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareEditProfilePresenter;
import cm.aptoide.pt.v8engine.view.fragment.FragmentView;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

/**
 * Created by filipe on 21-06-2017.
 */

public class SpotAndShareEditProfileFragment extends FragmentView
    implements SpotAndShareEditProfileView {

  private ImageView firstAvatar;
  private ImageView secondAvatar;
  private ImageView thirdAvatar;
  private ImageView fourthAvatar;
  private ImageView fifthAvatar;
  private ImageView sixthAvatar;
  private ImageView actualAvatar;
  private EditText usernameEditText;
  private Button saveProfile;
  private Button cancel;
  private List<ImageView> defaultAvatarList;
  private Toolbar toolbar;
  private int selectedAvatar = 0;

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareEditProfileFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void finish() {
    getActivity().finish();
  }

  @Override public Observable<Void> cancelProfileChanges() {
    return RxView.clicks(cancel);
  }

  @Override public Observable<SpotAndShareUser> saveProfileChanges() {
    return RxView.clicks(saveProfile)
        .map(aVoid -> new SpotAndShareUser(getUsername(), getAvatar()));
  }

  @Override public void goBack() {
    getFragmentNavigator().popBackStack();
  }

  @Override public Observable<Void> selectedFirstAvatar() {
    return RxView.clicks(firstAvatar);
  }

  @Override public Observable<Void> selectedSecondAvatar() {
    return RxView.clicks(secondAvatar);
  }

  @Override public Observable<Void> selectedThirdAvatar() {
    return RxView.clicks(thirdAvatar);
  }

  @Override public Observable<Void> selectedFourthAvatar() {
    return RxView.clicks(fourthAvatar);
  }

  @Override public Observable<Void> selectedFifthAvatar() {
    return RxView.clicks(fifthAvatar);
  }

  @Override public Observable<Void> selectedSixthAvatar() {
    return RxView.clicks(sixthAvatar);
  }

  @Override public void selectedAvatar(int avatar) {
    selectedAvatar = avatar;
    deselectAllAvatars();
    defaultAvatarList.get(avatar)
        .setImageDrawable(getResources().getDrawable(R.drawable.spotandshare_avatar_highlighter));
  }

  @Override public void setActualAvatar(Integer avatar) {
    switch (avatar) {
      case 0:
        actualAvatar.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.green)));
        break;
      case 1:
        actualAvatar.setImageDrawable(
            new ColorDrawable(getResources().getColor(R.color.aptoide_orange)));
        break;
      case 2:
        actualAvatar.setImageDrawable(
            new ColorDrawable(getResources().getColor(R.color.light_blue)));
        break;
      case 3:
        actualAvatar.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.amber)));
        break;
      case 4:
        actualAvatar.setImageDrawable(
            new ColorDrawable(getResources().getColor(R.color.grey_fog_normal)));
        break;
      case 5:
        actualAvatar.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.teal_700)));
        break;
      default:
        actualAvatar.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.green)));
        break;
    }
  }

  private void deselectAllAvatars() {
    for (int i = 0; i < defaultAvatarList.size(); i++) {
      if (defaultAvatarList.get(i)
          .getDrawable() != null) {
        defaultAvatarList.get(i)
            .setImageDrawable(null);
      }
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    firstAvatar = (ImageView) view.findViewById(R.id.first_option_avatar);
    secondAvatar = (ImageView) view.findViewById(R.id.second_option_avatar);
    thirdAvatar = (ImageView) view.findViewById(R.id.third_option_avatar);
    fourthAvatar = (ImageView) view.findViewById(R.id.fourth_option_avatar);
    fifthAvatar = (ImageView) view.findViewById(R.id.fifth_option_avatar);
    sixthAvatar = (ImageView) view.findViewById(R.id.sixth_option_avatar);
    actualAvatar = (ImageView) view.findViewById(R.id.actual_avatar);
    usernameEditText = (EditText) view.findViewById(R.id.username_edit_text);
    cancel = (Button) view.findViewById(R.id.cancel_profile_edition_button);
    saveProfile = (Button) view.findViewById(R.id.save_profile_edition_button);
    toolbar = (Toolbar) view.findViewById(R.id.spotandshare_toolbar);

    setupToolbar();

    buildImageViewList();

    SpotAndShareUserPersister persister = new SpotAndShareUserPersister(
        getContext().getSharedPreferences(SpotAndShareUserPersister.SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE));
    attachPresenter(
        new SpotAndShareEditProfilePresenter(this, new SpotAndShareUserManager(persister)),
        savedInstanceState);
  }

  @Override public void onDestroyView() {
    firstAvatar = null;
    secondAvatar = null;
    thirdAvatar = null;
    fourthAvatar = null;
    fifthAvatar = null;
    sixthAvatar = null;
    usernameEditText = null;
    cancel = null;
    saveProfile = null;
    toolbar = null;
    super.onDestroyView();
  }

  private void buildImageViewList() {
    defaultAvatarList = new ArrayList<>();
    defaultAvatarList.add(firstAvatar);
    defaultAvatarList.add(secondAvatar);
    defaultAvatarList.add(thirdAvatar);
    defaultAvatarList.add(fourthAvatar);
    defaultAvatarList.add(fifthAvatar);
    defaultAvatarList.add(sixthAvatar);
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
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.fragment_spotandshare_edit_profile, container, false);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  private SpotAndShareUserAvatar getAvatar() {
    return new SpotAndShareUserAvatar(selectedAvatar);
  }

  private String getUsername() {
    return usernameEditText.getText()
        .toString();
  }
}
