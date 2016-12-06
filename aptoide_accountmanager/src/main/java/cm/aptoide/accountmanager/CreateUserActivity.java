package cm.aptoide.accountmanager;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cm.aptoide.accountmanager.ws.CreateUserRequest;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.design.ShowMessage;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import java.io.File;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 24/11/16.
 */

public class CreateUserActivity extends PermissionsBaseActivity {

  private String userEmail;
  private String userPassword;
  private String username;
  private String avatarPath;
  private File avatar;
  private Boolean UPDATE = true;

  private Toolbar mToolbar;
  private RelativeLayout mUserAvatar;
  private EditText mUsername;
  private Button mCreateButton;
  private ImageView mAvatar;
  private CompositeSubscription mSubscriptions;

  static final int REQUEST_CAMERA_CODE = 1046;
  static final int REQUEST_IMAGE_CAPTURE = 1;

  private Boolean result = false;

  private String aptoideUserAvatar = "aptoide_user_avatar.png";

  private String TAG = "STORAGE";
  private String ERROR_TAG = "Error update user";

  private static final int STORAGE_REQUEST_CODE = 123;
  private static final int CAMERA_REQUEST_CODE = 124;
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
    return R.layout.fragment_create_user;
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
  }

  private void setupListeners() {
    mSubscriptions.add(RxTextView.textChanges(mUsername).subscribe(new Action1<CharSequence>() {
      @Override public void call(CharSequence input) {
        username += input.toString();
      }
    }));
    mSubscriptions.add(RxView.clicks(mUserAvatar).subscribe(click -> chooseAvatarSource()));
    mSubscriptions.add(RxView.clicks(mCreateButton).subscribe(click -> {
      /*CreateUserRequest.of("true", userEmail, username, userPassword, avatarPath).execute(answer -> {
        if (answer.hasErrors()) {
          ShowMessage.asSnack(this, "Error while creating user profile");
          Logger.d(ERROR_TAG, answer.getError_description());
        } else {
          //Successfull update
          saveUserDataOnPreferences();

          Intent intent = new Intent(this, CreateStoreActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.putExtra("user", username);
          startActivity(intent);

        }*/
      Intent intent = new Intent(this, CreateStoreActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra("user", username);
      startActivity(intent);
      //});
    }));
  }

  private void getUserData() {
    userEmail = getIntent().getStringExtra(AptoideLoginUtils.APTOIDE_LOGIN_USER_NAME_KEY);
    userPassword = getIntent().getStringExtra(AptoideLoginUtils.APTOIDE_LOGIN_PASSWORD_KEY);
  }


  private void chooseAvatarSource() {
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.dialog_choose_avatar_layout);
    dialog.setTitle(R.string.create_user_dialog_title);
    mSubscriptions.add(RxView.clicks(dialog.findViewById(R.id.button_camera)).subscribe(click -> {
      callPermissionAndAction(TYPE_CAMERA);
      dialog.dismiss();
    }));
    mSubscriptions.add(RxView.clicks(dialog.findViewById(R.id.button_gallery)).subscribe(click -> {
      callPermissionAndAction(TYPE_STORAGE);
      dialog.dismiss();
    }));
    mSubscriptions.add(RxView.clicks(dialog.findViewById(R.id.button_cancel))
        .subscribe(click -> dialog.dismiss())
    );
    dialog.show();
  }

  private void callGallery() {
    if (result) {
      Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      if (intent.resolveActivity(getPackageManager()) != null) {
        startActivityForResult(intent, REQUEST_CAMERA_CODE);
      }
    }
  }

  private void dispatchTakePictureIntent() {
    if (result) {
      Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(PermissionsBaseActivity.createAvatarPhotoName(aptoideUserAvatar)));
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
      }
    }
  }

  private Uri getPhotoFileUri(String fileName) {
      File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ".aptoide/user_avatar");
      if (!storageDir.exists() && !storageDir.mkdirs()) {
        Logger.d(TAG, "Failed to create directory");
      }
      avatar = storageDir;
      return Uri.fromFile(new File(storageDir.getPath() + File.separator + fileName));
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Uri avatarUrl = getPhotoFileUri(PermissionsBaseActivity.createAvatarPhotoName(aptoideUserAvatar));
      ImageLoader.loadWithCircleTransform(avatarUrl, mAvatar);
      avatarPath = avatarUrl.toString();
    } else if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK) {
        Uri avatarUrl = data.getData();
        ImageLoader.loadWithCircleTransform(avatarUrl, mAvatar);
        avatarPath = avatarUrl.toString();
    }
  }




  //public void checkAndAskPermission(final Context context, String type) {
  //
  //  if(Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.M) {
  //    /*if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && type.equals(TYPE_STORAGE)) {
  //      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
  //        mSubscriptions.add(GenericDialogs.createGenericContinueCancelMessage(context, "",
  //            "Permission for storage")
  //            .filter(answer -> (answer.equals(GenericDialogs.EResponse.YES)))
  //            .subscribe(answer -> {
  //              changePermissionValue(true);
  //            }));
  //        AndroidBasicDialog dialog = AndroidBasicDialog.build(context);
  //        dialog.setMessage("permission test")
  //            .setPositiveButton("allow", )
  //              ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 123);
  //      } else {
  //        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 123);
  //      }
  //    } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED & type.equals(TYPE_CAMERA)) {
  //        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
  //          mSubscriptions.add(GenericDialogs.createGenericContinueCancelMessage(context, "",
  //              "Permission for camera")
  //              .filter(answer -> (answer.equals(GenericDialogs.EResponse.YES)))
  //              .subscribe(answer -> {
  //                changePermissionValue(true);
  //                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, 123);
  //              }));
  //        } else {
  //          ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, 123);
  //        }
  //      }*/
  //    if (type.equals(TYPE_STORAGE)) {
  //      if (ContextCompat.checkSelfPermission(CreateUserActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
  //          != PackageManager.PERMISSION_GRANTED) {
  //
  //        // Should we show an explanation?
  //        if (ActivityCompat.shouldShowRequestPermissionRationale(CreateUserActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
  //
  //          ActivityCompat.requestPermissions(CreateUserActivity.this,
  //              new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_REQUEST_CODE);
  //          // Show an explanation to the user *asynchronously* -- don't block
  //          // this thread waiting for the user's response! After the user
  //          // sees the explanation, try again to request the permission.
  //
  //        } else {
  //
  //          // No explanation needed, we can request the permission.
  //
  //          ActivityCompat.requestPermissions(CreateUserActivity.this,
  //              new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_REQUEST_CODE);
  //
  //          // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
  //          // app-defined int constant. The callback method gets the
  //          // result of the request.
  //        }
  //      } else {
  //        changePermissionValue(true);
  //        callGallery();
  //      }
  //    } else if (type.equals(TYPE_CAMERA)) {
  //      if (ContextCompat.checkSelfPermission(CreateUserActivity.this, Manifest.permission.CAMERA)
  //          != PackageManager.PERMISSION_GRANTED) {
  //
  //        // Should we show an explanation?
  //        if (ActivityCompat.shouldShowRequestPermissionRationale(CreateUserActivity.this, Manifest.permission.CAMERA)) {
  //
  //          ActivityCompat.requestPermissions(CreateUserActivity.this,
  //              new String[] { Manifest.permission.CAMERA }, CAMERA_REQUEST_CODE);
  //          // Show an explanation to the user *asynchronously* -- don't block
  //          // this thread waiting for the user's response! After the user
  //          // sees the explanation, try again to request the permission.
  //
  //        } else {
  //
  //          // No explanation needed, we can request the permission.
  //
  //          ActivityCompat.requestPermissions(CreateUserActivity.this,
  //              new String[] { Manifest.permission.CAMERA }, CAMERA_REQUEST_CODE);
  //
  //          // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
  //          // app-defined int constant. The callback method gets the
  //          // result of the request.
  //        }
  //      } else {
  //        changePermissionValue(true);
  //        dispatchTakePictureIntent();
  //      }
  //    }
  //    }
  //  }

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
          dispatchTakePictureIntent();
        } else {
          //TODO: Deal with permissions not being given by user
        }
    }
  }

  private void changePermissionValue(boolean b) {
    result = b;
  }

  private void saveUserDataOnPreferences() {
    AccountManagerPreferences.setUserAvatar(avatarPath);
    AccountManagerPreferences.setUserNickName(username);
  }

  private void callPermissionAndAction(String type) {
    String result = PermissionsBaseActivity.checkAndAskPermission(CreateUserActivity.this, type);
    switch (result) {
      case PermissionsBaseActivity.CAMERA_PERMISSION_GIVEN:
        changePermissionValue(true);
        dispatchTakePictureIntent();
        break;
      case PermissionsBaseActivity.STORAGE_PERMISSION_GIVEN:
        changePermissionValue(true);
        callGallery();
        break;
    }
  }
}