package cm.aptoide.pt.reviews;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.model.v7.FullReview;
import cm.aptoide.pt.dataprovider.model.v7.ListFullReviews;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.store.view.GetStoreEndlessFragment;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayableGroup;
import java.util.ArrayList;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by neuro on 26-12-2016.
 */

public class ListReviewsFragment extends GetStoreEndlessFragment<ListFullReviews> {

  private StoreCredentialsProvider storeCredentialsProvider;

  public static Fragment newInstance() {
    return new ListReviewsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    storeCredentialsProvider = new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
        ((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class));
  }

  @Override
  protected V7<ListFullReviews, ? extends Endless> buildRequest(boolean refresh, String url) {
    return requestFactoryCdnPool.newListFullReviews(url, refresh);
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
