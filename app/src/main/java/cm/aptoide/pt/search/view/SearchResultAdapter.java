package cm.aptoide.pt.search.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import cm.aptoide.pt.app.view.screenshots.ScreenShotClickEvent;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.download.view.DownloadClick;
import cm.aptoide.pt.search.SearchItemDiffCallback;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.SearchAppResultWrapper;
import cm.aptoide.pt.search.model.SearchItem;
import cm.aptoide.pt.search.model.SearchLoadingItem;
import cm.aptoide.pt.search.view.item.SearchLoadingViewHolder;
import cm.aptoide.pt.search.view.item.SearchResultItemView;
import cm.aptoide.pt.search.view.item.SearchResultViewHolder;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import rx.subjects.PublishSubject;

public class SearchResultAdapter extends DiffUtilAdapter<SearchItem, SearchResultItemView> {

  private final PublishSubject<SearchAppResultWrapper> onItemViewClick;
  private final PublishSubject<DownloadClick> downloadClickPublishSubject;
  private final PublishSubject<ScreenShotClickEvent> screenShotClick;
  private List<SearchItem> searchResults;
  private String query;
  private CrashReport crashReport;

  public SearchResultAdapter(PublishSubject<SearchAppResultWrapper> onItemViewClick,
      PublishSubject<DownloadClick> downloadClickPublishSubject,
      PublishSubject<ScreenShotClickEvent> screenShotClick, List<SearchItem> searchResults,
      CrashReport crashReport) {
    this.onItemViewClick = onItemViewClick;
    this.searchResults = searchResults;
    this.crashReport = crashReport;
    this.downloadClickPublishSubject = downloadClickPublishSubject;
    this.screenShotClick = screenShotClick;
  }

  @Override public SearchResultItemView onCreateViewHolder(ViewGroup parent, int viewType) {
    final Context context = parent.getContext();
    View view = LayoutInflater.from(context)
        .inflate(viewType, parent, false);

    switch (viewType) {
      case SearchResultViewHolder.LAYOUT: {
        return new SearchResultViewHolder(view, onItemViewClick, downloadClickPublishSubject,
            screenShotClick, query);
      }
      default: {
        return new SearchLoadingViewHolder(view);
      }
    }
  }

  @SuppressWarnings("unchecked") @Override
  public void onBindViewHolder(SearchResultItemView holder, int position) {
    try {
      holder.setup(searchResults.get(position));
    } catch (ClassCastException e) {
      crashReport.log(e);
    }
  }

  @Override public void onBindViewHolder(@NonNull SearchResultItemView holder, int position,
      @NonNull List<Object> payloads) {
    // Partial rebind for updating downloads
    if (holder instanceof SearchResultViewHolder && !payloads.isEmpty()) {
      ((SearchResultViewHolder) holder).setDownloadStatus((SearchAppResult) payloads.get(0));
    } else {
      super.onBindViewHolder(holder, position, payloads);
    }
  }

  @Override public int getItemViewType(int position) {
    switch (searchResults.get(position)
        .getType()) {
      case APP:
        return SearchResultViewHolder.LAYOUT;
      case LOADING:
        return SearchLoadingViewHolder.LAYOUT;
    }
    return SearchLoadingViewHolder.LAYOUT;
  }

  @Override public int getItemCount() {
    return searchResults.size();
  }

  public void setResultForSearch(String query, List<SearchAppResult> searchAppResults,
      boolean hasMore) {
    this.query = query;
    searchResults = new ArrayList<>(searchAppResults);
    // TODO
    //if (hasMore) {
    //  searchResults.add(new SearchLoadingItem());
    //}
    notifyDataSetChanged();
  }

  public void addResultForSearch(String query, List<SearchAppResult> searchAppResults,
      boolean hasMore) {
    this.query = query;
    List<SearchItem> newList = new ArrayList<>(searchAppResults);
    // TODO: Add this back when WS are fixed
    //if (hasMore) {
    //  newList.add(new SearchLoadingItem());
    //}
    applyDiffUtil(new DiffRequest<>(newList,
        new SearchItemDiffCallback(new ArrayList<>(searchResults), newList)));
  }

  public void setMoreLoading() {
    if (hasLoadingItem()) return;
    List<SearchItem> newList = new ArrayList<>(searchResults);
    newList.add(new SearchLoadingItem());
    applyDiffUtil(new DiffRequest<>(newList,
        new SearchItemDiffCallback(new ArrayList<>(searchResults), newList)));
  }

  private boolean hasLoadingItem() {
    for (SearchItem item : searchResults) {
      if (item instanceof SearchLoadingItem) {
        return true;
      }
    }
    return false;
  }

  @Override public void dispatchUpdates(@NotNull List<? extends SearchItem> newItems,
      @NotNull DiffUtil.DiffResult diffResult) {
    searchResults = (List<SearchItem>) newItems;
    diffResult.dispatchUpdatesTo(this);
  }
}
