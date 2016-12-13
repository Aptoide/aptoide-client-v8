package cm.aptoide.accountmanager;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.ws.CheckUserCredentialsRequest;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.SetStoreRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import com.jakewharton.rxbinding.view.RxView;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by pedroribeiro on 29/11/16.
 */

public class CreateStoreActivity extends PermissionsBaseActivity implements
    AptoideAccountManager.ICreateStore{

  private Toolbar mToolbar;
  private Button mCreateStore;
  private Button mSkip;
  private RelativeLayout mStoreAvatarLayout;
  private ImageView mStoreAvatar;
  private TextView mTakePictureText;
  private TextView mHeader;
  private TextView mChooseNameTitle;
  private EditText mStoreName;
  private CompositeSubscription mSubscriptions;

  private String CREATE_STORE_CODE = "1";
  private String storeName;
  private String username;
  private String password;
  private String storeAvatarPath;
  private String storeTheme;
  private String aptoideStoreAvatar = "aptoide_user_store_avatar.png";

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    mSubscriptions = new CompositeSubscription();
    bindViews();
    editViews();
    setupToolbar();
    setupListeners();
    getUserData();
  }

  @Override protected String getActivityTitle() {
    return getString(R.string.create_store_title);
  }

  @Override int getLayoutId() {
    return R.layout.fragment_create_store;
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
    mCreateStore = (Button) findViewById(R.id.create_store_action);
    mSkip = (Button) findViewById(R.id.create_store_skip);
    mStoreAvatarLayout = (RelativeLayout) findViewById(R.id.create_store_image_action);
    mTakePictureText = (TextView) findViewById(R.id.create_store_take_picture_text);
    mStoreName = (EditText) findViewById(R.id.create_store_name);
    mHeader = (TextView) findViewById(R.id.create_store_header_textview);
    mChooseNameTitle = (TextView) findViewById(R.id.create_store_choose_name_title);
    mStoreAvatar = (ImageView) findViewById(R.id.create_store_image);
  }

  private void editViews() {
    mHeader.setText(AptoideUtils.StringU.getFormattedString(R.string.create_store_header, "Aptoide"));
    mChooseNameTitle.setText(AptoideUtils.StringU.getFormattedString(R.string.create_store_name, "Aptoide"));
  }

  private void setupListeners() {
    mSubscriptions.add(RxView.clicks(mStoreAvatarLayout).subscribe(click -> chooseAvatarSource()));
    mSubscriptions.add(RxView.clicks(mCreateStore).subscribe(click -> {
      storeName = mStoreName.getText().toString();
      //TODO: Make request to create repo and to update it (checkusercredentials and setStore) and add dialog
      ProgressDialog progressDialog = GenericDialogs.createGenericPleaseWaitDialog(this, getApplicationContext().getString(R.string.please_wait_upload));
      progressDialog.show();
      CheckUserCredentialsRequest.of(
          getIntent().getStringExtra(AptoideLoginUtils.APTOIDE_LOGIN_ACCESS_TOKEN_KEY),
          storeName, CREATE_STORE_CODE, username, password).execute(answer -> {
          if (answer.hasErrors()) {
            if (answer.getErrors() != null && answer.getErrors().size() > 0) {
              onCreateFail();
              progressDialog.dismiss();
            }
          } else {
            onCreateSuccess(progressDialog);
          }
      });
    }));
    mSubscriptions.add(RxView.clicks(mSkip).subscribe(click -> {
      AptoideAccountManager.refreshAndSaveUserInfoData();
      //TODO: Broadcast igual ao do signup
      finish();
    }));
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Uri avatarUrl = getPhotoFileUri(PermissionsBaseActivity.createAvatarPhotoName(photoAvatar));
      ImageLoader.loadWithCircleTransform(avatarUrl, mStoreAvatar);
      storeAvatarPath = avatarUrl.toString();
    } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK) {
      Uri avatarUrl = data.getData();
      ImageLoader.loadWithCircleTransform(avatarUrl, mStoreAvatar);
      FileUtils fileUtils = new FileUtils();
//      fileUtils.copyFile(avatarUrl.toString(), Application.getConfiguration().getUserAvatarCachePath(), aptoideStoreAvatar);
      storeAvatarPath = avatarUrl.toString();
    }
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
    }
  }

  private void getUserData() {
    username = getIntent().getStringExtra(AptoideLoginUtils.APTOIDE_LOGIN_USER_NAME_KEY);
    password = getIntent().getStringExtra(AptoideLoginUtils.APTOIDE_LOGIN_PASSWORD_KEY);
  }

  @Override public void onCreateSuccess(ProgressDialog progressDialog) {
    ShowMessage.asSnack(this, "Repo Created");
    SetStoreRequest.of(
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext()).getAptoideClientUUID(),
        AptoideAccountManager.getAccessToken(), storeName, "red", storeAvatarPath)
        .execute(answer -> {
          if (answer.getErrors().size() > 0) {
            //TODO: deal with success
            ShowMessage.asSnack(this, "failed");
            AptoideAccountManager.refreshAndSaveUserInfoData();
            progressDialog.dismiss();
          } else {
            //TODO: deal with failure
            ShowMessage.asSnack(this, "success");
            AptoideAccountManager.refreshAndSaveUserInfoData();
            progressDialog.dismiss();
            //TODO: Broadcast igual ao do signup
          }
        });
  }

  @Override public void onCreateFail() {
    ShowMessage.asSnack(this, "Failed");
  }

  @Override public String getRepoName() {
    return storeName == null ? "" : mStoreName.getText().toString();
  }

  @Override public String getRepoTheme() {
    return storeTheme == null ? "" : storeTheme;
  }

  @Override public String getRepoAvatar() {
    return storeAvatarPath == null ? "" : storeAvatarPath;
  }
}
