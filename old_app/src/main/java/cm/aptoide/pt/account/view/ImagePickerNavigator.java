package cm.aptoide.pt.account.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import cm.aptoide.pt.navigator.ActivityNavigator;
import rx.Observable;

public class ImagePickerNavigator {

  private final ActivityNavigator activityNavigator;

  public ImagePickerNavigator(ActivityNavigator activityNavigator) {
    this.activityNavigator = activityNavigator;
  }

  public Observable<String> navigateToGalleryForImageUri(int requestCode) {
    return activityNavigator.navigateForResult(Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, requestCode)
        .filter(result -> result.getResultCode() == Activity.RESULT_OK)
        .map(result -> result.getData()
            .getData()
            .toString());
  }

  public Observable<Void> navigateToCameraWithImageUri(int requestCode, Uri fileName) {
    return activityNavigator.navigateForResultWithOutput(MediaStore.ACTION_IMAGE_CAPTURE, fileName,
        requestCode)
        .filter(result -> result.getResultCode() == Activity.RESULT_OK)
        .map(result -> null);
  }
}
