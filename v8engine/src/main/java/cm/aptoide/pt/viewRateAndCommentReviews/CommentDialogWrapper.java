package cm.aptoide.pt.viewRateAndCommentReviews;

import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.R;
import com.jakewharton.rxbinding.view.RxView;
import lombok.Getter;
import rx.Observable;

public class CommentDialogWrapper {

  private AlertDialog dialog;
  private TextInputLayout textInputLayout;
  @Getter private Button commentButton, cancelButton;

  public CommentDialogWrapper() {

  }

  public void build(Context ctx, String appName) {
    final View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_comment_on_review, null);

    final TextView titleTextView = (TextView) view.findViewById(R.id.title);

    textInputLayout = (TextInputLayout) view.findViewById(R.id.input_layout_title);

    commentButton = (Button) view.findViewById(R.id.comment_button);
    cancelButton = (Button) view.findViewById(R.id.cancel_button);

    titleTextView.setText(appName);

    // build rating dialog
    final AlertDialog.Builder builder = new AlertDialog.Builder(ctx).setView(view);
    dialog = builder.create();
  }

  private String getText() {
    if (textInputLayout != null) {
      textInputLayout.getEditText().getText().toString();
    }
    return null;
  }

  public void enableError(String error) {
    textInputLayout.setError(error);
  }

  public void disableError() {
    textInputLayout.setErrorEnabled(false);
  }

  public void show() {
    if (!dialog.isShowing()) {
      dialog.show();
      // find a better way to do this
      cancelButton.setOnClickListener(a -> CommentDialogWrapper.this.dismiss());
    }
  }

  public void dismiss() {
    disableError();

    if (dialog.isShowing()) {
      dialog.dismiss();
    }
  }

  public Observable<String> onCommentButton() {
    return RxView.clicks(commentButton).flatMap(a -> Observable.just(getText()));
  }
}
