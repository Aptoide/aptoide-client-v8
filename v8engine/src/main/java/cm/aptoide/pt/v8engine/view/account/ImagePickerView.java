package cm.aptoide.pt.v8engine.view.account;

import android.content.DialogInterface;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.account.exception.InvalidImageException;
import rx.Observable;

public interface ImagePickerView extends View {
  void loadImage(String pictureUri);

  Observable<DialogInterface> dialogCameraSelected();

  Observable<DialogInterface> dialogGallerySelected();

  Observable<DialogInterface> dialogCancelsSelected();

  void showImagePickerDialog();

  void showIconPropertiesError(InvalidImageException exception);

  Observable<Void> selectStoreImageClick();

  void dismissLoadImageDialog();
}
