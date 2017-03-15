package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigation.AccountNavigator;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.activity.CreateStoreActivity;
import cm.aptoide.pt.v8engine.dialog.SharePreviewDialog;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.CardDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jdandrade on 29/11/2016.
 */

public abstract class CardWidget<T extends CardDisplayable> extends Widget<T> {

  private static final String TAG = CardWidget.class.getName();

  TextView shareButton;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;
  private AlertDialog alertDialog;
  private Account account;

  CardWidget(View itemView) {
    super(itemView);
  }

  @CallSuper @Override protected void assignViews(View itemView) {
    shareButton = (TextView) itemView.findViewById(R.id.social_share);
  }

  @Override public void unbindView() {
    if (alertDialog != null && alertDialog.isShowing()) {
      alertDialog.dismiss();
    }
    super.unbindView();
  }

  @CallSuper @Override public void bindView(T displayable) {
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    accountNavigator = new AccountNavigator(getContext(), getNavigationManager(), accountManager);

    compositeSubscription.add(
        accountManager.accountStatus().doOnNext(account -> updateAccount(account)).subscribe());

    compositeSubscription.add(RxView.clicks(shareButton)
        .doOnNext(click -> shareCard(displayable, getAccount()))
        .subscribe());
  }

  private void updateAccount(Account account) {
    this.account = account;
  }

  private void shareCard(T displayable, Account account) {
    if (!account.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> accountNavigator.navigateToAccountView());
      return;
    }

    if (TextUtils.isEmpty(account.getStore()) && !Account.Access.PUBLIC.equals(
        account.getAccess())) {
      ShowMessage.asSnack(getContext(), R.string.private_profile_create_store,
          R.string.create_store_create, snackView -> {
            Intent intent = new Intent(getContext(), CreateStoreActivity.class);
            getContext().startActivity(intent);
          });
      return;
    }

    final SharePreviewDialog sharePreviewDialog =
        new SharePreviewDialog(displayable, accountManager, true);

    final AlertDialog.Builder alertDialogBuilder =
        sharePreviewDialog.getPreviewDialogBuilder(getContext());

    if (!account.isAccessConfirmed()) {
      alertDialogBuilder.setPositiveButton(R.string.share, (dialogInterface, i) -> {
        displayable.share(getContext(), sharePreviewDialog.getPrivacyResult());
        ShowMessage.asSnack(getContext(), R.string.social_timeline_share_dialog_title);
      }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
        // does nothing
      });
    } else {
      alertDialogBuilder.setPositiveButton(R.string.continue_option, (dialogInterface, i) -> {
        displayable.share(getContext(), sharePreviewDialog.getPrivacyResult());
        ShowMessage.asSnack(getContext(), R.string.social_timeline_share_dialog_title);
      }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
        // does nothing
      });
    }
    alertDialog = alertDialogBuilder.show();
  }

  private Account getAccount() {
    return account;
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
