package cm.aptoide.pt.v8engine.timeline.view.widget;

import android.support.annotation.CallSuper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.PostCommentForTimelineArticle;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.timeline.view.LikeButtonView;
import cm.aptoide.pt.v8engine.timeline.view.ShareCardCallback;
import cm.aptoide.pt.v8engine.timeline.view.displayable.CardDisplayable;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.account.store.ManageStoreFragment;
import cm.aptoide.pt.v8engine.view.comments.CommentDialogFragment;
import cm.aptoide.pt.v8engine.view.dialog.SharePreviewDialog;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Converter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 29/11/2016.
 */
abstract class CardWidget<T extends CardDisplayable> extends Widget<T> {

  protected String socialAction = "(blank)";
  TextView shareButton;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private LinearLayout like;
  private LikeButtonView likeButton;
  private TextView comment;
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

  @CallSuper @Override public void bindView(T displayable) {
    final OkHttpClient httpClient =
        ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    bodyInterceptor = ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    accountNavigator = new AccountNavigator(getFragmentNavigator(), accountManager);

    compositeSubscription.add(accountManager.accountStatus()
        .doOnNext(account -> updateAccount(account))
        .subscribe());
    like.setVisibility(View.VISIBLE);
    comment.setVisibility(View.VISIBLE);

    compositeSubscription.add(RxView.clicks(like)
        .subscribe(click -> {
          if (hasSocialPermissions(Analytics.Account.AccountOrigins.LIKE_CARD)) {
            likeButton.performClick();
          }
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));

    compositeSubscription.add(RxView.clicks(likeButton)
        .subscribe(click -> {
          shareCardWithoutPreview(displayable, (String cardId) -> {
            likeCard(displayable, cardId, 1);
            showSuccessShareSnackBar();
          });
          socialAction = "Like";
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));

    compositeSubscription.add(RxView.clicks(comment)
        .subscribe(click -> {
          if (hasSocialPermissions(Analytics.Account.AccountOrigins.SHARE_CARD)) {
            FragmentManager fm = getContext().getSupportFragmentManager();
            CommentDialogFragment commentDialogFragment =
                CommentDialogFragment.newInstanceTimelineArticleComment(
                    displayable.getTimelineCard()
                        .getCardId());
            commentDialogFragment.setCommentBeforeSubmissionCallbackContract(
                (inputText) -> shareCardWithoutPreview(displayable,
                    cardId -> PostCommentForTimelineArticle.of(cardId, inputText, bodyInterceptor,
                        httpClient, converterFactory,
                        ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator(),
                        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences())
                        .observe()
                        .subscribe(setComment -> {
                          if (!setComment.getData()
                              .getBody()
                              .isEmpty() || !("".equals(setComment.getData()
                              .getBody()))) {
                            showSuccessShareSnackBar();
                          }
                        }, throwable -> CrashReport.getInstance()
                            .log(throwable))));
            commentDialogFragment.show(fm, "fragment_comment_dialog");
            socialAction = "Comment";
          }
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));
    compositeSubscription.add(RxView.clicks(shareButton)
        .subscribe(click -> {
          shareCard(displayable, displayable.getTimelineCard()
              .getCardId(), null, SharePreviewDialog.SharePreviewOpenMode.SHARE);
          socialAction = "Share";
        }, err -> CrashReport.getInstance()
            .log(err)));
  }

  private void showSuccessShareSnackBar() {
    ShowMessage.asSnack(getContext(), R.string.social_timeline_share_dialog_title);
  }

  private void updateAccount(Account account) {
    this.account = account;
  }

  protected void shareCardWithoutPreview(T displayable, ShareCardCallback callback) {
    if (hasSocialPermissions(Analytics.Account.AccountOrigins.SHARE_CARD)) {
      displayable.share(displayable.getTimelineCard()
          .getCardId(), callback, getContext().getResources());
    }
  }

  protected void shareCard(T displayable, String cardId, ShareCardCallback callback,
      SharePreviewDialog.SharePreviewOpenMode openMode) {
    if (!hasSocialPermissions(Analytics.Account.AccountOrigins.SHARE_CARD)) return;
    if (!accountManager.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> accountNavigator.navigateToAccountView(
              Analytics.Account.AccountOrigins.SHARE_CARD), Snackbar.LENGTH_SHORT);
      return;
    }

    if (account != null && !account.hasStore() && !account.isPublicUser()) {
      ShowMessage.asSnack(getContext(), R.string.private_profile_create_store,
          R.string.create_store_create, snackView -> {
            getFragmentNavigator().navigateTo(
                ManageStoreFragment.newInstance(new ManageStoreFragment.ViewModel(), false));
          }, Snackbar.LENGTH_SHORT);
      return;
    }

    SharePreviewDialog sharePreviewDialog =
        new SharePreviewDialog(displayable, accountManager, true, openMode,
            displayable.getTimelineAnalytics(),
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
    AlertDialog.Builder alertDialog = sharePreviewDialog.getPreviewDialogBuilder(getContext());

    Observable.create((Subscriber<? super GenericDialogs.EResponse> subscriber) -> {
      if (!accountManager.isAccountAccessConfirmed()) {
        alertDialog.setPositiveButton(R.string.share, (dialogInterface, i) -> {
          displayable.share(cardId, sharePreviewDialog.getPrivacyResult(), callback,
              getContext().getResources());
          subscriber.onNext(GenericDialogs.EResponse.YES);
          subscriber.onCompleted();
        })
            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
              subscriber.onNext(GenericDialogs.EResponse.NO);
              subscriber.onCompleted();
            });
      } else {
        alertDialog.setPositiveButton(R.string.continue_option, (dialogInterface, i) -> {
          displayable.share(cardId, callback, getContext().getResources());
          subscriber.onNext(GenericDialogs.EResponse.YES);
          subscriber.onCompleted();
        })
            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
              subscriber.onNext(GenericDialogs.EResponse.NO);
              subscriber.onCompleted();
            });
      }
      alertDialog.show();
    })
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(eResponse -> {
          switch (eResponse) {
            case YES:
              showSuccessShareSnackBar();
              displayable.sendSocialActionEvent(
                  TimelineAnalytics.SOCIAL_CARD_ACTION_SHARE_CONTINUE);
              break;
            case NO:
              break;
            case CANCEL:
              displayable.sendSocialActionEvent(TimelineAnalytics.SOCIAL_CARD_ACTION_SHARE_CANCEL);
              break;
          }
        });
  }

  protected boolean hasSocialPermissions(Analytics.Account.AccountOrigins accountOrigins) {
    if (!accountManager.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> accountNavigator.navigateToAccountView(accountOrigins),
          Snackbar.LENGTH_SHORT);
      return false;
    }

    if (account != null && !account.hasStore() && !account.isPublicUser()) {
      ShowMessage.asSnack(getContext(), R.string.private_profile_create_store,
          R.string.create_store_create, snackView -> {
            getFragmentNavigator().navigateTo(
                ManageStoreFragment.newInstance(new ManageStoreFragment.ViewModel(), false));
          }, Snackbar.LENGTH_SHORT);
      return false;
    }
    return true;
  }

  protected boolean likeCard(T displayable, String cardId, int rating) {
    if (!hasSocialPermissions(Analytics.Account.AccountOrigins.LIKE_CARD)) return false;
    displayable.like(getContext(), cardId, getCardTypeName().toUpperCase(), rating,
        getContext().getResources());

    return true;
  }

  abstract String getCardTypeName();

  private Account getAccount() {
    return account;
  }

  protected void knockWithSixpackCredentials(String url) {
    if (url == null) {
      return;
    }

    String credential = Credentials.basic(BuildConfig.SIXPACK_USER, BuildConfig.SIXPACK_PASSWORD);

    OkHttpClient client = new OkHttpClient();

    Request click = new Request.Builder().url(url)
        .addHeader("authorization", credential)
        .build();

    client.newCall(click)
        .enqueue(new Callback() {
          @Override public void onFailure(Call call, IOException e) {
            Logger.d(this.getClass()
                .getSimpleName(), "sixpack request fail " + call.toString());
          }

          @Override public void onResponse(Call call, Response response) throws IOException {
            Logger.d(this.getClass()
                .getSimpleName(), "sixpack knock success");
            response.body()
                .close();
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
    layoutParams.setMargins(displayable.getMarginWidth(getContext(), getContext().getResources()
        .getConfiguration().orientation), 0, displayable.getMarginWidth(getContext(),
        getContext().getResources()
            .getConfiguration().orientation), 15);
    cardView.setLayoutParams(layoutParams);
  }
}
