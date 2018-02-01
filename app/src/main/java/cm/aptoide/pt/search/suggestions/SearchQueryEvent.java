package cm.aptoide.pt.search.suggestions;

import android.support.annotation.NonNull;

public class SearchQueryEvent {
  private final String query;
  private final int position;
  private final boolean isSubmitted;
  private final boolean isSuggestion;

  public SearchQueryEvent(@NonNull String query, boolean isSubmitted) {
    this(query, 0, isSubmitted, false);
  }

  public SearchQueryEvent(@NonNull String query, int position, boolean isSubmitted,
      boolean isSuggestion) {
    this.query = query;
    this.position = position;
    this.isSubmitted = isSubmitted;
    this.isSuggestion = isSuggestion;
  }

  public String getQuery() {
    return query;
  }

  public boolean hasQuery() {
    return query != null && !query.isEmpty();
  }

  public int getPosition() {
    return position;
  }

  public boolean isSubmitted() {
    return isSubmitted;
  }

  public boolean isSuggestion() {
    return isSuggestion;
  }
}
