package cm.aptoide.pt.search.view;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.search.view.item.SearchLoadingViewHolder;
import cm.aptoide.pt.search.view.item.SearchResultAdViewHolder;
import cm.aptoide.pt.search.view.item.SearchResultViewHolder;
import cm.aptoide.pt.view.ItemView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<ItemView> {

  private final PublishRelay<MinimalAd> onAdClickRelay;
  private final PublishRelay<SearchApp> onItemViewClick;
  private final PublishRelay<Pair<SearchApp, android.view.View>> onOpenPopupMenuClick;
  private final List<MinimalAd> searchResultAds;
  private final List<SearchApp> searchResult;
  private boolean adsLoaded = false;

  public SearchResultAdapter(PublishRelay<MinimalAd> onAdClickRelay,
      PublishRelay<SearchApp> onItemViewClick,
      PublishRelay<Pair<SearchApp, View>> onOpenPopupMenuClick, List<MinimalAd> searchResultAds,
      List<SearchApp> searchResult) {
    this.onAdClickRelay = onAdClickRelay;
    this.onItemViewClick = onItemViewClick;
    this.onOpenPopupMenuClick = onOpenPopupMenuClick;
    this.searchResultAds = searchResultAds;
    this.searchResult = searchResult;
  }

  @Override public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(viewType, parent, false);

    switch (viewType) {
      case SearchResultViewHolder.LAYOUT: {
        return new SearchResultViewHolder(view, onItemViewClick, onOpenPopupMenuClick);
      }

      case SearchResultAdViewHolder.LAYOUT: {
        return new SearchResultAdViewHolder(view, onAdClickRelay);
      }

      default: {
        return new SearchLoadingViewHolder(view);
      }
    }
  }

  @Override public void onBindViewHolder(ItemView holder, int position) {
    holder.setup(getItem(position));
  }

  @Override public int getItemViewType(int position) {
    final int adsCount = searchResultAds.size();
    final int appsCount = searchResult.size();
    if ((position == 0 && !adsLoaded) || position > (appsCount + adsCount - 2)) {
      return SearchLoadingViewHolder.LAYOUT;
    }

    if (position < adsCount) {
      return SearchResultAdViewHolder.LAYOUT;
    }

    return SearchResultViewHolder.LAYOUT;
  }

  @Override public int getItemCount() {
    return searchResultAds.size() + searchResult.size();
  }

  private Object getItem(int position) {
    final int adsCount = searchResultAds.size();

    if (position == 0 && adsCount == 0 && !adsLoaded) {
      return null;
    }

    if (position < adsCount) {
      return searchResultAds.get(position);
    }
    return searchResult.get(position - adsCount);
  }

  public void addResultForSearch(List<SearchApp> dataList) {
    searchResult.addAll(dataList);
    notifyDataSetChanged();
  }

  public void addResultForAds(List<MinimalAd> minimalAds) {
    searchResultAds.addAll(minimalAds);
    setAdsLoaded();
  }

  public void setAdsLoaded() {
    adsLoaded = true;
    notifyDataSetChanged();
  }
}
