package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.crashreports.CrashReports;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 8/4/16.
 */
public class CommentWidget extends Widget<CommentDisplayable> {

  private View replyLayout;
  private ImageView userAvatar;
  private TextView userName;
  private TextView date;
  private TextView comment;

  public CommentWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    userAvatar = (ImageView) itemView.findViewById(R.id.user_icon);
    userName = (TextView) itemView.findViewById(R.id.user_name);
    date = (TextView) itemView.findViewById(R.id.added_date);
    comment = (TextView) itemView.findViewById(R.id.comment);
    replyLayout = itemView.findViewById(R.id.reply_layout);
  }

  @Override public void bindView(CommentDisplayable displayable) {
    Comment comment = displayable.getPojo();
    ImageLoader.loadWithCircleTransformAndPlaceHolderAvatarSize(comment.getUser().getAvatar(),
        userAvatar, R.drawable.layer_1);
    userName.setText(comment.getUser().getName());
    date.setText(AptoideUtils.DateTimeU.getInstance()
        .getTimeDiffString(getContext(), comment.getAdded().getTime()));
    this.comment.setText(comment.getBody());

    if (StoreTabGridRecyclerFragment.StoreComment.class.isAssignableFrom(comment.getClass())) {
      final StoreTabGridRecyclerFragment.StoreComment storeComment =
          (StoreTabGridRecyclerFragment.StoreComment) comment;
      replyLayout.setVisibility(View.VISIBLE);

      compositeSubscription.add(RxView.clicks(replyLayout)
          .flatMap(aVoid -> storeComment.observeReplySubmission()
              .doOnError( err -> {
                CrashReports.logException(err);
                ShowMessage.asSnack(userAvatar, R.string.error_occured);
              })
              .flatMap(v -> ShowMessage.asObservableSnack(userAvatar,R.string.comment_submitted))
          )
          .retry()
          .subscribe(aVoid -> { /* nothing else to do */ }));
    }
  }
}
