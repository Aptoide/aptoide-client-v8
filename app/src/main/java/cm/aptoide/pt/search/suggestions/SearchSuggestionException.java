package cm.aptoide.pt.search.suggestions;

public class SearchSuggestionException extends Exception {
  public SearchSuggestionException(String message) {
    super(message);
  }

  public SearchSuggestionException(Exception e) {
    super(e);
  }
}
