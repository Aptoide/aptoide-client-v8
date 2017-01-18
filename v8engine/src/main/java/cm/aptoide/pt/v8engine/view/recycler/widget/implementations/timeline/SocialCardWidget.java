package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.animations.LikeButtonView;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialCardDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.functions.Action1;

abstract class SocialCardWidget<T extends SocialCardDisplayable> extends CardWidget<T> {

  private static final String TAG = SocialCardWidget.class.getName();

  private TextView comments;
  private LinearLayout like;
  private LikeButtonView likeButton;
  private TextView numberLikes;
  private TextView numberComments;
  private TextView sharedBy;
  private TextView time;

  SocialCardWidget(View itemView) {
    super(itemView);
  }

  @Override @CallSuper protected void assignViews(View itemView) {
    super.assignViews(itemView);
    time = (TextView) itemView.findViewById(R.id.card_date);
    comments = (TextView) itemView.findViewById(R.id.social_comment);
    like = (LinearLayout) itemView.findViewById(R.id.social_like);
    likeButton = (LikeButtonView) itemView.findViewById(R.id.social_like_button);
    numberLikes = (TextView) itemView.findViewById(R.id.social_number_of_likes);
    numberComments = (TextView) itemView.findViewById(R.id.social_number_of_comments);
    sharedBy = (TextView) itemView.findViewById(R.id.social_shared_by);
  }

  @Override @CallSuper public void bindView(T displayable) {
    super.bindView(displayable);

    if (displayable.getUserSharer() != null) {
      if (displayable.getUserSharer().getName() != null && !displayable.getUser()
          .getName()
          .equals(displayable.getUserSharer().getName())) {
        sharedBy.setVisibility(View.VISIBLE);
        sharedBy.setText(
            displayable.getSharedBy(getContext(), displayable.getUserSharer().getName()));
      } else {
        sharedBy.setVisibility(View.GONE);
      }
    } else {
      sharedBy.setVisibility(View.GONE);
    }

    if (comments != null) {
      compositeSubscription.add(
          RxView.clicks(comments).flatMap(aVoid -> showComments(displayable)).subscribe(aVoid -> {
          }, showError()));

      comments.setVisibility(View.VISIBLE);
    } else {
      Logger.w(TAG, "comment button is null in this view");
    }

    time.setText(displayable.getTimeSinceLastUpdate(getContext()));

    if (like != null) {
      like.setOnClickListener(view -> likeButton.performClick());

      likeButton.setOnClickListener(view -> {
        if (likeCard(displayable, 1)) {
          numberLikes.setText(String.valueOf(displayable.getNumberOfLikes() + 1));
          numberLikes.setVisibility(View.VISIBLE);
        }
      });

      like.setVisibility(View.VISIBLE);
    } else {
      Logger.w(TAG, "like button is null in this view");
    }

    if (displayable.getNumberOfLikes() > 0) {
      numberLikes.setVisibility(View.VISIBLE);
      numberLikes.setText(String.valueOf(displayable.getNumberOfLikes()));
    }

    if (displayable.getNumberOfComments() > 0) {
      numberComments.setVisibility(View.VISIBLE);
      numberComments.setText(String.valueOf(displayable.getNumberOfComments()));
    }

    shareButton.setVisibility(View.VISIBLE);
  }

  @NonNull private Action1<Throwable> showError() {
    return err -> {
      Logger.e(TAG, err);
      CrashReports.logException(err);
    };
  }

  private boolean likeCard(T displayable, int rating) {
    if (!AptoideAccountManager.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            AptoideAccountManager.openAccountManager(snackView.getContext());
          });
      return false;
    }
    displayable.like(getContext(), getCardTypeName().toUpperCase(), rating);
    return true;
  }

  private Observable<Void> showComments(T displayable) {
    return Observable.fromCallable(() -> {
      final String elementId = displayable.getTimelineCard().getCardId();
      Fragment fragment = V8Engine.getFragmentProvider()
          .newCommentGridRecyclerFragment(CommentType.TIMELINE, elementId);
      ((FragmentShower) getContext()).pushFragmentV4(fragment);
      return null;
    });
  }
}
