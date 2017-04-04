package cm.aptoide.pt.v8engine.view.comments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.util.StoreThemeEnum;
import cm.aptoide.pt.v8engine.view.store.StoreAddCommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import rx.Observable;

public class StoreAddCommentWidget extends Widget<StoreAddCommentDisplayable> {

  private static final String TAG = StoreAddCommentWidget.class.getName();

  private Button commentStore;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;

  public StoreAddCommentWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    commentStore = (Button) itemView.findViewById(R.id.comment_store_button);
  }

  @Override public void bindView(StoreAddCommentDisplayable displayable) {

    final Context context = getContext();

    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator());
    @ColorInt int color = getColorOrDefault(displayable.getStoreTheme(), context);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      Drawable d = context.getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      commentStore.setBackground(d);
    } else {
      Drawable d = context.getResources().getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      commentStore.setBackgroundDrawable(d);
    }

    compositeSubscription.add(RxView.clicks(commentStore)
        .flatMap(a -> showStoreCommentFragment(displayable.getStoreId(), displayable.getStoreName(),
            getContext().getSupportFragmentManager(), commentStore))
        .subscribe(a -> {
          // all done when we get here.
        }, err -> {
          CrashReport.getInstance().log(err);
        }));
  }

  private int getColorOrDefault(StoreThemeEnum theme, Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources().getColor(theme.getStoreHeader(), context.getTheme());
    } else {
      return context.getResources().getColor(theme.getStoreHeader());
    }
  }

  private Observable<Void> showStoreCommentFragment(final long storeId,
      @NonNull final String storeName, @NonNull final FragmentManager fragmentManager,
      @NonNull final View view) {

    return Observable.just(accountManager.isLoggedIn()).flatMap(isLoggedIn -> {

      if (isLoggedIn) {
        // show fragment CommentDialog
        CommentDialogFragment commentDialogFragment =
            CommentDialogFragment.newInstanceStoreComment(storeId, storeName);

        return commentDialogFragment.lifecycle()
            .doOnSubscribe(
                () -> commentDialogFragment.show(fragmentManager, "fragment_comment_dialog"))
            .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
            .doOnNext(a -> reloadComments())
            .flatMap(event -> Observable.empty());
      }

      return showSignInMessage(view);
    });
  }

  private void reloadComments() {
    // TODO: 5/12/2016 sithengineer
    Logger.d(TAG, "TODO: reload the comments");
  }

  private Observable<Void> showSignInMessage(@NonNull final View view) {
    return ShowMessage.asObservableSnack(view, R.string.you_need_to_be_logged_in, R.string.login,
        snackView -> {
          accountNavigator.navigateToAccountView();
        }).flatMap(a -> Observable.empty());
  }
}
