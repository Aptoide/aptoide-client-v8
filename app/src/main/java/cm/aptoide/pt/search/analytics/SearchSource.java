package cm.aptoide.pt.search.analytics;

public enum SearchSource {
  WIDGET("widget"), SHORTCUT("shortcut"), DEEP_LINK("deep_link"), SEARCH_ICON(
      "vanilla"), SEARCH_TOOLBAR("vanilla");

  private final String identifier;

  SearchSource(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }
}
