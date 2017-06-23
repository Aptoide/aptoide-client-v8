/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 09/02/2017.
 */

package cm.aptoide.pt.v8engine.view.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.permission.PermissionServiceFragment;
import com.jakewharton.rxbinding.view.RxView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by pedroribeiro on 02/12/16.
 */
public abstract class ImageLoaderFragment extends PermissionServiceFragment {

  public static final int GALLERY_CODE = 1046;
  public static final int REQUEST_IMAGE_CAPTURE = 1;
  private static final String EXTRA_FILE_NAME = "file_name";
  private static final String TAG = ImageLoaderFragment.class.getName();
  private static final SimpleDateFormat DATE_FORMAT =
      new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
  protected String fileName;

  public void chooseImageSource() {
    final Dialog dialog = new Dialog(getActivity());
    dialog.setContentView(R.layout.dialog_choose_avatar_source);

    RxView.clicks(dialog.findViewById(R.id.button_camera))
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> {
          ImageLoaderFragment.this.requestAccessToCamera(() -> {
            dispatchTakePictureIntent();
          }, () -> {
            Logger.e(TAG, "User denied access to camera");
          });
          dialog.dismiss();
        });

    RxView.clicks(dialog.findViewById(R.id.button_gallery))
        .compose(bindUntilEvent(LifecycleEvent.DESTROY))
        .subscribe(click -> {
          ImageLoaderFragment.this.requestAccessToExternalFileSystem(false,
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
    fileName = getFileName();
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
      prepareTakePictureIntent(takePictureIntent, fileName);
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
  }

  private void prepareTakePictureIntent(Intent takePictureIntent, String imageFileName) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Uri uriForFile = FileProvider.getUriForFile(getActivity(), Application.getConfiguration()
          .getAppId() + ".provider", new File(getFileUriFromFileName(imageFileName).getPath()));
      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
    } else {
      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUriFromFileName(imageFileName));
    }
    takePictureIntent.putExtra(EXTRA_FILE_NAME, imageFileName);
  }

  public void dispatchOpenGalleryIntent() {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
      startActivityForResult(intent, GALLERY_CODE);
    }
  }

  private String getFileName() {
    return String.format("aptoide_image_%s.jpg", getTimestampString());
  }

  private String getTimestampString() {
    return DATE_FORMAT.format(new Date());
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

  protected String getMediaStoragePath(Uri contentUri, Context context) {
    if (contentUri == null) {
      throw new NullPointerException("content Uri is null");
    }

    Cursor cursor = null;
    try {
      String[] projection = { MediaStore.Images.Media.DATA };
      cursor = context.getContentResolver()
          .query(contentUri, projection, null, null, null);
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      cursor.moveToFirst();
      return cursor.getString(column_index);
    } catch (NullPointerException ex) {
      Logger.e(TAG, ex);
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    // default situation
    return contentUri.getPath();
  }

  private ImageInfo getImageInfo(String imagePath) {
    ImageInfo imageInfo = null;
    Bitmap image = BitmapFactory.decodeFile(imagePath);
    if (image != null) {
      imageInfo = new ImageInfo(image.getWidth(), image.getHeight(), new File(imagePath).length());
    }
    return imageInfo;
  }

  private List<ImageErrors> checkIconSizeProperties(String imagePath, Resources resources) {
    final int minHeight = resources.getInteger(R.integer.min_avatar_height);
    final int maxHeight = resources.getInteger(R.integer.max_avatar_height);
    final int minWidth = resources.getInteger(R.integer.min_avatar_width);
    final int maxWidth = resources.getInteger(R.integer.max_avatar_width);
    final int maxImageSize = resources.getInteger(R.integer.max_avatar_Size);

    ImageInfo imageInfo = getImageInfo(imagePath);
    List<ImageErrors> errors = new LinkedList<>();
    if (imageInfo == null) {
      errors.add(ImageErrors.ERROR_DECODING);
    } else {
      if (imageInfo.getHeight() < minHeight) {
        errors.add(ImageErrors.MIN_HEIGHT);
      }
      if (imageInfo.getWidth() < minWidth) {
        errors.add(ImageErrors.MIN_WIDTH);
      }
      if (imageInfo.getHeight() > maxHeight) {
        errors.add(ImageErrors.MAX_HEIGHT);
      }
      if (imageInfo.getWidth() > maxWidth) {
        errors.add(ImageErrors.MAX_WIDTH);
      }
      if (imageInfo.getSize() > maxImageSize) {
        errors.add(ImageErrors.MAX_IMAGE_SIZE);
      }
    }
    return errors;
  }

  protected String imageHasErrors(String imagePath) {
    if (!TextUtils.isEmpty(imagePath)) {
      List<ImageErrors> imageErrors = checkIconSizeProperties(imagePath, getResources());
      if (imageErrors != null && !imageErrors.isEmpty()) {
        return buildImageErrorMessage(imageErrors);
      }
      return null;
    }
    return getString(R.string.image_requirements_error_open_image);
  }

  public abstract void loadImage(Uri imagePath);

  public abstract void showIconPropertiesError(String errors);

  private String buildImageErrorMessage(List<ImageErrors> imageErrors) {
    StringBuilder message = new StringBuilder();
    message.append(getString(R.string.image_requirements_popup_message));
    for (ImageErrors imageSizeError : imageErrors) {
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

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Uri imageUri;
    final Context applicationContext = getActivity().getApplicationContext();

    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
      imageUri = getFileUriFromFileName(fileName);
    } else if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK) {
      imageUri = data.getData();
    } else {
      Logger.e(TAG, "URI for content is null or empty");
      return;
    }

    if (imageUri != null) {
      try {
        String filePath = getMediaStoragePath(imageUri, applicationContext);
        String imageError = imageHasErrors(filePath);
        if (TextUtils.isEmpty(imageError)) {
          loadImage(imageUri);
          setImageRealPath(filePath);
        } else {
          showIconPropertiesError(imageError);
        }
      } catch (NullPointerException ex) {
        CrashReport.getInstance()
            .log(ex);
      }
    } else {
      Logger.w(TAG, "onActivityResult called with null image URI");
    }
  }

  protected abstract void setImageRealPath(String filePath);

  private enum ImageErrors {
    ERROR_DECODING, MIN_HEIGHT, MAX_HEIGHT, MIN_WIDTH, MAX_WIDTH, MAX_IMAGE_SIZE
  }

  private static class ImageInfo {
    private final int height;
    private final int width;
    private final long size;

    private ImageInfo(int height, int width, long size) {
      this.height = height;
      this.width = width;
      this.size = size;
    }

    public int getHeight() {
      return height;
    }

    public int getWidth() {
      return width;
    }

    public long getSize() {
      return size;
    }
  }
}
