package cm.aptoide.pt.view.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.view.dialog.EditableTextDialog;
import cm.aptoide.pt.view.rx.RxAlertDialog;

/**
 * Created by marcelobenites on 09/03/17.
 */

public class InputDialog extends EditableTextDialog {

  public InputDialog(RxAlertDialog dialog, EditText editText) {
    super(dialog, editText);
  }

  @Override public void show() {
    super.show();
    setText("");
  }

  public void showWithInput(String text) {
    super.show();
    setText(text);
  }

  public static class Builder {

    private final RxAlertDialog.Builder builder;
    private final LayoutInflater layoutInflater;
    private int viewRes;
    private int editTextId;

    public Builder(Context context, ThemeManager themeManager) {
      this.builder = new RxAlertDialog.Builder(context, themeManager);
      this.layoutInflater = LayoutInflater.from(context);
    }

    public InputDialog.Builder setEditText(@IdRes int editTextId) {
      this.editTextId = editTextId;
      return this;
    }

    public InputDialog.Builder setView(@LayoutRes int viewRes) {
      this.viewRes = viewRes;
      return this;
    }

    public InputDialog.Builder setMessage(@StringRes int messageId) {
      builder.setMessage(messageId);
      return this;
    }

    public InputDialog.Builder setPositiveButton(@StringRes int textId) {
      builder.setPositiveButton(textId);
      return this;
    }

    public InputDialog.Builder setNegativeButton(@StringRes int textId) {
      builder.setNegativeButton(textId);
      return this;
    }

    public InputDialog build() {

      if (viewRes != 0 && editTextId != 0) {
        final android.view.View view = layoutInflater.inflate(viewRes, null, false);
        final EditText pinEditText = view.findViewById(editTextId);
        builder.setView(view);
        return new InputDialog(builder.build(), pinEditText);
      }
      throw new IllegalArgumentException("View and edit text resource ids must be provided");
    }
  }
}
