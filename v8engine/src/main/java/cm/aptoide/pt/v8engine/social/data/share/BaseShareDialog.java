package cm.aptoide.pt.v8engine.social.data.share;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import cm.aptoide.accountmanager.Account;
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
        .map(__ -> updateEventWithPrivacyCheckbox(shareEvent));
  }

  @Override public Observable<ShareEvent> cancels() {
    return dialog.cancels()
        .map(__ -> updateEventWithPrivacyCheckbox(cancelEvent));
  }

  public void show() {
    dialog.show();
  }

  @Override @CallSuper public void setup(T post) {
    setupView(dialog.getDialogView(), post);
    setupEvents(post);
  }

  private ShareEvent updateEventWithPrivacyCheckbox(ShareEvent shareEvent) {
    CheckBox makeMyProfilePrivate = (CheckBox) dialog.getDialogView()
        .findViewById(R.id.social_preview_checkbox);
    Account.Access access =
        makeMyProfilePrivate.isChecked() ? Account.Access.PRIVATE : Account.Access.PUBLIC;
    shareEvent.setAccess(access);
    return shareEvent;
  }

  private void setupEvents(T post) {
    shareEvent = new ShareEvent(ShareEvent.SHARE, post);
    cancelEvent = new ShareEvent(ShareEvent.CANCEL, post);
  }

  abstract void setupView(View view, T post);

  public static abstract class Builder {

    private final RxAlertDialog.Builder builder;
    private final Context context;
    private final SharePostViewSetup sharePostViewSetup;
    private final Account account;
    private final int layoutId;
    private final LayoutInflater layoutInflater;
    private int positiveStringRes = Integer.MAX_VALUE;
    private int negativeStringRes = Integer.MAX_VALUE;
    ;

    public Builder(Context context, SharePostViewSetup sharePostViewSetup, Account account,
        @LayoutRes int layoutId) {
      this.builder = new RxAlertDialog.Builder(context);
      layoutInflater = LayoutInflater.from(context);
      this.context = context;
      this.sharePostViewSetup = sharePostViewSetup;
      this.account = account;
      this.layoutId = layoutId;
    }

    public RxAlertDialog buildRxAlertDialog() {
      View view = getView();
      sharePostViewSetup.setup(view, context, account);
      builder.setView(view);
      builder.setPositiveButton(getPositiveMessage());
      builder.setNegativeButton(getNegativeMessage());
      return builder.build();
    }

    @StringRes private int getPositiveMessage() {
      return positiveStringRes != Integer.MAX_VALUE ? positiveStringRes : R.string.share;
    }

    public void setPositiveMessage(@StringRes int message) {
      this.positiveStringRes = message;
    }

    @StringRes private int getNegativeMessage() {
      return negativeStringRes != Integer.MAX_VALUE ? negativeStringRes : R.string.cancel;
    }

    public void setNegativeMessage(@StringRes int message) {
      this.negativeStringRes = message;
    }

    private View getView() {
      return layoutInflater.inflate(layoutId, null);
    }
  }
}
