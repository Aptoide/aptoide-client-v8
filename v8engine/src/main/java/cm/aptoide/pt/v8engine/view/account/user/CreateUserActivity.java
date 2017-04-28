/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.view.account.user;

import android.app.Activity;

/**
 * Created by pedroribeiro on 24/11/16.
 */

public class CreateUserActivity extends Activity {
  /*

  private static int CREATE_USER_REQUEST_CODE = 0; //1:Username and Avatar 2: Username
  private ThrowableToStringMapper errorMapper;

  private String avatarPath;
  private Toolbar toolbar;
  private RelativeLayout userAvatar;
  private EditText nameEditText;
  private Button createUserButton;
  private ImageView avatarImage;
  private View content;
  private CompositeSubscription subscriptions;
  private AptoideAccountManager accountManager;
  private ProgressDialog progressAvatarUploadDialog;
  private ProgressDialog progressDialog;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_user);
    errorMapper = new CreateUserErrorMapper(this, new AccountErrorMapper(this));
    accountManager = ((V8Engine) getApplicationContext()).getAccountManager();
    subscriptions = new CompositeSubscription();
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    userAvatar = (RelativeLayout) findViewById(R.id.create_user_image_action);
    nameEditText = (EditText) findViewById(R.id.create_user_username_inserted);
    createUserButton = (Button) findViewById(R.id.create_user_create_profile);
    avatarImage = (ImageView) findViewById(R.id.create_user_image);
    content = findViewById(android.R.id.content);
    progressAvatarUploadDialog = GenericDialogs.createGenericPleaseWaitDialog(this,
        getApplicationContext().getString(R.string.please_wait_upload));
    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(this,
        getApplicationContext().getString(R.string.please_wait));
    setSupportActionBar(toolbar);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle(R.string.create_user_title);
    setupListeners();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    subscriptions.clear();
  }

  @Override public void loadImage(Uri imagePath) {
    ImageLoader.with(this).loadWithCircleTransform(imagePath, avatarImage, false);
  }

  @Override public void showIconPropertiesError(String errors) {
    subscriptions.add(GenericDialogs.createGenericOkMessage(this,
        getString(R.string.image_requirements_error_popup_title), errors)
        .subscribe());
  }

  private void setupListeners() {
    subscriptions.add(RxView.clicks(userAvatar).subscribe(click -> chooseAvatarSource()));
    subscriptions.add(RxView.clicks(createUserButton).doOnNext(click -> {
      hideKeyboardAndShowProgressDialog();
      Analytics.Account.createdUserProfile(!TextUtils.isEmpty(avatarPath));
    })
        .flatMap(click -> accountManager.updateAccount(nameEditText.getText().toString().trim(),
            avatarPath)
            .toObservable()
            .timeout(90, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(throwable -> showError(throwable))
            .doOnTerminate(() -> dismissProgressDialog())
            .doOnCompleted(() -> showSuccessMessageAndNavigateToLoggedInView()))
        .retry()
        .subscribe());
  }

  private void hideKeyboardAndShowProgressDialog() {
    AptoideUtils.SystemU.hideKeyboard(this);
    if (isAvatarSelected()) {
      progressAvatarUploadDialog.show();
    } else {
      progressDialog.show();
    }
  }

  private void showError(Throwable throwable) {
    final String message = errorMapper.map(throwable);

    if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
      RxSnackbar.dismisses(Snackbar.make(content, message, Snackbar.LENGTH_SHORT))
          .subscribe(dismissed -> finish());
      return;
    }

    ShowMessage.asSnack(content, errorMapper.map(throwable));
  }

  private void dismissProgressDialog() {
    if (isAvatarSelected()) {
      progressAvatarUploadDialog.dismiss();
    } else {
      progressDialog.dismiss();
    }
  }

  private void showSuccessMessageAndNavigateToLoggedInView() {
    ShowMessage.asSnack(content, R.string.user_created);
    if (Application.getConfiguration().isCreateStoreAndSetUserPrivacyAvailable()) {
      startActivity(new Intent(this, ProfileStepOneActivity.class));
    } else {
      Toast.makeText(this, R.string.create_profile_pub_pri_suc_login,
          Toast.LENGTH_LONG).show();
    }
    finish();
  }

  private boolean isAvatarSelected() {
    return !TextUtils.isEmpty(avatarPath);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    FileUtils fileUtils = new FileUtils();
    Uri avatarUrl = null;
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      avatarUrl = getPhotoFileUri(photoAvatar);
      avatarPath = fileUtils.getPath(avatarUrl, getApplicationContext());
    } else if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
      avatarUrl = data.getData();
      avatarPath = fileUtils.getPath(avatarUrl, getApplicationContext());
    }
    checkAvatarRequirements(avatarPath, avatarUrl);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case STORAGE_REQUEST_CODE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          changePermissionValue(true);
          callGallery();
        } else {
          //TODO: Deal with permissions not being given by user
        }
        return;
      case CAMERA_REQUEST_CODE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          changePermissionValue(true);
          dispatchTakePictureIntent(getApplicationContext());
        } else {
          //TODO: Deal with permissions not being given by user
        }
        break;
    }
  }
  */
}
