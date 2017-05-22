package cm.aptoide.pt.v8engine.view.account.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.presenter.CreateUserView;
import cm.aptoide.pt.v8engine.view.ThrowableToStringMapper;
import cm.aptoide.pt.v8engine.view.account.AccountErrorMapper;
import cm.aptoide.pt.v8engine.view.account.PictureLoaderFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import com.jakewharton.rxbinding.view.RxView;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

// TODO
// create presenter and separate logic code from view
public class CreateUserFragment extends PictureLoaderFragment implements CreateUserView {

  public static final String FROM_MY_ACCOUNT = "My Account";
  private static final String TAG = CreateUserFragment.class.getName();
  private static final String USER_IMAGE_PATH = "user_image_path";
  private ThrowableToStringMapper errorMapper;
  private String userPicturePath;
  private String userNickname;
  private ImageView userPicture;
  private RelativeLayout userPictureLayout;
  private EditText userName;
  private Button createUserButton;
  private AptoideAccountManager accountManager;
  private ProgressDialog uploadWaitDialog;
  private ProgressDialog waitDialog;
  private String from;
  private Button cancelUserProfile;

  public CreateUserFragment() {
    super(true, false);
  }

  public static CreateUserFragment newInstance() {
    return new CreateUserFragment();
  }

  public static CreateUserFragment newInstance(String userPicturePath, String userName,
      String from) {
    CreateUserFragment createUserFragment = newInstance();
    Bundle args = new Bundle();
    args.putString("userName", userName);
    args.putString("userAvatar", userPicturePath);
    args.putString("from", from);
    createUserFragment.setArguments(args);
    return createUserFragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final Context context = getActivity();

    errorMapper = new CreateUserErrorMapper(context, new AccountErrorMapper(context));

    final Context applicationContext = context.getApplicationContext();
    accountManager = ((V8Engine) applicationContext).getAccountManager();

    uploadWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(context,
        applicationContext.getString(R.string.please_wait_upload));

    waitDialog = GenericDialogs.createGenericPleaseWaitDialog(context,
        applicationContext.getString(R.string.please_wait));
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (savedInstanceState != null && savedInstanceState.containsKey(USER_IMAGE_PATH)) {
      String uri = savedInstanceState.getString(USER_IMAGE_PATH);
      if (!TextUtils.isEmpty(uri)) {
        loadImage(Uri.parse(uri));
      }
    }
    setupViewsDefaultValues(view);
  }

  @Override public void onDestroy() {
    super.onDestroy();

    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }

    if (uploadWaitDialog != null && uploadWaitDialog.isShowing()) {
      uploadWaitDialog.dismiss();
    }
  }

  @Override public void loadExtras(Bundle args) {
    super.loadExtras(args);
    userPicturePath = args.getString("userAvatar");
    userNickname = args.getString("userName");
    from = args.getString("from");
  }

  private void setupViewsDefaultValues(View view) {
    if (isEditProfile()) {
      createUserButton.setText(getString(R.string.edit_profile_edit_button));
      getToolbar().setTitle(getString(R.string.edit_profile_title));
      if (userPicturePath != null) {
        userPicturePath = userPicturePath.replace("50", "150");
        loadImage(Uri.parse(userPicturePath));
      }
      if (userNickname != null) {
        userName.setText(userNickname);
      }
      cancelUserProfile.setVisibility(View.VISIBLE);
      cancelUserProfile.setText(getString(R.string.cancel));
    }
  }

  private boolean isEditProfile() {
    return from != null;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(USER_IMAGE_PATH, userPicturePath);
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_create_user;
  }

  public void loadImage(Uri imagePath) {
    ImageLoader.with(getActivity())
        .loadWithCircleTransform(imagePath, userPicture, false);
  }

  @Override public void showIconPropertiesError(String errors) {
    GenericDialogs.createGenericOkMessage(getActivity(),
        getString(R.string.image_requirements_error_popup_title), errors)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void setupViews() {
    super.setupViews();

    selectUserImageClick().compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(__ -> chooseAvatarSource());

    final Completable dismissProgressDialogCompletable =
        Completable.fromAction(() -> dismissProgressDialog());

    final Completable sendAnalytics = Completable.fromAction(
        () -> Analytics.Account.createdUserProfile(!TextUtils.isEmpty(userPicturePath)));

    createUserButtonClick().doOnNext(__ -> {
      hideKeyboardAndShowProgressDialog();
      validateUserAvatar();
    })
        .flatMap(__ -> accountManager.updateAccount(userName.getText()
            .toString()
            .trim(), userPicturePath)
            .timeout(90, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .andThen(Completable.merge(dismissProgressDialogCompletable, sendAnalytics))
            .andThen(showLoggedInOrMyAccount())
            .onErrorResumeNext(err -> {
              CrashReport.getInstance()
                  .log(err);
              return dismissProgressDialogCompletable.andThen(showError(err));
            })
            .toObservable())
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .retry()
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));

    cancelButtonClick().doOnNext(__ -> navigateToMyAccount())
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(R.string.create_user_title);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    userPictureLayout = (RelativeLayout) view.findViewById(R.id.create_user_image_action);
    userName = (EditText) view.findViewById(R.id.create_user_username_inserted);
    createUserButton = (Button) view.findViewById(R.id.create_user_create_profile);
    cancelUserProfile = (Button) view.findViewById(R.id.create_user_cancel_button);
    userPicture = (ImageView) view.findViewById(R.id.create_user_image);
  }

  private void navigateToMyAccount() {
    getFragmentNavigator().popBackStack();
  }

  private void validateUserAvatar() {
    if (userPicturePath != null) {
      userPicturePath = userPicturePath.contains("http") ? "" : userPicturePath;
    }
  }

  @Override public Observable<Void> createUserButtonClick() {
    return RxView.clicks(createUserButton);
  }

  @Override public Observable<Void> selectUserImageClick() {
    return RxView.clicks(userPictureLayout);
  }

  @Override public Observable<Void> cancelButtonClick() {
    return RxView.clicks(cancelUserProfile);
  }

  private void hideKeyboardAndShowProgressDialog() {
    AptoideUtils.SystemU.hideKeyboard(getActivity());

    if (isAvatarSelected()) {
      uploadWaitDialog.show();
      return;
    }

    waitDialog.show();
  }

  private Completable showError(Throwable throwable) {
    final String message = errorMapper.map(throwable);

    if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
      // go home
      return ShowMessage.asObservableSnack(createUserButton, message)
          .toCompletable()
          .doOnCompleted(() -> navigateToHome());
    }

    // don't go home
    return ShowMessage.asObservableSnack(createUserButton, message)
        .toCompletable();
  }

  private void dismissProgressDialog() {
    if (isAvatarSelected()) {
      uploadWaitDialog.dismiss();
    } else {
      waitDialog.dismiss();
    }
  }

  public Completable showLoggedInOrMyAccount() {
    if (from != null) {
      Single<Integer> showUserEdit =
          ShowMessage.asObservableSnack(createUserButton, R.string.user_edited)
              .filter(vis -> vis == ShowMessage.DISMISSED)
              .first()
              .toSingle();
      return showUserEdit.flatMapCompletable(__ -> navigateToMyAccountCompletable());
    } else {
      return showSuccessMessageAndNavigateToLoggedInView();
    }
  }

  private Completable showSuccessMessageAndNavigateToLoggedInView() {
    final boolean showPrivacyConfigs = Application.getConfiguration()
        .isCreateStoreAndSetUserPrivacyAvailable();

    Single<Integer> showUserCreated =
        ShowMessage.asObservableSnack(createUserButton, R.string.user_created)
            .filter(vis -> vis == ShowMessage.DISMISSED)
            .first()
            .toSingle();

    if (showPrivacyConfigs) {
      return showUserCreated.flatMapCompletable(__ -> navigateToProfileStepOneCompletable());
    }

    Single<Integer> showCreatePublicPrivateProfile =
        ShowMessage.asObservableSnack(this, R.string.create_profile_pub_pri_suc_login)
            .filter(vis -> vis == ShowMessage.DISMISSED)
            .first()
            .toSingle();

    return showUserCreated.flatMap(__ -> showCreatePublicPrivateProfile)
        .flatMapCompletable(__ -> navigateToHomeCompletable());
  }

  private Completable navigateToHomeCompletable() {
    return Completable.fromAction(() -> navigateToHome());
  }

  private Completable navigateToProfileStepOneCompletable() {
    return Completable.fromAction(() -> navigateToProfileStepOne());
  }

  private void navigateToProfileStepOne() {
    final FragmentNavigator fragmentNavigator = getFragmentNavigator();
    fragmentNavigator.cleanBackStack();
    fragmentNavigator.navigateTo(ProfileStepOneFragment.newInstance());
  }

  private Completable navigateToMyAccountCompletable() {
    return Completable.fromAction(() -> navigateToMyAccount());
  }

  public void navigateToHome() {
    getFragmentNavigator().navigateToHomeCleaningBackStack();
  }

  private boolean isAvatarSelected() {
    return !TextUtils.isEmpty(userPicturePath);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Uri avatarUrl = null;
    final Context applicationContext = getActivity().getApplicationContext();

    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
      avatarUrl = getFileUriFromFileName(photoFileName);
    }

    if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK && data != null) {
      avatarUrl = data.getData();
    }

    if (avatarUrl != null && !TextUtils.isEmpty(avatarUrl.toString())) {
      userPicturePath = new FileUtils().getMediaStoragePath(avatarUrl, applicationContext);
      checkAvatarRequirements(userPicturePath, avatarUrl);
    } else {
      Logger.w(TAG, "URI for content is null or empty");
    }
  }
}
