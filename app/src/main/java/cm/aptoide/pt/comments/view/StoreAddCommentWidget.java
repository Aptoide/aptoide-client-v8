package cm.aptoide.pt.comments.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
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
    commentStore.setBackgroundDrawable(getContext().getResources()
        .getDrawable(displayable.getStoreTheme()
            .getRaisedButtonDrawable()));

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
          accountNavigator.navigateToAccountView(AccountAnalytics.AccountOrigins.STORE_COMMENT);
        });
  }
}
