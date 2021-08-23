package cm.aptoide.pt.home;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import cm.aptoide.pt.R;
import rx.Observable;
import rx.subjects.PublishSubject;

public class EskillsHomeDialog {

  private AlertDialog dialog;
  private final View dialogView;
  private Button navigate;
  private Button cancel;
  private PublishSubject<String> uiEvents;

  public EskillsHomeDialog(Context context) {
    uiEvents = PublishSubject.create();
    LayoutInflater inflater = LayoutInflater.from(context);
    dialog = new AlertDialog.Builder(context).create();
    dialogView = inflater.inflate(R.layout.eskills_home_dialog, null);
    dialog.setView(dialogView);
    cancel = dialogView.findViewById(R.id.cancel_button);
    navigate = dialogView.findViewById(R.id.navigate_button);
    dialog.setCancelable(true);
    dialog.setCanceledOnTouchOutside(true);

    Window window = dialog.getWindow();
    if (window != null) {
      window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    navigate.setOnClickListener(__ -> {
      if (uiEvents != null) {
        uiEvents.onNext("navigate");
      }
    });

    cancel.setOnClickListener(__ -> {
      if (uiEvents != null) {
        uiEvents.onNext("cancel");
      }
    });
  }

  public void showDialog() {
    dialog.show();
  }

  public void dismissDialog() {
    dialog.dismiss();
  }

  public void destroyDialog() {
    dismissDialog();
    dialog = null;
    navigate = null;
    cancel = null;
    uiEvents = null;
  }

  public Observable<String> dialogClicked() {
    return uiEvents;
  }
}