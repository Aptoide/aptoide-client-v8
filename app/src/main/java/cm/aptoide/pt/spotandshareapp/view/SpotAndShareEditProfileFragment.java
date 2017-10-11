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
import android.widget.Button;
import android.widget.EditText;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.AptoideNavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalAvatarsProvider;
import cm.aptoide.pt.spotandshareapp.SpotAndShareLocalUser;
import cm.aptoide.pt.spotandshareapp.presenter.SpotAndShareEditProfilePresenter;
import cm.aptoide.pt.view.fragment.FragmentView;
import com.jakewharton.rxbinding.view.RxView;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by filipe on 21-06-2017.
 */

public class SpotAndShareEditProfileFragment extends FragmentView
    implements SpotAndShareEditProfileView {

  private EditText usernameEditText;
  private Button saveProfile;
  private Toolbar toolbar;
  private RecyclerView avatarsRecyclerView;
  private SpotAndShareEditProfileAdapter pickAvatarAdapter;
  private PublishSubject<SpotAndShareAvatar> pickAvatarSubject;
  private AptoideNavigationTracker aptoideNavigationTracker;

  public static Fragment newInstance() {
    Fragment fragment = new SpotAndShareEditProfileFragment();
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    pickAvatarSubject = PublishSubject.create();
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

  @Override public Observable<SpotAndShareLocalUser> saveProfileChanges() {
    return RxView.clicks(saveProfile)
        .map(aVoid -> new SpotAndShareLocalUser(getUsername(),
            pickAvatarAdapter.getSelectedAvatar()));
  }

  @Override public Observable<SpotAndShareAvatar> onSelectedAvatar() {
    return pickAvatarAdapter.onSelectedAvatar();
  }

  @Override public void goBack() {
    getFragmentNavigator().popBackStack();
  }

  @Override public void selectAvatar(SpotAndShareAvatar avatar) {
    pickAvatarAdapter.selectAvatar(avatar);
  }

  @Override public void setAvatarsList(List<SpotAndShareAvatar> list) {
    pickAvatarAdapter.setAvatarList(list);
    pickAvatarAdapter.notifyDataSetChanged();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    usernameEditText = (EditText) view.findViewById(R.id.username_edit_text);
    saveProfile = (Button) view.findViewById(R.id.save_profile_edition_button);
    toolbar = (Toolbar) view.findViewById(R.id.spotandshare_toolbar);

    setupToolbar();

    avatarsRecyclerView = (RecyclerView) view.findViewById(R.id.pick_avatar_recyclerView);
    pickAvatarAdapter = new SpotAndShareEditProfileAdapter(getContext(), pickAvatarSubject);
    avatarsRecyclerView.setAdapter(pickAvatarAdapter);
    setupAvatarsListLayoutManager();
    avatarsRecyclerView.setHasFixedSize(true);

    attachPresenter(new SpotAndShareEditProfilePresenter(this,
        ((AptoideApplication) getActivity().getApplicationContext()).getSpotAndShareUserManager(),
        new SpotAndShareLocalAvatarsProvider(getContext().getPackageName()),
        CrashReport.getInstance()), savedInstanceState);
  }

  private void setupAvatarsListLayoutManager() {
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 4);
    avatarsRecyclerView.setLayoutManager(gridLayoutManager);
  }

  @Override public void onDestroyView() {
    usernameEditText = null;
    saveProfile = null;
    toolbar = null;

    pickAvatarAdapter.removeAll();
    pickAvatarAdapter = null;
    avatarsRecyclerView = null;
    super.onDestroyView();
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

  private String getUsername() {
    return usernameEditText.getText()
        .toString();
  }
}
