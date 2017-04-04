package cm.aptoide.pt.v8engine.view.timeline.widget;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.model.v7.timeline.UserTimeline;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.timeline.LikeButtonView;
import cm.aptoide.pt.v8engine.view.store.StoreFragment;
import cm.aptoide.pt.v8engine.view.timeline.displayable.SocialCardDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

abstract class SocialCardWidget<T extends SocialCardDisplayable> extends CardWidget<T> {

  private static final String TAG = SocialCardWidget.class.getName();
  private final LayoutInflater inflater;
  protected ImageView userAvatar;
  protected ImageView storeAvatar;
  private TextView comments;
  private LinearLayout like;
  private LikeButtonView likeButton;
  private TextView numberLikes;
  private TextView numberComments;
  private TextView sharedBy;
  private TextView time;
  private RelativeLayout likePreviewContainer;
  private int marginOfTheNextLikePreview;
  private AptoideAccountManager accountManager;
  private AccountNavigator accountNavigator;

  SocialCardWidget(View itemView) {
    super(itemView);
    inflater = LayoutInflater.from(itemView.getContext());
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
    likePreviewContainer = (RelativeLayout) itemView.findViewById(
        R.id.displayable_social_timeline_likes_preview_container);
    storeAvatar = (ImageView) itemView.findViewById(R.id.card_image);
    userAvatar = (ImageView) itemView.findViewById(R.id.card_user_avatar);
  }

  @Override @CallSuper public void bindView(T displayable) {
    super.bindView(displayable);
    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator());

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
      compositeSubscription.add(RxView.clicks(comments)
          .flatMap(aVoid -> showComments(displayable))
          .subscribe(aVoid -> knockWithSixpackCredentials(displayable.getAbUrl()), showError()));

      comments.setVisibility(View.VISIBLE);
    } else {
      Logger.w(TAG, "comment button is null in this view");
    }

    time.setText(displayable.getTimeSinceLastUpdate(getContext()));

    if (like != null) {
      like.setOnClickListener(view -> likeButton.performClick());

      if (displayable.isLiked()) {
        likeButton.setHeartState(true);
      } else {
        likeButton.setHeartState(false);
      }

      compositeSubscription.add(RxView.clicks(likeButton)
          .flatMap(__ -> accountManager.accountStatus().first().toSingle().toObservable())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(account -> {
            if (likeCard(displayable, 1, account)) {
              numberLikes.setText(String.valueOf(displayable.getNumberOfLikes() + 1));
              numberLikes.setVisibility(View.VISIBLE);
              if (likePreviewContainer.getChildCount() < 4) {
                if (!displayable.isLiked()) {
                  knockWithSixpackCredentials(displayable.getAbUrl());
                  UserTimeline user = new UserTimeline();
                  Store store = new Store();
                  store.setAvatar(account.getStoreAvatar());
                  user.setAvatar(account.getAvatar());
                  user.setStore(store);
                  addUserToPreview(marginOfTheNextLikePreview, user);
                  likePreviewContainer.invalidate();
                }
              }
            }
          }, err -> CrashReport.getInstance().log(err)));

      like.setVisibility(View.VISIBLE);
    } else {
      Logger.w(TAG, "like button is null in this view");
    }

    if (displayable.getNumberOfLikes() > 0) {
      numberLikes.setVisibility(View.VISIBLE);
      numberLikes.setText(String.valueOf(displayable.getNumberOfLikes()));
    } else {
      numberLikes.setVisibility(View.INVISIBLE);
    }

    if (displayable.getNumberOfComments() > 0) {
      numberComments.setVisibility(View.VISIBLE);
      numberComments.setText(String.valueOf(displayable.getNumberOfComments()));
    } else {
      numberComments.setVisibility(View.INVISIBLE);
    }

    shareButton.setVisibility(View.VISIBLE);

    likePreviewContainer.removeAllViews();
    showLikesPreview(displayable);

    compositeSubscription.add(RxView.clicks(likePreviewContainer)
        .subscribe(click -> displayable.likesPreviewClick(getFragmentNavigator()),
            err -> CrashReport.getInstance().log(err)));

    compositeSubscription.add(
        Observable.merge(RxView.clicks(storeAvatar), RxView.clicks(userAvatar)).subscribe(click -> {
          if (displayable.getStore() == null) {
            openStore(displayable.getUser().getId(), "DEFAULT");
          } else {
            openStore(displayable.getStore().getName(),
                displayable.getStore().getAppearance().getTheme());
          }
        }));
  }

  private Observable<Void> showComments(T displayable) {
    return Observable.fromCallable(() -> {
      final String elementId = displayable.getTimelineCard().getCardId();
      Fragment fragment = V8Engine.getFragmentProvider()
          .newCommentGridRecyclerFragment(CommentType.TIMELINE, elementId);
      getFragmentNavigator().navigateTo(fragment);
      return null;
    });
  }

  @NonNull private Action1<Throwable> showError() {
    return err -> CrashReport.getInstance().log(err);
  }

  private boolean likeCard(T displayable, int rating, Account account) {
    if (!account.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            accountNavigator.navigateToAccountView();
          });
      return false;
    }
    displayable.like(getContext(), getCardTypeName().toUpperCase(), rating);
    return true;
  }

  private void addUserToPreview(int i, UserTimeline user) {
    View likeUserPreviewView;
    ImageView likeUserPreviewIcon;
    likeUserPreviewView =
        inflater.inflate(R.layout.social_timeline_like_user_preview, likePreviewContainer, false);
    likeUserPreviewIcon =
        (ImageView) likeUserPreviewView.findViewById(R.id.social_timeline_like_user_preview);
    ViewGroup.MarginLayoutParams p =
        (ViewGroup.MarginLayoutParams) likeUserPreviewView.getLayoutParams();
    p.setMargins(i, 0, 0, 0);
    likeUserPreviewView.requestLayout();

    if (user != null) {
      final FragmentActivity context = getContext();
      if (user.getAvatar() != null) {
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(user.getAvatar(), likeUserPreviewIcon);
      } else if (user.getStore().getAvatar() != null) {
        ImageLoader.with(context)
            .loadWithShadowCircleTransform(user.getStore().getAvatar(), likeUserPreviewIcon);
      }
      likePreviewContainer.addView(likeUserPreviewView);
      marginOfTheNextLikePreview -= 20;
    }
  }

  private void showLikesPreview(T displayable) {
    marginOfTheNextLikePreview = 60;
    for (int j = 0; j < displayable.getNumberOfLikes(); j++) {

      UserTimeline user = null;
      if (displayable.getUserLikes() != null && j < displayable.getUserLikes().size()) {
        user = displayable.getUserLikes().get(j);
      }
      addUserToPreview(marginOfTheNextLikePreview, user);
      if (marginOfTheNextLikePreview < 0) {
        break;
      }
    }
  }

  private void openStore(long userId, String storeTheme) {
    getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
        .newStoreFragment(userId, storeTheme, Event.Name.getUserTimeline,
            StoreFragment.OpenType.GetHome));
  }

  private void openStore(String storeName, String storeTheme) {
    getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
        .newStoreFragment(storeName, storeTheme, Event.Name.getUserTimeline,
            StoreFragment.OpenType.GetHome));
  }
}
