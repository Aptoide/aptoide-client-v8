package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.SocialCardDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import rx.Observable;
import rx.functions.Action1;

abstract class SocialCardWidget<T extends SocialCardDisplayable> extends CardWidget<T> {

  private static final String TAG = SocialCardWidget.class.getName();
  private final LayoutInflater inflater;
  private TextView comments;
  private LinearLayout like;
  private LikeButton likeButton;
  private TextView numberLikes;
  private TextView numberComments;
  private TextView sharedBy;
  private TextView time;
  private RelativeLayout likePreviewContainer;

  SocialCardWidget(View itemView) {
    super(itemView);
    inflater = LayoutInflater.from(itemView.getContext());
  }

  @Override @CallSuper protected void assignViews(View itemView) {
    super.assignViews(itemView);
    time = (TextView) itemView.findViewById(R.id.card_date);
    comments = (TextView) itemView.findViewById(R.id.social_comment);
    like = (LinearLayout) itemView.findViewById(R.id.social_like);
    likeButton = (LikeButton) itemView.findViewById(R.id.social_like_test);
    numberLikes = (TextView) itemView.findViewById(R.id.social_number_of_likes);
    numberComments = (TextView) itemView.findViewById(R.id.social_number_of_comments);
    sharedBy = (TextView) itemView.findViewById(R.id.social_shared_by);
    likePreviewContainer = (RelativeLayout) itemView.findViewById(
        R.id.displayable_social_timeline_likes_preview_container);
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
      //compositeSubscription.add(
      //    RxView.clicks(like).flatMap(aVoid -> toggleLike()).subscribe(aVoid -> {
      //    }, showError()));

      likeButton.setOnLikeListener(new OnLikeListener() {
        @Override public void liked(LikeButton likeButton) {
          likeCard(displayable, 1);
          numberLikes.setText(String.valueOf(displayable.getNumberOfLikes() + 1));
        }

        @Override public void unLiked(LikeButton likeButton) {
          likeButton.setLiked(true);
        }
      });

      like.setVisibility(View.VISIBLE);
    } else {
      Logger.w(TAG, "like button is null in this view");
    }

    likeButton.setLiked(false);
    numberLikes.setVisibility(View.VISIBLE);
    numberLikes.setText(String.valueOf(displayable.getNumberOfLikes()));
    numberComments.setVisibility(View.VISIBLE);
    numberComments.setText(String.valueOf(displayable.getNumberOfComments()));

    shareButton.setVisibility(View.VISIBLE);

    //
    // should this be inside the like button logic ??
    //
    likeButton.setOnLikeListener(new OnLikeListener() {
      @Override public void liked(LikeButton likeButton) {
        likeCard(displayable, 1);
        numberLikes.setText(String.valueOf(displayable.getNumberOfLikes() + 1));
      }

      @Override public void unLiked(LikeButton likeButton) {
        likeButton.setLiked(true);
        //likeCard(displayable, cardType, -1);
        //numberLikes.setText("0");
      }
    });

    View likeUserPreviewView;
    ImageView likeUserPreviewIcon;

    likeUserPreviewView =
        inflater.inflate(R.layout.social_timeline_like_user_preview, likePreviewContainer, false);
    View likeUserPreviewView1 =
        inflater.inflate(R.layout.social_timeline_like_user_preview, likePreviewContainer, false);
    View likeUserPreviewView2 =
        inflater.inflate(R.layout.social_timeline_like_user_preview, likePreviewContainer, false);
    View likeUserPreviewView3 =
        inflater.inflate(R.layout.social_timeline_like_user_preview, likePreviewContainer, false);

    likeUserPreviewIcon =
        (ImageView) likeUserPreviewView.findViewById(R.id.social_timeline_like_user_preview);
    ImageView likeUserPreviewIcon1 =
        (ImageView) likeUserPreviewView1.findViewById(R.id.social_timeline_like_user_preview);
    ImageView likeUserPreviewIcon2 =
        (ImageView) likeUserPreviewView2.findViewById(R.id.social_timeline_like_user_preview);
    ImageView likeUserPreviewIcon3 =
        (ImageView) likeUserPreviewView3.findViewById(R.id.social_timeline_like_user_preview);

    ViewGroup.MarginLayoutParams p =
        (ViewGroup.MarginLayoutParams) likeUserPreviewView.getLayoutParams();
    p.setMargins(60, 0, 0, 0);
    likeUserPreviewView1.requestLayout();

    ViewGroup.MarginLayoutParams p1 =
        (ViewGroup.MarginLayoutParams) likeUserPreviewView1.getLayoutParams();
    p1.setMargins(40, 0, 0, 0);
    likeUserPreviewView1.requestLayout();

    ViewGroup.MarginLayoutParams p2 =
        (ViewGroup.MarginLayoutParams) likeUserPreviewView2.getLayoutParams();
    p2.setMargins(20, 0, 0, 0);
    likeUserPreviewView2.requestLayout();

    ViewGroup.MarginLayoutParams p3 =
        (ViewGroup.MarginLayoutParams) likeUserPreviewView3.getLayoutParams();
    p3.setMargins(0, 0, 0, 0);
    likeUserPreviewView3.requestLayout();

    ImageLoader.loadWithShadowCircleTransform(R.drawable.user_account_grey, likeUserPreviewIcon);
    ImageLoader.loadWithShadowCircleTransform(R.drawable.icon_user, likeUserPreviewIcon1);
    ImageLoader.loadWithShadowCircleTransform(R.drawable.user_account_white, likeUserPreviewIcon2);
    ImageLoader.loadWithShadowCircleTransform(R.drawable.user_default, likeUserPreviewIcon3);

    likePreviewContainer.addView(likeUserPreviewView);
    likePreviewContainer.addView(likeUserPreviewView1);
    likePreviewContainer.addView(likeUserPreviewView2);
    likePreviewContainer.addView(likeUserPreviewView3);

    compositeSubscription.add(RxView.clicks(likePreviewContainer)
        .subscribe(click -> displayable.likesPreviewClick(((FragmentShower) getContext())),
            (throwable) -> {
              throwable.printStackTrace();
            }));
  }

  @NonNull private Action1<Throwable> showError() {
    return err -> {
      Logger.e(TAG, err);
      CrashReports.logException(err);
    };
  }

  void likeCard(T displayable, int rating) {
    if (!AptoideAccountManager.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            AptoideAccountManager.openAccountManager(snackView.getContext());
          });
      return;
    }
    displayable.like(getContext(), getCardTypeName().toUpperCase(), rating);
  }

  Observable<Void> showComments(T displayable) {
    return Observable.fromCallable(() -> {
      final String elementId = displayable.getTimelineCard().getCardId();
      Fragment fragment = V8Engine.getFragmentProvider()
          .newCommentGridRecyclerFragment(CommentType.TIMELINE, elementId);
      ((FragmentShower) getContext()).pushFragmentV4(fragment);
      return null;
    });
  }
}
