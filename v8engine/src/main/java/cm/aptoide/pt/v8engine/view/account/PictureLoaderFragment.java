/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.view.account;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
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
// migrate the profile picture rules to another entity
@Deprecated public abstract class PictureLoaderFragment extends BaseToolbarFragment {

  public static final int GALLERY_CODE = 1046;
  public static final int REQUEST_IMAGE_CAPTURE = 1;
  protected static final String FILE_NAME = "file_name";
  private static final String TAG = PictureLoaderFragment.class.getName();
  protected String photoFileName;
  private boolean createUserProfile;
  private boolean createStore;

  protected PictureLoaderFragment(boolean createUserProfile, boolean createStore) {
    this.createUserProfile = createUserProfile;
    this.createStore = createStore;
  }

  public void chooseAvatarSource() {
    final Dialog dialog = new Dialog(getActivity());
    dialog.setContentView(R.layout.dialog_choose_avatar_layout);

    RxView.clicks(dialog.findViewById(R.id.button_camera))
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> {
          PictureLoaderFragment.this.requestAccessToCamera(() -> {
            dispatchTakePictureIntent();
          }, () -> {
            Logger.e(TAG, "User denied access to camera");
          });
          dialog.dismiss();
        });

    RxView.clicks(dialog.findViewById(R.id.button_gallery))
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> {
          PictureLoaderFragment.this.requestAccessToExternalFileSystem(false,
              R.string.access_to_open_gallery_rationale, () -> {
                dispatchOpenGalleryIntent();
              }, () -> {
                Logger.e(TAG, "User denied access to camera");
              });
          dialog.dismiss();
        });

    RxView.clicks(dialog.findViewById(R.id.cancel))
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> dialog.dismiss());

    dialog.show();
  }

  public void dispatchTakePictureIntent() {
    photoFileName = getPhotoFileName();
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Uri uriForFile = FileProvider.getUriForFile(getActivity(), Application.getConfiguration()
            .getAppId() + ".provider", new File(getFileUriFromFileName(photoFileName).getPath()));
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
      } else {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUriFromFileName(photoFileName));
      }
      takePictureIntent.putExtra(FILE_NAME, photoFileName);
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
  }

  public void dispatchOpenGalleryIntent() {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
      startActivityForResult(intent, GALLERY_CODE);
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

  protected Uri getFileUriFromFileName(String fileName) {
    File storageDir = new File(Environment.getExternalStorageDirectory()
        .getAbsolutePath(), ".aptoide/user_avatar");
    if (!storageDir.exists() && !storageDir.mkdirs()) {
      CrashReport.getInstance()
          .log(new IOException("Failed to create directory"));
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

    int index = message.lastIndexOf("\n");
    if (index > 0) {
      message.delete(index, message.length());
    }

    return message.toString();
  }
}
