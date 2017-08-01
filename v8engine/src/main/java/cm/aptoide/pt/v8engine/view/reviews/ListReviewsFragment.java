package cm.aptoide.pt.v8engine.view.reviews;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.model.v7.FullReview;
import cm.aptoide.pt.dataprovider.model.v7.ListFullReviews;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
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
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((V8Engine) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
  }

  @Override
  protected V7<ListFullReviews, ? extends Endless> buildRequest(boolean refresh, String url) {
    return requestFactory.newListFullReviews(url, refresh);
  }

  @Override protected Action1<ListFullReviews> buildAction() {
    return listFullReviews -> {
      if (listFullReviews != null
          && listFullReviews.getDataList() != null
          && listFullReviews.getDataList()
          .getList() != null) {
        List<FullReview> reviews = listFullReviews.getDataList()
            .getList();
        ArrayList<Displayable> displayables = new ArrayList<>(reviews.size());
        for (int i = 0; i < reviews.size(); i++) {
          FullReview review = reviews.get(i);
          displayables.add(new RowReviewDisplayable(review));
        }
        addDisplayable(new DisplayableGroup(displayables,
            (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
            getContext().getResources()));
      }
    };
  }
}
