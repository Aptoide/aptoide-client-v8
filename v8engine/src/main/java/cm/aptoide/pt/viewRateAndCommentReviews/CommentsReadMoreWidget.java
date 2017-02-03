/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/08/2016.
 */

package cm.aptoide.pt.viewRateAndCommentReviews;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 8/5/16.
 */
public class CommentsReadMoreWidget extends Widget<CommentsReadMoreDisplayable> {

  private AptoideClientUUID aptoideClientUUID;
  private AptoideAccountManager accountManager;
  private TextView readMoreButton;

  public CommentsReadMoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    readMoreButton = (TextView) itemView.findViewById(R.id.read_more_button);
  }

  @Override public void bindView(CommentsReadMoreDisplayable displayable) {
    aptoideClientUUID = new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
        DataProvider.getContext());
    accountManager = AptoideAccountManager.getInstance();
    compositeSubscription.add(RxView.clicks(readMoreButton).subscribe(aVoid -> {
      ListCommentsRequest.of(displayable.getResourceId(), displayable.getNext(), 100,
          accountManager.getAccessToken(), aptoideClientUUID.getAptoideClientUUID(),
          displayable.isReview())
          .execute(listComments -> displayable.getCommentAdder()
              .addComment(listComments.getDatalist().getList()));
    }));
  }
}
