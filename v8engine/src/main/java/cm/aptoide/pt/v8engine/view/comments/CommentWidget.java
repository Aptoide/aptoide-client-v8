package cm.aptoide.pt.v8engine.view.comments;

import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Dimension;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.comments.ComplexComment;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 8/4/16.
 */
public class CommentWidget extends Widget<CommentDisplayable> {

  private static final String TAG = CommentWidget.class.getName();
  private static final int MARGIN_IN_DIP = 15;

  private View replyLayout;
  private View outerLayout;
  private ImageView userAvatar;
  private TextView userName;
  private TextView datePos1;
  private TextView datePos2;
  private TextView comment;

  public CommentWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    userAvatar = (ImageView) itemView.findViewById(R.id.user_icon);
    outerLayout = itemView.findViewById(R.id.outer_layout);
    userName = (TextView) itemView.findViewById(R.id.user_name);
    datePos1 = (TextView) itemView.findViewById(R.id.added_date_pos1);
    datePos2 = (TextView) itemView.findViewById(R.id.added_date_pos2);
    comment = (TextView) itemView.findViewById(R.id.comment);
    replyLayout = itemView.findViewById(R.id.reply_layout);
  }

  @Override public void bindView(CommentDisplayable displayable) {
    Comment comment = displayable.getComment();

    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .loadWithCircleTransformAndPlaceHolderAvatarSize(comment.getUser().getAvatar(), userAvatar,
            R.drawable.layer_1);
    userName.setText(comment.getUser().getName());

    String date = AptoideUtils.DateTimeU.getInstance()
        .getTimeDiffString(context, comment.getAdded().getTime());
    datePos1.setText(date);
    datePos2.setText(date);

    this.comment.setText(comment.getBody());

    if (ComplexComment.class.isAssignableFrom(comment.getClass())) {
      datePos2.setVisibility(View.VISIBLE);
      bindComplexComment((ComplexComment) comment);
    } else {
      datePos1.setVisibility(View.VISIBLE);
    }
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
      color = context.getResources().getColor(bgColor);
    }
    outerLayout.setBackgroundColor(color);

    // set left/start margin width in default comment
    setLayoutLeftPadding(complexComment);

    if (complexComment.getLevel() == 1) {
      // first level
      replyLayout.setVisibility(View.VISIBLE);
      compositeSubscription.add(RxView.clicks(replyLayout)
          .flatMap(aVoid -> complexComment.observeReplySubmission().doOnError(err -> {
            ShowMessage.asSnack(userAvatar, R.string.error_occured);
          }))
          .retry()
          .subscribe(aVoid -> { /* nothing else to do */ }, err -> {
            CrashReport.getInstance().log(err);
          }));
    } else {
      // other levels
      replyLayout.setVisibility(View.GONE);
      userAvatar.setScaleX(0.7F);
      userAvatar.setScaleY(0.7F);
    }
  }

  private void setLayoutLeftPadding(ComplexComment complexComment) {
    final int level = complexComment.getLevel();
    int baseMargin = AptoideUtils.ScreenU.getPixelsForDip(MARGIN_IN_DIP);
    @Dimension int leftMargin = level < 2 ? baseMargin : baseMargin * level;
    outerLayout.setPadding(leftMargin, outerLayout.getPaddingTop(), baseMargin,
        outerLayout.getPaddingBottom());
  }
}
