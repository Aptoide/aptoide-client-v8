package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.functions.Action1;

abstract class SocialCardWidget<T extends SocialCardDisplayable> extends CardWidget<T> {

  private static final String TAG = SocialCardWidget.class.getName();

  private LinearLayout comments;
  private LinearLayout like;

  SocialCardWidget(View itemView) {
    super(itemView);
  }

  @Override @CallSuper protected void assignViews(View itemView) {
    comments = (LinearLayout) itemView.findViewById(R.id.social_comment);
    like = (LinearLayout) itemView.findViewById(R.id.social_like);
  }

  @Override @CallSuper public void bindView(T displayable) {
    if (comments != null) {
      compositeSubscription.add(
          RxView.clicks(comments).flatMap(aVoid -> showComments(displayable)).subscribe(aVoid -> {
          }, showError()));

      comments.setVisibility(View.VISIBLE);
    } else {
      Logger.w(TAG, "comment button is null in this view");
    }

    if (like != null) {
      compositeSubscription.add(
          RxView.clicks(like).flatMap(aVoid -> toggleLike()).subscribe(aVoid -> {
          }, showError()));

      like.setVisibility(View.VISIBLE);
    } else {
      Logger.w(TAG, "like button is null in this view");
    }
  }

  @NonNull private Action1<Throwable> showError() {
    return err -> {
      Logger.e(TAG, err);
      CrashReports.logException(err);
    };
  }

  void likeCard(T displayable, String cardType, int rating) {
    if (!AptoideAccountManager.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            AptoideAccountManager.openAccountManager(snackView.getContext());
          });
      return;
    }
    displayable.like(getContext(), cardType.toUpperCase(), rating);
  }

  Observable<Void> showComments(T displayable) {
    return Observable.fromCallable(() -> {
      // TODO: 19/12/2016 sithengineer

      ShowMessage.asSnack(comments, "TO DO: show comments");

      final String elementId = displayable.getTimelineCard().getCardId();
      V8Engine.getFragmentProvider()
          .newCommentGridRecyclerFragment(CommentType.TIMELINE, elementId);

      return null;
    });
  }

  Observable<Void> toggleLike() {
    return Observable.fromCallable(() -> {
      // TODO: 19/12/2016 sithengineer
      ShowMessage.asSnack(comments, "TO DO: like");
      return null;
    });
  }
}
