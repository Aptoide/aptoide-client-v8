package cm.aptoide.pt.view.dialog;

import android.content.DialogInterface;
import android.widget.EditText;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import rx.Observable;

/**
 * Created by marcelobenites on 08/03/17.
 */

public class EditableTextDialog implements DialogInterface {

  private final RxAlertDialog dialog;
  private final EditText editText;

  public EditableTextDialog(RxAlertDialog dialog, EditText editText) {
    this.dialog = dialog;
    this.editText = editText;
  }

  public void show() {
    dialog.show();
  }

  @Override public void cancel() {
    dialog.cancel();
  }

  @Override public void dismiss() {
    dialog.dismiss();
  }

  public Observable<CharSequence> positiveClicks() {
    return dialog.positiveClicks()
        .map(click -> editText.getText());
  }

  public Observable<DialogInterface> negativeClicks() {
    return dialog.negativeClicks();
  }

  protected void setText(String text) {
    editText.setText(text);
  }
}
