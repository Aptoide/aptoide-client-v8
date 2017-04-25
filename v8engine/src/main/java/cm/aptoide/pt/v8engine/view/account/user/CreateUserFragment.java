package cm.aptoide.pt.v8engine.view.account.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import cm.aptoide.pt.v8engine.view.ThrowableToStringMapper;
import cm.aptoide.pt.v8engine.view.account.AccountErrorMapper;
import com.jakewharton.rxbinding.view.RxView;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import rx.Completable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

// TODO
// refactor (remove) more code
//     - avoid using a base class for permissions
//     - move some code to PermissionServiceFragment and the remainder for this class or other entity
// chain Rx in method calls
// apply MVP
// save / restore data in input fields
public class CreateUserFragment extends AccountPermissionsBaseFragment {

  private static final String TAG = CreateUserFragment.class.getName();

  private ThrowableToStringMapper errorMapper;

  private String avatarPath;
  private RelativeLayout userAvatar;
  private EditText nameEditText;
  private Button createUserButton;
  private ImageView avatarImage;
  private AptoideAccountManager accountManager;
  private ProgressDialog uploadWaitDialog;
  private ProgressDialog waitDialog;

  public CreateUserFragment() {
    super(true, false);
  }

  public static CreateUserFragment newInstance() {
    return new CreateUserFragment();
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

  @Override public void onDestroy() {
    super.onDestroy();

    if (waitDialog != null && waitDialog.isShowing()) {
      waitDialog.dismiss();
    }

    if (uploadWaitDialog != null && uploadWaitDialog.isShowing()) {
      uploadWaitDialog.dismiss();
    }
  }

  @Override public int getContentViewId() {
    return R.layout.fragment_create_user;
  }

  public void loadImage(Uri imagePath) {
    ImageLoader.with(getActivity()).loadWithCircleTransform(imagePath, avatarImage, false);
  }

  @Override public void showIconPropertiesError(String errors) {
    GenericDialogs.createGenericOkMessage(getActivity(),
        getString(R.string.image_requirements_error_popup_title), errors)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe();
  }

  @Override public void setupViews() {
    super.setupViews();

    RxView.clicks(userAvatar)
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(__ -> chooseAvatarSource());

    final Completable dismissProgressDialogCompletable =
        Completable.fromAction(() -> dismissProgressDialog());

    final Completable sendAnalytics = Completable.fromAction(
        () -> Analytics.Account.createdUserProfile(!TextUtils.isEmpty(avatarPath)));

    RxView.clicks(createUserButton)
        .doOnNext(__ -> hideKeyboardAndShowProgressDialog())
        .flatMap(
            __ -> accountManager.updateAccount(nameEditText.getText().toString().trim(), avatarPath)
                .timeout(90, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(Completable.merge(dismissProgressDialogCompletable, sendAnalytics))
                .andThen(showSuccessMessageAndNavigateToLoggedInView())
                .onErrorResumeNext(err -> {
                  CrashReport.getInstance().log(err);
                  return dismissProgressDialogCompletable.andThen(showError(err));
                })
                .toObservable())
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .retry()
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance().log(err));
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override protected void setupToolbarDetails(Toolbar toolbar) {
    super.setupToolbarDetails(toolbar);
    toolbar.setTitle(R.string.create_user_title);
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    userAvatar = (RelativeLayout) view.findViewById(R.id.create_user_image_action);
    nameEditText = (EditText) view.findViewById(R.id.create_user_username_inserted);
    createUserButton = (Button) view.findViewById(R.id.create_user_create_profile);
    avatarImage = (ImageView) view.findViewById(R.id.create_user_image);
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
    return ShowMessage.asObservableSnack(createUserButton, message).toCompletable();
  }

  private void dismissProgressDialog() {
    if (isAvatarSelected()) {
      uploadWaitDialog.dismiss();
    } else {
      waitDialog.dismiss();
    }
  }

  private Completable showSuccessMessageAndNavigateToLoggedInView() {
    final boolean showPrivacyConfigs =
        Application.getConfiguration().isCreateStoreAndSetUserPrivacyAvailable();

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
    getFragmentNavigator().navigateTo(ProfileStepOneFragment.newInstance());
  }

  public void navigateToHome() {
    getFragmentNavigator().navigateToHomeCleaningBackStack();
  }

  private boolean isAvatarSelected() {
    return !TextUtils.isEmpty(avatarPath);
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
      avatarPath = new FileUtils().getMediaStoragePath(avatarUrl, applicationContext);
      checkAvatarRequirements(avatarPath, avatarUrl);
    } else {
      Logger.w(TAG, "URI for content is null or empty");
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case STORAGE_REQUEST_CODE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          setUserHasGivenPermission(true);
          dispatchOpenGalleryIntent();
        } else {
          //TODO: Deal with permissions not being given by user
        }
        return;

      case CAMERA_REQUEST_CODE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          setUserHasGivenPermission(true);
          dispatchTakePictureIntent(getActivity().getApplicationContext());
        } else {
          //TODO: Deal with permissions not being given by user
        }
        break;
    }
  }
}
