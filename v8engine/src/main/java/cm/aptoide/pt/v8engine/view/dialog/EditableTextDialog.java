package cm.aptoide.pt.v8engine.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.widget.EditText;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
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
    return dialog.positiveClicks().map(click -> editText.getText());
  }

  public Observable<DialogInterface> negativeClicks() {
    return dialog.negativeClicks();
  }

  public Observable<DialogInterface> cancels() {
    return dialog.cancels();
  }

  public Observable<DialogInterface> dismisses() {
    return dialog.dismisses();
  }

  protected void setText(String text) {
    editText.setText(text);
  }

  public static class Builder {

    private final RxAlertDialog.Builder builder;
    private final LayoutInflater layoutInflater;
    private int viewRes;
    private int editTextId;

    public Builder(Context context) {
      this.builder = new RxAlertDialog.Builder(context);
      this.layoutInflater = LayoutInflater.from(context);
    }

    public Builder setEditText(@IdRes int editTextId) {
      this.editTextId = editTextId;
      return this;
    }

    public Builder setView(@LayoutRes int viewRes) {
      this.viewRes = viewRes;
      return this;
    }

    public Builder setMessage(@StringRes int messageId) {
      builder.setMessage(messageId);
      return this;
    }

    public Builder setPositiveButton(@StringRes int textId) {
      builder.setPositiveButton(textId);
      return this;
    }

    public Builder setNegativeButton(@StringRes int textId) {
      builder.setNegativeButton(textId);
      return this;
    }

    public EditableTextDialog build() {

      if (viewRes != 0 && editTextId != 0) {
        final android.view.View view = layoutInflater.inflate(viewRes, null, false);
        final EditText pinEditText = ((EditText) view.findViewById(editTextId));
        builder.setView(view);
        final RxAlertDialog dialog = builder.build();
        return new EditableTextDialog(dialog, pinEditText);
      }
      throw new IllegalArgumentException("View and edit text resource ids must be provided");
    }
  }
}