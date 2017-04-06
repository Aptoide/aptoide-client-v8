package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.PostCommentForTimelineArticle;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.account.user.CreateStoreActivity;
import cm.aptoide.pt.v8engine.view.timeline.LikeButtonView;
import cm.aptoide.pt.v8engine.view.comments.CommentDialogFragment;
import cm.aptoide.pt.v8engine.view.dialog.SharePreviewDialog;
import cm.aptoide.pt.v8engine.interfaces.ShareCardCallback;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.timeline.displayable.CardDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 29/11/2016.
 */

public abstract class CardWidget<T extends CardDisplayable> extends Widget<T> {

  private static final String TAG = CardWidget.class.getName();
  TextView shareButton;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private LinearLayout like;
  private LikeButtonView likeButton;
  private TextView comment;
  private AlertDialog alertDialog;
  private Account account;

  CardWidget(View itemView) {
    super(itemView);
  }

  @CallSuper @Override protected void assignViews(View itemView) {
    shareButton = (TextView) itemView.findViewById(R.id.social_share);
    like = (LinearLayout) itemView.findViewById(R.id.social_like);
    comment = (TextView) itemView.findViewById(R.id.social_comment);
    likeButton = (LikeButtonView) itemView.findViewById(R.id.social_like_button);
  }

  @Override public void unbindView() {
    if (alertDialog != null && alertDialog.isShowing()) {
      alertDialog.dismiss();
    }
    super.unbindView();
  }

  @CallSuper @Override public void bindView(T displayable) {
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator());

    compositeSubscription.add(
        accountManager.accountStatus().doOnNext(account -> updateAccount(account)).subscribe());
    like.setVisibility(View.VISIBLE);
    comment.setVisibility(View.VISIBLE);

    compositeSubscription.add(RxView.clicks(like)
        .subscribe(click -> likeButton.performClick(),
            throwable -> CrashReport.getInstance().log(throwable)));

    compositeSubscription.add(RxView.clicks(likeButton).subscribe(click -> {
      shareCard(displayable, (String cardId) -> likeCard(displayable, cardId, 1),
          SharePreviewDialog.SharePreviewOpenMode.LIKE);
      likeButton.setHeartState(false);
    }, throwable -> CrashReport.getInstance().log(throwable)));

    compositeSubscription.add(RxView.clicks(comment).subscribe(click -> {
      FragmentManager fm = getContext().getSupportFragmentManager();
      CommentDialogFragment commentDialogFragment =
          CommentDialogFragment.newInstanceTimelineArticleComment(
              displayable.getTimelineCard().getCardId());
      commentDialogFragment.setCommentBeforeSubmissionCallbackContract(
          (inputText) -> shareCard(displayable,
              cardId -> PostCommentForTimelineArticle.of(cardId, inputText, bodyInterceptor)
                  .observe()
                  .subscribe(), SharePreviewDialog.SharePreviewOpenMode.COMMENT));
      commentDialogFragment.show(fm, "fragment_comment_dialog");
    }, throwable -> CrashReport.getInstance().log(throwable)));

    compositeSubscription.add(RxView.clicks(shareButton)
        .subscribe(
            click -> shareCard(displayable, null, SharePreviewDialog.SharePreviewOpenMode.SHARE),
            err -> CrashReport.getInstance().log(err)));
  }

  private void updateAccount(Account account) {
    this.account = account;
  }

  private void shareCard(T displayable, ShareCardCallback callback,
      SharePreviewDialog.SharePreviewOpenMode openMode) {
    if (!accountManager.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> accountNavigator.navigateToAccountView());
      return;
    }

    if (TextUtils.isEmpty(account.getStoreName()) && !Account.Access.PUBLIC.equals(
        account.getAccess())) {
      ShowMessage.asSnack(getContext(), R.string.private_profile_create_store,
          R.string.create_store_create, snackView -> {
            Intent intent = new Intent(getContext(), CreateStoreActivity.class);
            getContext().startActivity(intent);
          });
      return;
    }

    SharePreviewDialog sharePreviewDialog =
        new SharePreviewDialog(displayable, accountManager, true, openMode);
    AlertDialog.Builder alertDialog = sharePreviewDialog.getPreviewDialogBuilder(getContext());

    Observable.create((Subscriber<? super GenericDialogs.EResponse> subscriber) -> {
      if (!accountManager.isAccountAccessConfirmed()) {
        alertDialog.setPositiveButton(R.string.share, (dialogInterface, i) -> {
          displayable.share(getContext(), sharePreviewDialog.getPrivacyResult(), callback);
          subscriber.onNext(GenericDialogs.EResponse.YES);
          subscriber.onCompleted();
        }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
          subscriber.onNext(GenericDialogs.EResponse.NO);
          subscriber.onCompleted();
        });
      } else {
        alertDialog.setPositiveButton(R.string.continue_option, (dialogInterface, i) -> {
          displayable.share(getContext(), callback);
          subscriber.onNext(GenericDialogs.EResponse.YES);
          subscriber.onCompleted();
        }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
          subscriber.onNext(GenericDialogs.EResponse.NO);
          subscriber.onCompleted();
        });
      }
      alertDialog.show();
    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(eResponse -> {
      switch (eResponse) {
        case YES:
          ShowMessage.asSnack(getContext(), R.string.social_timeline_share_dialog_title);
          break;
        case NO:
          break;
        case CANCEL:
          break;
      }
    });
  }

  private Account getAccount() {
    return account;
  }

  private boolean likeCard(T displayable, String cardId, int rating) {
    if (!accountManager.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            accountNavigator.navigateToAccountView();
          });
      return false;
    }
    displayable.like(getContext(), cardId, getCardTypeName().toUpperCase(), rating);
    return true;
  }

  abstract String getCardTypeName();

  protected void knockWithSixpackCredentials(String url) {
    if (url == null) {
      return;
    }

    String credential = Credentials.basic(BuildConfig.SIXPACK_USER, BuildConfig.SIXPACK_PASSWORD);

    OkHttpClient client = new OkHttpClient();

    Request click = new Request.Builder().url(url).addHeader("authorization", credential).build();

    client.newCall(click).enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        Logger.d(this.getClass().getSimpleName(), "sixpack request fail " + call.toString());
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        Logger.d(this.getClass().getSimpleName(), "sixpack knock success");
        response.body().close();
      }
    });
  }

  //
  // all cards are "shareable"
  //

  protected void setCardViewMargin(CardDisplayable displayable, CardView cardView) {
    CardView.LayoutParams layoutParams =
        new CardView.LayoutParams(CardView.LayoutParams.WRAP_CONTENT,
            CardView.LayoutParams.WRAP_CONTENT);
    layoutParams.setMargins(displayable.getMarginWidth(getContext(),
        getContext().getResources().getConfiguration().orientation), 0,
        displayable.getMarginWidth(getContext(),
            getContext().getResources().getConfiguration().orientation), 30);
    cardView.setLayoutParams(layoutParams);
  }
}
