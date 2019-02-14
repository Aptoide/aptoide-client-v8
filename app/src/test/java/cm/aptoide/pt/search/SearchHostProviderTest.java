package cm.aptoide.pt.search;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SearchHostProviderTest {

  @Test public void getSearchHost_toolBoxDisabled_aptoideHttpScheme() {
    SearchHostProvider searchHostProvider =
        new SearchHostProvider(false, "http", "buzz.aptoide.com:10001", "buzz.aptoide.com:10002");
    String searchHost = searchHostProvider.getSearchHost();
    assertEquals(searchHost, "http://buzz.aptoide.com:10001/v1/");
  }

  @Test public void getSearchHost_toolBoxDisabled_aptoideHttpsScheme() {
    SearchHostProvider searchHostProvider =
        new SearchHostProvider(false, "https", "buzz.aptoide.com:10001", "buzz.aptoide.com:10002");
    String searchHost = searchHostProvider.getSearchHost();
    assertEquals(searchHost, "https://buzz.aptoide.com:10002/v1/");
  }

  @Test public void getSearchHost_toolBoxEnabled_aptoideHttpScheme() {
    SearchHostProvider searchHostProvider =
        new SearchHostProvider(true, "http", "buzz.aptoide.com:10001", "buzz.aptoide.com:10002");
    String searchHost = searchHostProvider.getSearchHost();
    assertEquals(searchHost, "http://buzz.aptoide.com:10001/v1/");
  }

  @Test public void getSearchHost_toolBoxEnabled_aptoideHttpsScheme() {
    SearchHostProvider searchHostProvider =
        new SearchHostProvider(true, "https", "buzz.aptoide.com:10001", "buzz.aptoide.com:10002");
    String searchHost = searchHostProvider.getSearchHost();
    assertEquals(searchHost, "http://buzz.aptoide.com:10001/v1/");
  }
}