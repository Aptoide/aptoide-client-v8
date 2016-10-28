/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.model.v7.Review;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentsReadMoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 8/5/16.
 */
public class CommentsReadMoreWidget extends Widget<CommentsReadMoreDisplayable> {

  private TextView readMoreButton;

  public CommentsReadMoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    readMoreButton = (TextView) itemView.findViewById(R.id.read_more_button);
  }

  @Override public void bindView(CommentsReadMoreDisplayable displayable) {
    Review review = displayable.getPojo();
    compositeSubscription.add(RxView.clicks(readMoreButton).subscribe(aVoid -> {
      ListCommentsRequest.of(review.getId(), displayable.getNext(), 100,
          AptoideAccountManager.getAccessToken(), AptoideAccountManager.getUserEmail(),
          new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
              DataProvider.getContext()).getAptoideClientUUID())
          .execute(listComments -> displayable.getCommentAdder()
              .addComment(listComments.getDatalist().getList()));
    }));
  }
}
