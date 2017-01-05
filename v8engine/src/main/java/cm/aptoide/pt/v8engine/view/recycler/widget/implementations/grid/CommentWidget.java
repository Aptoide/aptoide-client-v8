package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.os.Build;
import android.support.annotation.ColorRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.viewRateAndCommentReviews.ComplexComment;
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
  private TextView date;
  private TextView comment;

  public CommentWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    userAvatar = (ImageView) itemView.findViewById(R.id.user_icon);
    outerLayout = itemView.findViewById(R.id.outer_layout);
    userName = (TextView) itemView.findViewById(R.id.user_name);
    date = (TextView) itemView.findViewById(R.id.added_date);
    comment = (TextView) itemView.findViewById(R.id.comment);
    replyLayout = itemView.findViewById(R.id.reply_layout);
  }

  @Override public void bindView(CommentDisplayable displayable) {
    Comment comment = displayable.getComment();
    ImageLoader.loadWithCircleTransformAndPlaceHolderAvatarSize(comment.getUser().getAvatar(),
        userAvatar, R.drawable.layer_1);
    userName.setText(comment.getUser().getName());
    date.setText(AptoideUtils.DateTimeU.getInstance()
        .getTimeDiffString(getContext(), comment.getAdded().getTime()));
    this.comment.setText(comment.getBody());

    if (ComplexComment.class.isAssignableFrom(comment.getClass())) {
      final ComplexComment complexComment = (ComplexComment) comment;

      @ColorRes int bgColor = R.color.white;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        outerLayout.setBackgroundColor(getContext().getColor(bgColor));
      } else {
        outerLayout.setBackgroundColor(getContext().getResources().getColor(bgColor));
      }

      // set left/start margin width in default comment
      setLayoutLeftPadding(complexComment);

      if (complexComment.getLevel() == 1) {
        replyLayout.setVisibility(View.VISIBLE);
        compositeSubscription.add(RxView.clicks(replyLayout)
            .flatMap(aVoid -> complexComment.observeReplySubmission().doOnError(err -> {
              ShowMessage.asSnack(userAvatar, R.string.error_occured);
            }))
            .retry()
            .subscribe(aVoid -> { /* nothing else to do */ }, err -> {
              Logger.e(TAG, err);
              CrashReports.logException(err);
            }));
      } else {
        replyLayout.setVisibility(View.GONE);
      }
    }
  }

  private void setLayoutLeftPadding(ComplexComment complexComment) {
    final int level = complexComment.getLevel();

    /*
    outerLayout.setPadding(outerLayout.getPaddingRight(), outerLayout.getPaddingTop(),
        outerLayout.getPaddingRight(), outerLayout.getPaddingBottom());

    int baseMargin = AptoideUtils.ScreenU.getPixels(MARGIN_IN_DIP);
    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) rootView.getLayoutParams();
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
      if (level == 2) {
        params.setMarginStart(baseMargin);
      } else {
        params.setMarginStart(0);
      }
    }
    if (level == 2) {
      params.leftMargin = baseMargin;
    } else {
      params.leftMargin = 0;
    }
    rootView.setLayoutParams(params);
    */

    //int baseMargin = level < 2 ? 0 : AptoideUtils.ScreenU.getPixels(MARGIN_IN_DIP);
    //rootView.setPadding(baseMargin, rootView.getPaddingTop(), rootView.getPaddingRight(),
    //    rootView.getPaddingBottom());

    int baseMargin = AptoideUtils.ScreenU.getPixels(MARGIN_IN_DIP);
    int leftMargin = level < 2 ? baseMargin : baseMargin * level;
    outerLayout.setPadding(leftMargin, outerLayout.getPaddingTop(), baseMargin,
        outerLayout.getPaddingBottom());
  }
}
