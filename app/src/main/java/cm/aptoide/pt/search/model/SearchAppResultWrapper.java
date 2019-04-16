package cm.aptoide.pt.search.model;

import org.parceler.Parcel;

/**
 * Created by franciscocalado on 04/05/18.
 */

@Parcel public class SearchAppResultWrapper {

  private SearchAppResult searchAppResult;
  private int position;
  private String query;

  public SearchAppResultWrapper() {
  }

  public SearchAppResultWrapper(String query, SearchAppResult result, int position) {
    this.query = query;
    this.searchAppResult = result;
    this.position = position;
  }

  public SearchAppResult getSearchAppResult() {
    return searchAppResult;
  }

  public int getPosition() {
    return position;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }
}
