package cm.aptoide.pt.v8engine.view.reviews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.model.v7.ListFullReviews;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayableGroup;
import cm.aptoide.pt.v8engine.view.store.GetStoreEndlessFragment;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by neuro on 26-12-2016.
 */

public class ListReviewsFragment extends GetStoreEndlessFragment<ListFullReviews> {

  private StoreCredentialsProvider storeCredentialsProvider;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    storeCredentialsProvider = new StoreCredentialsProviderImpl();
  }

  @Override
  protected V7<ListFullReviews, ? extends Endless> buildRequest(boolean refresh, String url) {
    return requestFactory.newListFullReviews(url, refresh);
  }

  @Override protected Action1<ListFullReviews> buildAction() {
    return listFullReviews -> {
      if (listFullReviews != null
          && listFullReviews.getDatalist() != null
          && listFullReviews.getDatalist().getList() != null) {
        List<FullReview> reviews = listFullReviews.getDatalist().getList();
        ArrayList<Displayable> displayables = new ArrayList<>(reviews.size());
        for (int i = 0; i < reviews.size(); i++) {
          FullReview review = reviews.get(i);
          displayables.add(new RowReviewDisplayable(review));
        }
        addDisplayable(new DisplayableGroup(displayables));
      }
    };
  }
}
