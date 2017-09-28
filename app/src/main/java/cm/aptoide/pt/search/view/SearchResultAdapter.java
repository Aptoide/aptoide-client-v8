package cm.aptoide.pt.search.view;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.search.view.item.SearchLoadingViewHolder;
import cm.aptoide.pt.search.view.item.SearchResultAdViewHolder;
import cm.aptoide.pt.search.view.item.SearchResultItemView;
import cm.aptoide.pt.search.view.item.SearchResultViewHolder;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultItemView> {

  private final PublishRelay<MinimalAd> onAdClickRelay;
  private final PublishRelay<SearchApp> onItemViewClick;
  private final PublishRelay<Pair<SearchApp, android.view.View>> onOpenPopupMenuClick;
  private final List<MinimalAd> searchAdResults;
  private final List<SearchApp> searchResults;
  private boolean adsLoaded = false;
  private boolean isLoadingMore = false;
  private CrashReport crashReport;

  public SearchResultAdapter(PublishRelay<MinimalAd> onAdClickRelay,
      PublishRelay<SearchApp> onItemViewClick,
      PublishRelay<Pair<SearchApp, View>> onOpenPopupMenuClick, List<SearchApp> searchResults,
      List<MinimalAd> searchAdResults, CrashReport crashReport) {
    this.onAdClickRelay = onAdClickRelay;
    this.onItemViewClick = onItemViewClick;
    this.onOpenPopupMenuClick = onOpenPopupMenuClick;
    this.searchResults = searchResults;
    this.searchAdResults = searchAdResults;
    this.crashReport = crashReport;
  }

  @Override public SearchResultItemView onCreateViewHolder(ViewGroup parent, int viewType) {
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

  @Override public void onBindViewHolder(SearchResultItemView holder, int position) {
    try {
      holder.setup(getItem(position));
    } catch (ClassCastException e) {
      crashReport.log(e);
    }
  }

  @Override public int getItemViewType(int position) {
    if (!adsLoaded && position == 0) {
      return SearchLoadingViewHolder.LAYOUT;
    }

    final int totalItems = searchAdResults.size() + searchResults.size();
    if (isLoadingMore && position >= totalItems) {
      return SearchLoadingViewHolder.LAYOUT;
    }

    if (adsLoaded && position < searchAdResults.size()) {
      return SearchResultAdViewHolder.LAYOUT;
    }

    return SearchResultViewHolder.LAYOUT;
  }

  @Override public int getItemCount() {
    final int itemCount = searchAdResults.size() + searchResults.size();
    return isLoadingMore ? itemCount + 1 : itemCount;
  }

  private Object getItem(int position) {
    if (!adsLoaded && position == 0) {
      return null;
    }

    final int totalItems = searchAdResults.size() + searchResults.size();
    if (isLoadingMore && position >= totalItems) {
      return null;
    }

    if (adsLoaded && position < searchAdResults.size()) {
      return searchAdResults.get(position);
    }
    return searchResults.get(position - searchAdResults.size());
  }

  public void addResultForSearch(List<SearchApp> dataList) {
    searchResults.addAll(dataList);
    notifyDataSetChanged();
  }

  public void setResultForAd(MinimalAd minimalAd) {
    searchAdResults.add(minimalAd);
    setAdsLoaded();
  }

  public void setAdsLoaded() {
    adsLoaded = true;
    notifyDataSetChanged();
  }

  public void setIsLoadingMore(boolean isLoadingMore) {
    this.isLoadingMore = isLoadingMore;
    if (isLoadingMore) {
      notifyItemInserted(searchAdResults.size() + searchResults.size());
    } else {
      notifyItemRemoved(searchAdResults.size() + searchResults.size() + 1);
    }
  }
}
