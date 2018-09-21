package cm.aptoide.pt.comments.view;

import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Dimension;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.comments.ComplexComment;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 8/4/16.
 */
public class StoreCommentWidget extends Widget<StoreCommentDisplayable> {

  private static final int MARGIN_IN_DIP = 15;

  private View outerLayout;
  private ImageView userAvatar;
  private TextView userName;
  private TextView date;
  private TextView comment;
  private TextView replies;

  public StoreCommentWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View view) {
    userAvatar = view.findViewById(R.id.user_icon);
    outerLayout = view.findViewById(R.id.outer_layout);
    userName = view.findViewById(R.id.user_name);
    date = view.findViewById(R.id.date);
    comment = view.findViewById(R.id.comment);
    replies = view.findViewById(R.id.replies);
  }

  @Override public void bindView(StoreCommentDisplayable displayable) {
    Comment comment = displayable.getComment();

    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .loadWithCircleTransformAndPlaceHolderAvatarSize(comment.getUser()
            .getAvatar(), userAvatar, R.drawable.layer_1);
    userName.setText(comment.getUser()
        .getName());

    String date = AptoideUtils.DateTimeU.getInstance(getContext())
        .getTimeDiffString(context, comment.getAdded()
            .getTime(), getContext().getResources());
    this.date.setText(date);

    this.comment.setText(comment.getBody());

    this.date.setVisibility(View.VISIBLE);

    Comment.Stats stats = comment.getStats();
    if (stats != null && stats.getComments() > 0) {
      String repliesText =
          String.format(getContext().getString(R.string.comment_replies_number_short),
              stats.getComments());
      this.replies.setText(repliesText);
      this.replies.setVisibility(View.VISIBLE);
    } else {
      this.replies.setVisibility(View.INVISIBLE);
    }

    compositeSubscription.add(RxView.clicks(itemView)
        .doOnNext(click -> displayable.itemClicked(itemView))
        .subscribe(__ -> {
        }, throwable -> CrashReport.getInstance()
            .log(throwable)));
  }

  private void bindComplexComment(ComplexComment comment) {
    final ComplexComment complexComment = comment;

    // switch background color according to level
    @ColorRes int bgColor = (complexComment.getLevel() == 1) ? R.color.white : R.color.comment_gray;
    final FragmentActivity context = getContext();
    int color;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      color = context.getColor(bgColor);
    } else {
      color = context.getResources()
          .getColor(bgColor);
    }
    outerLayout.setBackgroundColor(color);

    // set left/start margin width in default comment
    //setLayoutLeftPadding(complexComment);

    //if (complexComment.getLevel() == 1) {
    //  // first level
    //  replyLayout.setVisibility(View.VISIBLE);
    //  compositeSubscription.add(RxView.clicks(replyLayout)
    //      .flatMap(aVoid -> complexComment.observeReplySubmission()
    //          .doOnError(err -> {
    //            Snackbar.make(userAvatar, R.string.error_occured, Snackbar.LENGTH_SHORT);
    //          })
    //          .toObservable())
    //      .retry()
    //      .subscribe(aVoid -> { /* nothing else to do */ }, err -> {
    //        CrashReport.getInstance()
    //            .log(err);
    //      }));
    //} else {
    //  // other levels
    //  replyLayout.setVisibility(View.GONE);
    //
    //  userAvatar.setScaleX(0.7F);
    //  userAvatar.setScaleY(0.7F);
    //}
  }

  private void setLayoutLeftPadding(ComplexComment complexComment) {
    final int level = complexComment.getLevel();
    int baseMargin =
        AptoideUtils.ScreenU.getPixelsForDip(MARGIN_IN_DIP, getContext().getResources());
    @Dimension int leftMargin = level < 2 ? baseMargin : baseMargin * level;
    outerLayout.setPadding(leftMargin, outerLayout.getPaddingTop(), baseMargin,
        outerLayout.getPaddingBottom());
  }
}
