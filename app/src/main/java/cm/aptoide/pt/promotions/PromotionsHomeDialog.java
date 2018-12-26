package cm.aptoide.pt.promotions;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.home.HomePromotionsWrapper;
import rx.Observable;
import rx.subjects.PublishSubject;

public class PromotionsHomeDialog {
  private AlertDialog dialog;
  private View dialogView;
  private Button navigate;
  private Button cancel;
  private PublishSubject<String> uiEvents;

  public PromotionsHomeDialog(Context context) {
    uiEvents = PublishSubject.create();
    LayoutInflater inflater = LayoutInflater.from(context);
    dialog = new AlertDialog.Builder(context).create();
    dialogView = inflater.inflate(R.layout.promotions_home_dialog, null);
    dialog.setView(dialogView);
    cancel = dialogView.findViewById(R.id.cancel_button);
    navigate = dialogView.findViewById(R.id.navigate_button);
    dialog.setCancelable(true);
    dialog.setCanceledOnTouchOutside(true);

    navigate.setOnClickListener(__ -> uiEvents.onNext("navigate"));

    cancel.setOnClickListener(__ -> uiEvents.onNext("cancel"));
  }

  public void showDialog(Context context, HomePromotionsWrapper wrapper) {
    dialog.show();
    TextView description = dialogView.findViewById(R.id.description);
    description.setText(context.getString(R.string.holidayspromotion_message_popup,
        String.valueOf(wrapper.getTotalAppcValue())));
  }

  public void dismissDialog() {
    dialog.dismiss();
  }

  public void destroyDialog() {
    dialog = null;
    navigate = null;
    cancel = null;
    uiEvents = null;
  }

  public Observable<String> dialogClicked() {
    return uiEvents;
  }
}