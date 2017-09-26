package cm.aptoide.pt.view.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.search.ListSearchApps;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.view.ItemView;
import cm.aptoide.pt.view.search.result.SearchResultAdViewHolder;
import cm.aptoide.pt.view.search.result.SearchResultViewHolder;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Arrays;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<ItemView> {

  private final PublishRelay<MinimalAd> onAdClickRelay;
  private final PublishRelay<SearchApp> onItemViewClick;
  private final PublishRelay<SearchApp> onOpenPopupMenuClick;
  private final List<MinimalAd> searchResultAds;
  private final List<SearchApp> searchResult;

  public SearchResultAdapter(PublishRelay<MinimalAd> onAdClickRelay,
      PublishRelay<SearchApp> onItemViewClick, PublishRelay<SearchApp> onOpenPopupMenuClick,
      List<MinimalAd> searchResultAds, List<SearchApp> searchResult) {
    this.onAdClickRelay = onAdClickRelay;
    this.onItemViewClick = onItemViewClick;
    this.onOpenPopupMenuClick = onOpenPopupMenuClick;
    this.searchResultAds = searchResultAds;
    this.searchResult = searchResult;
  }

  @Override public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(viewType, parent, false);
    if (viewType == SearchResultAdViewHolder.LAYOUT) {
      return new SearchResultAdViewHolder(view, onAdClickRelay);
    }
    return new SearchResultViewHolder(view, onItemViewClick, onOpenPopupMenuClick);
  }

  @Override public void onBindViewHolder(ItemView holder, int position) {
    holder.setup(getItem(position));
  }

  @Override public int getItemViewType(int position) {
    if (position < searchResultAds.size()) {
      return SearchResultAdViewHolder.LAYOUT;
    }
    return SearchResultViewHolder.LAYOUT;
  }

  @Override public int getItemCount() {
    return searchResultAds.size() + searchResult.size();
  }

  private Object getItem(int position) {
    final int adsCount = searchResultAds.size();
    if (position < adsCount) {
      return searchResultAds.get(position);
    }
    return searchResult.get(position - adsCount);
  }

  public void addResultForSearch(ListSearchApps data) {
    final int insertionPosition = searchResultAds.size() + searchResult.size();
    final List<SearchApp> dataList = data.getDataList()
        .getList();
    searchResult.addAll(dataList);
    final int insertedElementsCount = dataList.size();
    notifyItemRangeInserted(insertionPosition, insertedElementsCount);
  }

  public void addResultForAds(MinimalAd... minimalAds) {
    final int insertionPosition = searchResultAds.size();
    searchResultAds.addAll(Arrays.asList(minimalAds));
    notifyItemRangeInserted(insertionPosition, searchResultAds.size());
  }
}
