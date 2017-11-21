package cm.aptoide.pt.comments.view;

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
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.store.view.StoreAddCommentDisplayable;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.android.FragmentEvent;
import rx.Completable;
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

    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    accountNavigator = ((ActivityResultNavigator) getContext()).getAccountNavigator();
    @ColorInt int color = getColorOrDefault(displayable.getStoreTheme(), context);
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      Drawable d = context.getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      commentStore.setBackground(d);
    } else {
      Drawable d = context.getResources()
          .getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      commentStore.setBackgroundDrawable(d);
    }

    compositeSubscription.add(RxView.clicks(commentStore)
        .flatMap(a -> showStoreCommentFragment(displayable.getStoreId(), displayable.getStoreName(),
            getContext().getSupportFragmentManager(), commentStore))
        .subscribe(a -> {
          // all done when we get here.
        }, err -> {
          CrashReport.getInstance()
              .log(err);
        }));
  }

  private int getColorOrDefault(StoreTheme theme, Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources()
          .getColor(theme.getPrimaryColor(), context.getTheme());
    } else {
      return context.getResources()
          .getColor(theme.getPrimaryColor());
    }
  }

  private Observable<Void> showStoreCommentFragment(final long storeId,
      @NonNull final String storeName, @NonNull final FragmentManager fragmentManager,
      @NonNull final View view) {

    return Observable.just(accountManager.isLoggedIn())
        .flatMap(isLoggedIn -> {

          if (isLoggedIn) {
            // show fragment CommentDialog
            CommentDialogFragment commentDialogFragment =
                CommentDialogFragment.newInstanceStoreComment(storeId, storeName);

            return commentDialogFragment.lifecycle()
                .doOnSubscribe(
                    () -> commentDialogFragment.show(fragmentManager, "fragment_comment_dialog"))
                .filter(event -> event.equals(FragmentEvent.DESTROY_VIEW))
                .flatMap(event -> Observable.empty());
          }

          return showSignInMessage(view).toObservable();
        });
  }

  private Completable showSignInMessage(@NonNull final View view) {
    return ShowMessage.asObservableSnack(view, R.string.you_need_to_be_logged_in, R.string.login,
        snackView -> {
          accountNavigator.navigateToAccountView(Analytics.Account.AccountOrigins.STORE_COMMENT);
        });
  }
}
