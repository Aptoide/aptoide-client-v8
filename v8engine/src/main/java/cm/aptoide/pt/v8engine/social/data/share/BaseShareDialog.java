package cm.aptoide.pt.v8engine.social.data.share;

import android.support.annotation.CallSuper;
import android.view.View;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.social.data.Post;
import cm.aptoide.pt.v8engine.view.rx.RxAlertDialog;
import rx.Observable;

abstract class BaseShareDialog<T extends Post> implements ShareDialogInterface<T> {
  private RxAlertDialog dialog;
  private ShareEvent shareEvent;
  private ShareEvent cancelEvent;

  BaseShareDialog(RxAlertDialog dialog) {
    this.dialog = dialog;
  }

  @Override public void cancel() {
    dialog.cancel();
  }

  @Override public void dismiss() {
    dialog.dismiss();
  }

  @Override public Observable<ShareEvent> shares() {
    return dialog.positiveClicks()
        .map(__ -> shareEvent);
  }

  @Override public Observable<ShareEvent> cancels() {
    return dialog.cancels()
        .map(__ -> cancelEvent);
  }

  public void show() {
    dialog.show();
  }

  @Override @CallSuper public void setup(T post) {
    setupView(dialog.getView(R.id.dialog_root), post);
    setupEvents(post);
  }

  private void setupEvents(T post) {
    shareEvent = new ShareEvent(ShareEvent.SHARE, post);
    cancelEvent = new ShareEvent(ShareEvent.CANCEL, post);
  }

  abstract void setupView(View view, T post);
}
