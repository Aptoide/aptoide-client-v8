package cm.aptoide.pt.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;

public class ImagePickerDialog implements DialogInterface {

  public static final int LAYOUT = R.layout.dialog_choose_avatar_source;

  private final RxAlertDialog dialog;
  private View selectFromGallery;
  private View selectFromCamera;

  private ImagePickerDialog(RxAlertDialog dialog, TextView cameraButton, TextView galleryButton) {
    this.dialog = dialog;
    this.selectFromCamera = cameraButton;
    this.selectFromGallery = galleryButton;
  }

  public void show() {
    dialog.show();
  }

  public Observable<DialogInterface> cameraSelected() {
    return RxView.clicks(selectFromCamera)
        .map(__ -> this);
  }

  public Observable<DialogInterface> gallerySelected() {
    return RxView.clicks(selectFromGallery)
        .map(__ -> this);
  }

  public Observable<DialogInterface> cancelsSelected() {
    return dialog.cancels();
  }

  @Override public void cancel() {
    dialog.cancel();
  }

  @Override public void dismiss() {
    dialog.dismiss();
  }

  public static class Builder {

    private final RxAlertDialog.Builder builder;
    private final LayoutInflater layoutInflater;
    private int viewRes;
    private int galleryButtonRes;
    private int cameraButtonRes;

    public Builder(Context context) {
      this.builder = new RxAlertDialog.Builder(context);
      this.layoutInflater = LayoutInflater.from(context);
    }

    public ImagePickerDialog.Builder setViewRes(@LayoutRes int viewRes) {
      this.viewRes = viewRes;
      return this;
    }

    public ImagePickerDialog.Builder setCameraButton(@IdRes int textRes) {
      this.cameraButtonRes = textRes;
      return this;
    }

    public ImagePickerDialog.Builder setGalleryButton(@IdRes int textRes) {
      this.galleryButtonRes = textRes;
      return this;
    }

    public ImagePickerDialog.Builder setTitle(@StringRes int titleId) {
      builder.setTitle(titleId);
      return this;
    }

    public ImagePickerDialog.Builder setNegativeButton(@StringRes int textId) {
      builder.setNegativeButton(textId);
      return this;
    }

    public ImagePickerDialog build() {

      if (viewRes != 0 && cameraButtonRes != 0 && galleryButtonRes != 0) {
        final android.view.View view = layoutInflater.inflate(viewRes, null, false);
        final TextView cameraButton = ((TextView) view.findViewById(cameraButtonRes));
        final TextView galleryButton = ((TextView) view.findViewById(galleryButtonRes));
        builder.setView(view);
        final RxAlertDialog dialog = builder.build();
        return new ImagePickerDialog(dialog, cameraButton, galleryButton);
      }
      throw new IllegalArgumentException("View and edit text resource ids must be provided");
    }
  }
}
