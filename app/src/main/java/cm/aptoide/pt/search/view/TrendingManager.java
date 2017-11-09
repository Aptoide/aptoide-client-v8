package cm.aptoide.pt.search.view;

import cm.aptoide.pt.search.SearchManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Single;

/**
 * Created by franciscocalado on 11/9/17.
 */

public class TrendingManager {
  private static final int SUGGESTION_COUNT = 5;

  public TrendingManager(){
  }

  public Observable<List<String>> getTrendingSuggestions(){
    List<String> test = new ArrayList<>();
    test.add("Facebook");
    test.add("Twitter");
    test.add("Google");
    test.add("Hill Climb Racing");
    test.add("Aptoide");
    return Observable.just(test);
  }
}
