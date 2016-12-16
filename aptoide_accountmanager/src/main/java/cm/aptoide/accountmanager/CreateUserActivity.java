package cm.aptoide.accountmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cm.aptoide.accountmanager.ws.CreateUserRequest;
import cm.aptoide.accountmanager.ws.ErrorsMapper;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import com.jakewharton.rxbinding.view.RxView;
import java.util.ArrayList;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 24/11/16.
 */

public class CreateUserActivity extends PermissionsBaseActivity implements AptoideAccountManager.ICreateProfile {

  private String userEmail;
  private String userPassword;
  private String username;
  private String avatarPath;
  private String accessToken;
  private Boolean UPDATE = true;
  private String SIGNUP = "signup";

  private Toolbar mToolbar;
  private RelativeLayout mUserAvatar;
  private EditText mUsername;
  private Button mCreateButton;
  private ImageView mAvatar;
  private View content;

  private static int CREATE_USER_REQUEST_CODE = 0; //1:Username and Avatar 2: Username

  private CompositeSubscription mSubscriptions;

  private Boolean result = false;

  private String ERROR_TAG = "Error update user";

  private static final String TYPE_STORAGE = "storage";
  private static final String TYPE_CAMERA = "camera";

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    mSubscriptions = new CompositeSubscription();
    bindViews();
    getUserData();
    setupToolbar();
    setupListeners();
  }

  @Override protected String getActivityTitle() {
    return getString(R.string.create_user_title);
  }

  @Override int getLayoutId() {
    return R.layout.activity_create_user;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mSubscriptions.clear();
  }

  private void setupToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(getActivityTitle());
    }
  }

  private void bindViews() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mUserAvatar = (RelativeLayout) findViewById(R.id.create_user_image_action);
    mUsername = (EditText) findViewById(R.id.create_user_username_inserted);
    mCreateButton = (Button) findViewById(R.id.create_user_create_profile);
    mAvatar = (ImageView) findViewById(R.id.create_user_image);
    content = findViewById(android.R.id.content);
  }

  private void setupListeners() {
    mSubscriptions.add(RxView.clicks(mUserAvatar).subscribe(click -> chooseAvatarSource()));
    mSubscriptions.add(RxView.clicks(mCreateButton).subscribe(click -> {
      username = mUsername.getText().toString();
      validateProfileData();
      if (CREATE_USER_REQUEST_CODE == 1) {
        ProgressDialog pleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(this, getApplicationContext().getString(R.string.please_wait_upload));
        pleaseWaitDialog.show();
        CreateUserRequest.of("true", userEmail, username, userPassword, avatarPath).execute(answer -> {
          if (answer.hasErrors()) {
            if (answer.getErrors() != null && answer.getErrors().size() > 0) {
              onRegisterFail(ErrorsMapper.getWebServiceErrorMessageFromCode(answer.getErrors().get(0).code));
              pleaseWaitDialog.dismiss();
            } else {
              onRegisterFail(R.string.unknown_error);
              pleaseWaitDialog.dismiss();
            }
          } else {
            //Successfull update
            saveUserDataOnPreferences();
            onRegisterSuccess(pleaseWaitDialog);
            pleaseWaitDialog.dismiss();
          }
        });
      } else if (CREATE_USER_REQUEST_CODE == 2) {
        avatarPath = "";
        ProgressDialog pleaseWaitDialog = GenericDialogs.createGenericPleaseWaitDialog(this, getApplicationContext().getString(R.string.please_wait));
        pleaseWaitDialog.show();
        CreateUserRequest.of("true", userEmail, username, userPassword, avatarPath).execute(answer -> {
          if (answer.hasErrors()) {
            if (answer.getErrors() != null && answer.getErrors().size() > 0) {
              onRegisterFail(ErrorsMapper.getWebServiceErrorMessageFromCode(answer.getErrors().get(0).code));
              pleaseWaitDialog.dismiss();
            } else {
              onRegisterFail(R.string.unknown_error);
              pleaseWaitDialog.dismiss();
            }
          } else {
            //Successfull update
            saveUserDataOnPreferences();
            onRegisterSuccess(pleaseWaitDialog);
            pleaseWaitDialog.dismiss();
          }
        });
      }
    }));
  }

  private void getUserData() {
    userEmail = getIntent().getStringExtra(AptoideLoginUtils.APTOIDE_LOGIN_USER_NAME_KEY);
    userPassword = getIntent().getStringExtra(AptoideLoginUtils.APTOIDE_LOGIN_PASSWORD_KEY);
    accessToken = getIntent().getStringExtra(AptoideLoginUtils.APTOIDE_LOGIN_ACCESS_TOKEN_KEY);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      FileUtils fileUtils = new FileUtils();
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Uri avatarUrl = getPhotoFileUri(PermissionsBaseActivity.createAvatarPhotoName(photoAvatar));
      ImageLoader.loadWithCircleTransform(avatarUrl, mAvatar);
      avatarPath = avatarUrl.toString();
    } else if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
        Uri avatarUrl = data.getData();
        avatarPath = fileUtils.getPath(avatarUrl, getApplicationContext());
        if (checkImageResolution(avatarPath)) {
          ImageLoader.loadWithCircleTransform(avatarUrl, mAvatar);
        } else {
          ShowMessage.asSnack(this, R.string.create_user_bad_photo);
          avatarPath = "";
        }
    } else if (requestCode == CREATE_STORE_REQUEST_CODE) {
      finish();
    }
  }

  private boolean checkImageResolution(String avatarPath) {
    ArrayList<Integer> resolution;
    FileUtils fileUtils = new FileUtils();
    resolution = fileUtils.getImageResolution(avatarPath);
    if (resolution.get(0) > 300 && resolution.get(1) > 300) {
      return true;
    }
    return false;
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
      case USER_PROFILE_CODE:
        Intent intent = new Intent(this, CreateStoreActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("user", username);
        intent.putExtra(AptoideLoginUtils.APTOIDE_LOGIN_ACCESS_TOKEN_KEY, accessToken);
        intent.putExtra(AptoideLoginUtils.APTOIDE_LOGIN_USER_NAME_KEY, userEmail);
        intent.putExtra(AptoideLoginUtils.APTOIDE_LOGIN_PASSWORD_KEY, userPassword);
        startActivityForResult(intent, CREATE_STORE_REQUEST_CODE);

    }
  }


  private void saveUserDataOnPreferences() {
    AccountManagerPreferences.setUserAvatar(avatarPath);
    AccountManagerPreferences.setUserNickName(username);
  }

  @Override public void onRegisterSuccess(ProgressDialog progressDialog) {
    ShowMessage.asSnack(content, R.string.user_created);
    //data.putString(AptoideLoginUtils.APTOIDE_LOGIN_FROM, SIGNUP);
    progressDialog.dismiss();
    Intent intent = new Intent(this, LoggedInActivity.class);
    startActivityForResult(intent, USER_PROFILE_CODE);
  }

  @Override public void onRegisterFail(@StringRes int reason) {
    ShowMessage.asSnack(content, reason);
  }

  @Override public String getUserUsername() {
    return mUsername == null ? "" : mUsername.getText().toString();
  }

  @Override public String getUserAvatar() {
    return avatarPath == null ? "" : avatarPath;
  }

  public int validateProfileData() {
    if (getUserUsername().length() != 0) {
      if (getUserAvatar().length() != 0) {
        CREATE_USER_REQUEST_CODE = 1;
      } else if (getUserAvatar().length() == 0) {
        CREATE_USER_REQUEST_CODE = 2;
      }
    } else {
      CREATE_USER_REQUEST_CODE = 0;
      onRegisterFail(R.string.nothing_inserted);
    }
    return CREATE_USER_REQUEST_CODE;
  }



}