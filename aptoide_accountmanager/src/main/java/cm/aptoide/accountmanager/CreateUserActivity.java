package cm.aptoide.accountmanager;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import java.io.File;
import java.text.SimpleDateFormat;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by pedroribeiro on 24/11/16.
 */

public class CreateUserActivity extends BaseActivity {

  private Toolbar mToolbar;
  private RelativeLayout mUserAvatar;
  private EditText mUsername;
  private Button mCreateButton;
  private ImageView mAvatar;
  private CompositeSubscription mSubscriptions;

  static final int CALL_CAMERA_CODE = 1046;
  static final int REQUEST_IMAGE_CAPTURE = 1;

  private String aptoideUserAvatar = "aptoide_user_avatar.png";

  private String TAG = "STORAGE";

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getLayoutId());
    mSubscriptions = new CompositeSubscription();
    bindViews();
    setupToolbar();
    setupListeners();
  }

  @Override protected String getActivityTitle() {
    return getString(R.string.create_user_title);
  }

  @Override int getLayoutId() {
    return R.layout.fragment_create_user;
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

      }
    }));
    mSubscriptions.add(RxView.clicks(mUserAvatar).subscribe(click -> chooseAvatarSource()));
    mSubscriptions.add(RxView.clicks(mCreateButton).subscribe(click -> {
      finish();
    }));
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mSubscriptions.clear();
  }

  private void chooseAvatarSource() {
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.dialog_choose_avatar_layout);
    dialog.setTitle(R.string.create_user_dialog_title);
    mSubscriptions.add(RxView.clicks(dialog.findViewById(R.id.button_camera)).subscribe(click -> {
      dispatchTakePictureIntent();
      dialog.dismiss();
    }));
    mSubscriptions.add(RxView.clicks(dialog.findViewById(R.id.button_gallery)).subscribe(click -> {
      callGallery();
      dialog.dismiss();
    }));
    mSubscriptions.add(RxView.clicks(dialog.findViewById(R.id.button_cancel))
        .subscribe(click -> dialog.dismiss()));
    dialog.show();
  }

  private void callGallery() {
    checkPermission(getApplicationContext());
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    if (intent.resolveActivity(getPackageManager()) != null) {
      startActivityForResult(intent, CALL_CAMERA_CODE);
    }

  }

  private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(createAvatarPhotoName()));
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
  }

  private Uri getPhotoFileUri(String fileName) {
   // if (Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED)) {
      File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), ",aptoide");
      if (!storageDir.exists() && !storageDir.mkdirs()) {
        Logger.d(TAG, "Failed to create directory");
      }

      return Uri.fromFile(new File(storageDir.getPath() + File.separator + fileName));
    //}
    //return null;
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Uri avatarUrl = getPhotoFileUri(createAvatarPhotoName());
      ImageLoader.loadWithCircleTransform(avatarUrl, mAvatar);
    } else if (requestCode == CALL_CAMERA_CODE) {
      Uri avatarUrl = data.getData();
      ImageLoader.loadWithCircleTransform(avatarUrl, mAvatar);
    }
  }

  private String createAvatarPhotoName() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");
    String output = aptoideUserAvatar /*+ simpleDateFormat.toString()*/;
    return output;
  }

  public static boolean checkPermission(final Context context)
  {
    int currentAPIVersion = Build.VERSION.SDK_INT;
    if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
    {
      if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
          AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
          alertBuilder.setCancelable(true);
          alertBuilder.setTitle("Permission necessary");
          alertBuilder.setMessage("External storage permission is necessary");
          alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
              ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }
          });
          AlertDialog alert = alertBuilder.create();
          alert.show();
        } else {
          ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
        return false;
      } else {
        return true;
      }
    } else {
      return true;
    }
  }
}
