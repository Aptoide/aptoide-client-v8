package cm.aptoide.pt.view.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.model.v7.ListSearchApps;
import cm.aptoide.pt.view.search.result.SearchResultViewHolder;
import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultViewHolder> {

  private final ArrayList<ListSearchApps.SearchAppsApp> resultsForSearchFollowedStores;
  private final ArrayList<ListSearchApps.SearchAppsApp> resultsForSearchEverywhere;
  private boolean showingResultsFromEverywhere;

  public SearchResultAdapter() {
    resultsForSearchFollowedStores = new ArrayList<>();
    resultsForSearchEverywhere = new ArrayList<>();
    showingResultsFromEverywhere = false;
  }

  @Override public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(SearchResultViewHolder.LAYOUT, parent, false);

    return new SearchResultViewHolder(view, test);
  }

  @Override public void onBindViewHolder(SearchResultViewHolder holder, int position) {
    boolean resultsFromEveryWhere = false;
    ListSearchApps.SearchAppsApp app = resultsFromEveryWhere ? resultsForSearchEverywhere.get(position)
        : resultsForSearchFollowedStores.get(position);
    holder.setupWith(app);
  }

  @Override public int getItemViewType(int position) {
    return 0;
  }

  @Override public int getItemCount() {
    return showingResultsFromEverywhere ? resultsForSearchEverywhere.size()
        : resultsForSearchFollowedStores.size();
  }

  public void showResultsForSearchFollowedStores() {
    showingResultsFromEverywhere = false;
    notifyDataSetChanged();
  }

  public void showResultsForSearchEverywhere() {
    showingResultsFromEverywhere = true;
    notifyDataSetChanged();
  }

  public void cleanResultForSearchFollowedStores() {
    resultsForSearchFollowedStores.clear();
  }

  public void cleanResultForSearchEverywhere() {
    resultsForSearchEverywhere.clear();
  }

  public void addResultForSearchFollowedStores(ListSearchApps data) {
    final List<ListSearchApps.SearchAppsApp> dataList = data.getDataList()
        .getList();
    resultsForSearchFollowedStores.addAll(dataList);
  }

  public void addResultForSearchEverywhere(ListSearchApps data) {
    final List<ListSearchApps.SearchAppsApp> dataList = data.getDataList()
        .getList();
    resultsForSearchEverywhere.addAll(dataList);
  }

  private boolean hasMoreResults(ListSearchApps data) {
    DataList<ListSearchApps.SearchAppsApp> dataList = data.getDataList();
    return dataList.getList()
        .size() > 0 || data.getTotal() > data.getNextSize();
  }
}
