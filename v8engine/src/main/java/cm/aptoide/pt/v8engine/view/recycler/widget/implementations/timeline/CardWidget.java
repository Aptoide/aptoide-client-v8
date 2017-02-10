package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.BaseActivity;
import cm.aptoide.accountmanager.CreateStoreActivity;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.R;
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
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 29/11/2016.
 */

public abstract class CardWidget<T extends CardDisplayable> extends Widget<T> {

  private static final String TAG = CardWidget.class.getName();

  TextView shareButton;

  CardWidget(View itemView) {
    super(itemView);
  }

  @CallSuper @Override protected void assignViews(View itemView) {
    shareButton = (TextView) itemView.findViewById(R.id.social_share);
  }

  @CallSuper @Override public void bindView(T displayable) {
    compositeSubscription.add(RxView.clicks(shareButton)
        //.flatMap(a -> {
        //// FIXME: 20/12/2016 sithengineer remove this flatMap
        //return Observable.fromCallable(() -> {
        //  final String elementId = displayable.getTimelineCard().getCardId();
        //  Fragment fragment = V8Engine.getFragmentProvider()
        //      .newCommentGridRecyclerFragment(CommentType.TIMELINE, elementId);
        //  ((FragmentShower) getContext()).pushFragmentV4(fragment);
        //  return null;
        //});
        //})
        .subscribe(click -> {
          shareCard(displayable);
        }, err -> {
          Logger.e(TAG, err);
        }));
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

  //
  // all cards are "shareable"
  //

  void shareCard(T displayable) {
    if (!AptoideAccountManager.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            AptoideAccountManager.openAccountManager(snackView.getContext());
          });
      return;
    }

    if (TextUtils.isEmpty(AptoideAccountManager.getUserData().getUserRepo())
        && !BaseActivity.UserAccessState.PUBLIC.toString()
        .equals(ManagerPreferences.getUserAccess())) {
      ShowMessage.asSnack(getContext(), R.string.private_profile_create_store,
          R.string.create_store_create, snackView -> {
            Intent intent = new Intent(getContext(), CreateStoreActivity.class);
            getContext().startActivity(intent);
          });
      return;
    }

    SharePreviewDialog sharePreviewDialog = new SharePreviewDialog(displayable);
    AlertDialog.Builder alertDialog = sharePreviewDialog.showPreviewDialog(getContext());

    Observable.create((Subscriber<? super GenericDialogs.EResponse> subscriber) -> {
      if (!ManagerPreferences.getUserAccessConfirmed()) {
        alertDialog.setPositiveButton(R.string.share, (dialogInterface, i) -> {
          displayable.share(getContext(), sharePreviewDialog.getPrivacyResult());
          subscriber.onNext(GenericDialogs.EResponse.YES);
          subscriber.onCompleted();
        }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
          subscriber.onNext(GenericDialogs.EResponse.NO);
          subscriber.onCompleted();
        });
      } else {
        alertDialog.setPositiveButton(R.string.continue_option, (dialogInterface, i) -> {
          displayable.share(getContext(), sharePreviewDialog.getPrivacyResult());
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
          ShowMessage.asSnack(getContext(),R.string.social_timeline_share_dialog_title);
          break;
        case NO:
          break;
        case CANCEL:
          break;
      }
    });
  }
}
