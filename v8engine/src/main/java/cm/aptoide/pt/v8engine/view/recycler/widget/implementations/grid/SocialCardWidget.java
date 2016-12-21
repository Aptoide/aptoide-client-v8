package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.LinearLayout;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import com.jakewharton.rxbinding.view.RxView;

public abstract class SocialCardWidget<T extends SocialCardDisplayable> extends CardWidget<T> {

  private LinearLayout comments;

  public SocialCardWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    comments = (LinearLayout) itemView.findViewById(R.id.social_comment);
  }

  public void likeCard(T displayable, String cardType, int rating) {
    if (!AptoideAccountManager.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            AptoideAccountManager.openAccountManager(snackView.getContext());
          });
      return;
    }
    displayable.like(getContext(), cardType.toUpperCase(), rating);
  }

  private void showComments(T displayable) {
    compositeSubscription.add(RxView.clicks(comments).subscribe(aVoid -> {
      // TODO
      ShowMessage.asSnack(comments, "TO DO");
    }));
  }
}
