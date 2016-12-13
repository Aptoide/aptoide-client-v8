package cm.aptoide.accountmanager;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import cm.aptoide.pt.logger.Logger;
import com.jakewharton.rxbinding.view.RxView;
import java.io.File;
import rx.subscriptions.CompositeSubscription;

import static cm.aptoide.accountmanager.CreateUserActivity.REQUEST_CAMERA_CODE;
import static cm.aptoide.accountmanager.CreateUserActivity.REQUEST_IMAGE_CAPTURE;

/**
 * Created by pedroribeiro on 02/12/16.
 */

public abstract class PermissionsBaseActivity extends BaseActivity {

  protected static final int STORAGE_REQUEST_CODE = 123;
  protected static final int CAMERA_REQUEST_CODE = 124;
  static final int REQUEST_CAMERA_CODE = 1046;
  static final int REQUEST_IMAGE_CAPTURE = 1;

  private static final String TYPE_STORAGE = "storage";
  private static final String TYPE_CAMERA = "camera";
  private String TAG = "STORAGE";
  private File avatar;
  protected static String photoAvatar = "aptoide_user_avatar.png";

  static final String STORAGE_PERMISSION_GIVEN = "storage_permission_given";
  static final String CAMERA_PERMISSION_GIVEN = "camera_permission_given";
  static final String STORAGE_PERMISSION_REQUESTED = "storage_permission_requested";
  static final String CAMERA_PERMISSION_REQUESTED = "camera_permission_requested";

  public boolean result;

  private CompositeSubscription mSubscriptions;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mSubscriptions = new CompositeSubscription();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mSubscriptions.clear();
  }

  @Override protected String getActivityTitle() {
    return null;
  }

  @Override int getLayoutId() {
    return 0;
  }

  public static String checkAndAskPermission(final AppCompatActivity activity, String type) {

    if(Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.M) {
      /*if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && type.equals(TYPE_STORAGE)) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
          mSubscriptions.add(GenericDialogs.createGenericContinueCancelMessage(context, "",
              "Permission for storage")
              .filter(answer -> (answer.equals(GenericDialogs.EResponse.YES)))
              .subscribe(answer -> {
                changePermissionValue(true);
              }));
          AndroidBasicDialog dialog = AndroidBasicDialog.build(context);
          dialog.setMessage("permission test")
              .setPositiveButton("allow", )
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 123);
        } else {
          ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 123);
        }
      } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED & type.equals(TYPE_CAMERA)) {
          if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            mSubscriptions.add(GenericDialogs.createGenericContinueCancelMessage(context, "",
                "Permission for camera")
                .filter(answer -> (answer.equals(GenericDialogs.EResponse.YES)))
                .subscribe(answer -> {
                  changePermissionValue(true);
                  ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, 123);
                }));
          } else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, 123);
          }
        }*/
      if (type.equals(TYPE_STORAGE)) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

          // Should we show an explanation?
          if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions(activity,
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_REQUEST_CODE);
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            return STORAGE_PERMISSION_REQUESTED;
          } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(activity,
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_REQUEST_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
          }
        } else {
          return STORAGE_PERMISSION_GIVEN;
        }
      } else if (type.equals(TYPE_CAMERA)) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

          // Should we show an explanation?
          if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {

            ActivityCompat.requestPermissions(activity,
                new String[] { Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE }, CAMERA_REQUEST_CODE);
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            return CAMERA_PERMISSION_REQUESTED;
          } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(activity,
                new String[] { Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE  }, CAMERA_REQUEST_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
          }
        } else {
          return CAMERA_PERMISSION_GIVEN;
        }
      }
    } else {
     if (type.equals(TYPE_CAMERA)) {
       return CAMERA_PERMISSION_GIVEN;
     } else if (type.equals(TYPE_STORAGE)) {
       return STORAGE_PERMISSION_GIVEN;
     }
    }
    return "";
  }


  protected static String createAvatarPhotoName(String avatar) {
    //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");
    String output = avatar /*+ simpleDateFormat.toString()*/;
    return output;
  }




  public void chooseAvatarSource() {
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.dialog_choose_avatar_layout);
    mSubscriptions.add(RxView.clicks(dialog.findViewById(R.id.button_camera)).subscribe(click -> {
      callPermissionAndAction(TYPE_CAMERA);
      dialog.dismiss();
    }));
    mSubscriptions.add(RxView.clicks(dialog.findViewById(R.id.button_gallery)).subscribe(click -> {
      callPermissionAndAction(TYPE_STORAGE);
      dialog.dismiss();
    }));
    mSubscriptions.add(RxView.clicks(dialog.findViewById(R.id.cancel))
        .subscribe(click -> dialog.dismiss())
    );
    dialog.show();
  }

  public void callPermissionAndAction(String type) {
    String result = PermissionsBaseActivity.checkAndAskPermission(PermissionsBaseActivity.this, type);
    switch (result) {
      case PermissionsBaseActivity.CAMERA_PERMISSION_GIVEN:
        changePermissionValue(true);
        dispatchTakePictureIntent(getApplicationContext());
        break;
      case PermissionsBaseActivity.STORAGE_PERMISSION_GIVEN:
        changePermissionValue(true);
        callGallery();
        break;
    }
  }

  public void changePermissionValue(boolean b) {
    result = b;
  }

  public void callGallery() {
    if (result) {
      Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      if (intent.resolveActivity(getPackageManager()) != null) {
        startActivityForResult(intent, REQUEST_CAMERA_CODE);
      }
    }
  }


  public void dispatchTakePictureIntent(Context context) {
    if (result) {
      setFileName();
      Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(PermissionsBaseActivity.createAvatarPhotoName(photoAvatar)));
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
      }
    }
  }

  private void setFileName() {
    if (getActivityTitle().equals("Create User Profile")) {
      photoAvatar = "aptoide_user_avatar.png";
    } else if (getActivityTitle().equals("Create Your Store")) {
      photoAvatar = "aptoide_store_avatar.png";
    }
  }

  public Uri getPhotoFileUri(String fileName) {
    File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ".aptoide/user_avatar");
    if (!storageDir.exists() && !storageDir.mkdirs()) {
      Logger.d(TAG, "Failed to create directory");
    }
    avatar = storageDir;
    return Uri.fromFile(new File(storageDir.getPath() + File.separator + fileName));
  }

}
