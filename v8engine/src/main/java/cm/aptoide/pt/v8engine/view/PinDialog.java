package cm.aptoide.pt.v8engine.view;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.widget.EditText;
import cm.aptoide.pt.v8engine.view.binding.RxAlertDialog;

/**
 * Created by marcelobenites on 09/03/17.
 */

public class PinDialog extends EditableTextDialog {

  public PinDialog(RxAlertDialog dialog, EditText editText) {
    super(dialog, editText);
  }

  @Override public void show() {
    super.show();
    setText("");
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

    public PinDialog.Builder setEditText(@IdRes int editTextId) {
      this.editTextId = editTextId;
      return this;
    }

    public PinDialog.Builder setView(@LayoutRes int viewRes) {
      this.viewRes = viewRes;
      return this;
    }

    public PinDialog.Builder setMessage(@StringRes int messageId) {
      builder.setMessage(messageId);
      return this;
    }

    public PinDialog.Builder setPositiveButton(@StringRes int textId) {
      builder.setPositiveButton(textId);
      return this;
    }

    public PinDialog.Builder setNegativeButton(@StringRes int textId) {
      builder.setNegativeButton(textId);
      return this;
    }

    public PinDialog build() {

      if (viewRes != 0 && editTextId != 0) {
        final android.view.View view = layoutInflater.inflate(viewRes, null, false);
        final EditText pinEditText = ((EditText) view.findViewById(editTextId));
        builder.setView(view);
        return new PinDialog(builder.build(), pinEditText);
      }
      throw new IllegalArgumentException("View and edit text resource ids must be provided");
    }
  }
}
