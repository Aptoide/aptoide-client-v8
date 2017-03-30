package cm.aptoide.pt.v8engine.view.binding;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Observable;

/**
 * Created by marcelobenites on 08/03/17.
 */

public class RxAlertDialog implements DialogInterface {

  private final AlertDialog dialog;
  private final DialogClick negativeClick;
  private final DialogClick positiveClick;
  private final CancelEvent cancelEvent;
  private final DismissEvent dismissEvent;

  protected RxAlertDialog(AlertDialog dialog, DialogClick positiveClick, DialogClick negativeClick,
      CancelEvent cancelEvent, DismissEvent dismissEvent) {
    this.dialog = dialog;
    this.positiveClick = positiveClick;
    this.negativeClick = negativeClick;
    this.cancelEvent = cancelEvent;
    this.dismissEvent = dismissEvent;
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

  public Observable<DialogInterface> positiveClicks() {
    if (positiveClick != null) {
      return positiveClick.clicks().map(click -> this);
    }
    return Observable.empty();
  }

  public Observable<DialogInterface> negativeClicks() {
    if (negativeClick != null) {
      return negativeClick.clicks().map(click -> this);
    }
    return Observable.empty();
  }

  public Observable<DialogInterface> cancels() {
    return cancelEvent.cancels().map(click -> this);
  }

  public Observable<DialogInterface> dismisses() {
    return dismissEvent.dismisses().map(click -> this);
  }

  public static class Builder {

    private final AlertDialog.Builder builder;

    private DialogClick positiveClick;
    private DialogClick negativeClick;

    public Builder(Context context) {
      this.builder = new AlertDialog.Builder(context);
    }

    public Builder setView(View view) {
      builder.setView(view);
      return this;
    }

    public Builder setMessage(@StringRes int messageId) {
      builder.setMessage(messageId);
      return this;
    }

    public Builder setPositiveButton(@StringRes int textId) {
      positiveClick = new DialogClick(DialogInterface.BUTTON_POSITIVE, PublishRelay.create());
      builder.setPositiveButton(textId, positiveClick);
      return this;
    }

    public Builder setNegativeButton(@StringRes int textId) {
      negativeClick = new DialogClick(DialogInterface.BUTTON_NEGATIVE, PublishRelay.create());
      builder.setNegativeButton(textId, negativeClick);
      return this;
    }

    public RxAlertDialog build() {
      final AlertDialog dialog = builder.create();
      final CancelEvent cancelEvent = new CancelEvent(PublishRelay.create());
      final DismissEvent dismissEvent = new DismissEvent(PublishRelay.create());
      dialog.setOnCancelListener(cancelEvent);
      dialog.setOnDismissListener(dismissEvent);
      return new RxAlertDialog(dialog, positiveClick, negativeClick, cancelEvent, dismissEvent);
    }
  }

  protected static class DismissEvent implements DialogInterface.OnDismissListener {

    private final PublishRelay<Void> subject;

    public DismissEvent(PublishRelay<Void> subject) {
      this.subject = subject;
    }

    @Override public void onDismiss(DialogInterface dialog) {
      subject.call(null);
    }

    public Observable<Void> dismisses() {
      return subject;
    }
  }

  protected static class CancelEvent implements DialogInterface.OnCancelListener {

    private final PublishRelay<Void> subject;

    public CancelEvent(PublishRelay<Void> subject) {
      this.subject = subject;
    }

    @Override public void onCancel(DialogInterface dialog) {
      subject.call(null);
    }

    public Observable<Void> cancels() {
      return subject;
    }
  }

  protected static class DialogClick implements DialogInterface.OnClickListener {

    private final int which;
    private final PublishRelay<Void> subject;

    public DialogClick(int which, PublishRelay<Void> subject) {
      this.which = which;
      this.subject = subject;
    }

    @Override public void onClick(DialogInterface dialog, int which) {
      if (this.which == which) {
        subject.call(null);
      }
    }

    public Observable<Void> clicks() {
      return subject;
    }
  }
}
