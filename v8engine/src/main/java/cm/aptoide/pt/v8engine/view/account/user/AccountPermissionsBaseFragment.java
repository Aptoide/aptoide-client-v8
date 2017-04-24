/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.view.account.user;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.fragment.BaseToolbarFragment;
import com.jakewharton.rxbinding.view.RxView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by pedroribeiro on 02/12/16.
 */

// FIXME: 6/4/2017
// pass all the permission request actions to "PermissionServiceFragment"
// migrate the profile picture rules to another entity or use a shrinking strategy to the supplied picture
@Deprecated abstract class AccountPermissionsBaseFragment extends BaseToolbarFragment {

  public static final int GALLERY_CODE = 1046;
  public static final int REQUEST_IMAGE_CAPTURE = 1;
  protected static final int CREATE_STORE_REQUEST_CODE = 1;
  protected static final int USER_PROFILE_CODE = 125;
  protected static final int STORAGE_REQUEST_CODE = 123;
  protected static final int CAMERA_REQUEST_CODE = 124;
  protected static final String FILE_NAME = "file_name";
  private static final String TAG = AccountPermissionsBaseFragment.class.getName();
  private static final String STORAGE_PERMISSION_GIVEN = "storage_permission_given";
  private static final String CAMERA_PERMISSION_GIVEN = "camera_permission_given";
  private static final String STORAGE_PERMISSION_REQUESTED = "storage_permission_requested";
  private static final String CAMERA_PERMISSION_REQUESTED = "camera_permission_requested";
  private static final String TYPE_STORAGE = "storage";
  private static final String TYPE_CAMERA = "camera";
  String photoFileName;
  private boolean userHasGivenPermission;
  private boolean createUserProfile;
  private boolean createStore;

  AccountPermissionsBaseFragment(boolean createUserProfile, boolean createStore) {
    this.createUserProfile = createUserProfile;
    this.createStore = createStore;
  }

  public static String checkAndAskPermission(final Activity activity, String type) {
    if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      if (type.equals(TYPE_STORAGE)) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

          if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
              Manifest.permission.READ_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions(activity,
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_REQUEST_CODE);
            return STORAGE_PERMISSION_REQUESTED;
          } else {
            ActivityCompat.requestPermissions(activity,
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, STORAGE_REQUEST_CODE);
          }
        } else {
          return STORAGE_PERMISSION_GIVEN;
        }
      } else if (type.equals(TYPE_CAMERA)) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
          if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
              Manifest.permission.CAMERA)) {

            ActivityCompat.requestPermissions(activity, new String[] {
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
            }, CAMERA_REQUEST_CODE);
            return CAMERA_PERMISSION_REQUESTED;
          } else {
            ActivityCompat.requestPermissions(activity, new String[] {
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
            }, CAMERA_REQUEST_CODE);
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

  public void chooseAvatarSource() {
    final Dialog dialog = new Dialog(getActivity());
    dialog.setContentView(R.layout.dialog_choose_avatar_layout);

    RxView.clicks(dialog.findViewById(R.id.button_camera))
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> {
          callPermissionAndAction(TYPE_CAMERA);
          dialog.dismiss();
        });

    RxView.clicks(dialog.findViewById(R.id.button_gallery))
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> {
          callPermissionAndAction(TYPE_STORAGE);
          dialog.dismiss();
        });

    RxView.clicks(dialog.findViewById(R.id.cancel))
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> dialog.dismiss());

    dialog.show();
  }

  public void callPermissionAndAction(String type) {
    String result = AccountPermissionsBaseFragment.checkAndAskPermission(getActivity(), type);
    switch (result) {
      case AccountPermissionsBaseFragment.CAMERA_PERMISSION_GIVEN:
        setUserHasGivenPermission(true);
        dispatchTakePictureIntent(getActivity().getApplicationContext());
        break;
      case AccountPermissionsBaseFragment.STORAGE_PERMISSION_GIVEN:
        setUserHasGivenPermission(true);
        dispatchOpenGalleryIntent();
        break;
    }
  }

  public void setUserHasGivenPermission(boolean b) {
    userHasGivenPermission = b;
  }

  public void dispatchTakePictureIntent(Context context) {
    if (userHasGivenPermission) {
      photoFileName = getPhotoFileName();
      Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          Uri uriForFile = FileProvider.getUriForFile(context,
              Application.getConfiguration().getAppId() + ".provider",
              new File(getFileUriFromFileName(photoFileName).getPath()));
          takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        } else {
          takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
              getFileUriFromFileName(photoFileName));
        }
        takePictureIntent.putExtra(FILE_NAME, photoFileName);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
      }
    }
  }

  public void dispatchOpenGalleryIntent() {
    if (userHasGivenPermission) {
      Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
        startActivityForResult(intent, GALLERY_CODE);
      }
    }
  }

  private String getPhotoFileName() {

    final String timestamp = getTimestampString();
    if (createUserProfile) {
      return String.format("aptoide_user_avatar_%s.jpg", timestamp);
    }

    if (createStore) {
      return String.format("aptoide_store_avatar_%s.jpg", timestamp);
    }

    // return default picture name
    return String.format("aptoide_photo_%s.jpg", timestamp);
  }

  private String getTimestampString() {
    return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
  }

  Uri getFileUriFromFileName(String fileName) {
    File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
        ".aptoide/user_avatar");
    if (!storageDir.exists() && !storageDir.mkdirs()) {
      CrashReport.getInstance().log(new IOException("Failed to create directory"));
    }
    return Uri.fromFile(new File(storageDir.getPath() + File.separator + fileName));
  }

  protected void checkAvatarRequirements(String avatarPath, Uri avatarUrl) {
    if (!TextUtils.isEmpty(avatarPath)) {
      List<AptoideUtils.IconSizeU.ImageErrors> imageErrors =
          AptoideUtils.IconSizeU.checkIconSizeProperties(avatarPath,
              getResources().getInteger(R.integer.min_avatar_height),
              getResources().getInteger(R.integer.max_avatar_height),
              getResources().getInteger(R.integer.min_avatar_width),
              getResources().getInteger(R.integer.max_avatar_width),
              getResources().getInteger(R.integer.max_avatar_Size));
      if (imageErrors.isEmpty()) {
        Logger.v(TAG, String.format("loading image with url '%s'", avatarUrl.toString()));
        loadImage(avatarUrl);
      } else {
        showIconPropertiesError(getErrorsMessage(imageErrors));
      }
    }
  }

  public abstract void loadImage(Uri imagePath);

  public abstract void showIconPropertiesError(String errors);

  private String getErrorsMessage(List<AptoideUtils.IconSizeU.ImageErrors> imageErrors) {
    StringBuilder message = new StringBuilder();
    message.append(getString(R.string.image_requirements_popup_message));
    for (AptoideUtils.IconSizeU.ImageErrors imageSizeError : imageErrors) {
      switch (imageSizeError) {
        case MIN_HEIGHT:
          message.append(getString(R.string.image_requirements_error_min_height));
          break;
        case MAX_HEIGHT:
          message.append(getString(R.string.image_requirements_error_max_height));
          break;
        case MIN_WIDTH:
          message.append(getString(R.string.image_requirements_error_min_width));
          break;
        case MAX_WIDTH:
          message.append(getString(R.string.image_requirements_error_max_width));
          break;
        case MAX_IMAGE_SIZE:
          message.append(getString(R.string.image_requirements_error_max_file_size));
          break;
        case ERROR_DECODING:
          message.append(getString(R.string.image_requirements_error_open_image));
          break;
      }
    }

    //remove last \n
    int index = message.lastIndexOf("\n");
    if (index > 0) {
      message.delete(index, message.length());
    }

    return message.toString();
  }
}
