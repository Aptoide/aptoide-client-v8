package cm.aptoide.pt.account.view;

import android.content.DialogInterface;
import cm.aptoide.pt.account.view.exception.InvalidImageException;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface ImagePickerView extends View {
  void loadImage(String pictureUri);

  Observable<DialogInterface> dialogCameraSelected();

  Observable<DialogInterface> dialogGallerySelected();

  void showImagePickerDialog();

  void showIconPropertiesError(InvalidImageException exception);

  Observable<Void> selectStoreImageClick();

  void dismissLoadImageDialog();
}
