package cm.aptoide.pt.search;

public class SearchFailureException extends Exception {
  SearchFailureException(String message) {
    super(message);
  }

  SearchFailureException(Exception e) {
    super(e);
  }
}
