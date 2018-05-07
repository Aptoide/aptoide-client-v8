package cm.aptoide.pt.search.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAdResultWrapper;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.SearchAppResultWrapper;
import cm.aptoide.pt.search.view.item.SearchLoadingViewHolder;
import cm.aptoide.pt.search.view.item.SearchResultAdViewHolder;
import cm.aptoide.pt.search.view.item.SearchResultItemView;
import cm.aptoide.pt.search.view.item.SearchResultViewHolder;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultItemView> {

  private final PublishRelay<SearchAdResultWrapper> onAdClickRelay;
  private final PublishRelay<SearchAppResultWrapper> onItemViewClick;
  private final List<SearchAdResult> searchAdResults;
  private final List<SearchAppResult> searchResults;
  private boolean adsLoaded = false;
  private boolean isLoadingMore = false;
  private CrashReport crashReport;

  public SearchResultAdapter(PublishRelay<SearchAdResultWrapper> onAdClickRelay,
      PublishRelay<SearchAppResultWrapper> onItemViewClick, List<SearchAppResult> searchResults,
      List<SearchAdResult> searchAdResults, CrashReport crashReport) {
    this.onAdClickRelay = onAdClickRelay;
    this.onItemViewClick = onItemViewClick;
    this.searchResults = searchResults;
    this.searchAdResults = searchAdResults;
    this.crashReport = crashReport;
  }

  @Override public SearchResultItemView onCreateViewHolder(ViewGroup parent, int viewType) {
    final Context context = parent.getContext();
    View view = LayoutInflater.from(context)
        .inflate(viewType, parent, false);

    switch (viewType) {
      case SearchResultViewHolder.LAYOUT: {
        return new SearchResultViewHolder(view, onItemViewClick);
      }

      case SearchResultAdViewHolder.LAYOUT: {
        return new SearchResultAdViewHolder(view, onAdClickRelay);
      }

      default: {
        return new SearchLoadingViewHolder(view);
      }
    }
  }

  @SuppressWarnings("unchecked") @Override
  public void onBindViewHolder(SearchResultItemView holder, int position) {
    try {
      holder.setup(getItem(position), position);
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

  @Override public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    for (int i = 0; i < getItemCount(); i++) {
      try {
        ((SearchResultItemView) getItem(i)).prepareToRecycle();
      } catch (NullPointerException | ClassCastException e) {
      }
    }
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

  public void addResultForSearch(List<SearchAppResult> dataList) {
    searchResults.addAll(dataList);
    notifyDataSetChanged();
  }

  public void setResultForAd(SearchAdResult searchAd) {
    searchAdResults.add(searchAd);
    setAdsLoaded();
  }

  public void setAdsLoaded() {
    adsLoaded = true;
    notifyDataSetChanged();
  }

  public void setIsLoadingMore(boolean isLoadingMore) {
    this.isLoadingMore = isLoadingMore;
    notifyDataSetChanged();
  }

  public void restoreState(List<SearchAppResult> apps, List<SearchAdResult> ads) {
    this.searchResults.clear();
    this.searchResults.addAll(apps);

    this.searchAdResults.clear();
    this.searchAdResults.addAll(ads);

    adsLoaded = true;
    isLoadingMore = false;
  }
}
