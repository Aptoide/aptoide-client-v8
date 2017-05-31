package cm.aptoide.pt.v8engine.view.account.store;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxDialogFragment;

public class ImageSourceSelectionDialogFragment extends RxDialogFragment {

  private static final int LAYOUT = R.layout.dialog_choose_avatar_source;
  private View cancel;
  private View selectFromGallery;
  private View selectFromCamera;
  private ImageSourceSelectionHandler sourceSelectionHandler;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      sourceSelectionHandler = (ImageSourceSelectionHandler) getTargetFragment();
    } catch (ClassCastException ex) {
      throw new IllegalStateException("Calling fragment must implement interface "
          + ImageSourceSelectionHandler.class.getName());
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    RxView.clicks(cancel)
        .doOnNext(__ -> dismiss())
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));

    RxView.clicks(selectFromCamera)
        .doOnNext(__ -> {
          sourceSelectionHandler.selectedCamera();
          dismiss();
        })
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));

    RxView.clicks(selectFromGallery)
        .doOnNext(__ -> {
          sourceSelectionHandler.selectedGallery();
          dismiss();
        })
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe(__ -> {
        }, err -> CrashReport.getInstance()
            .log(err));
  }

  @Override public void onDestroy() {
    sourceSelectionHandler = null;
    super.onDestroy();
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.setTitle(R.string.upload_dialog_title);
    return dialog;
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(LAYOUT, container, false);
    if (view != null) {
      cancel = view.findViewById(R.id.cancel);
      selectFromGallery = view.findViewById(R.id.button_gallery);
      selectFromCamera = view.findViewById(R.id.button_camera);
    }
    return view;
  }

  public interface ImageSourceSelectionHandler {
    void selectedGallery();

    void selectedCamera();
  }
}
