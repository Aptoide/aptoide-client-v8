package cm.aptoide.pt.search.view;

/**
 * Created by franciscocalado on 11/6/17.
 */

public class TrendingQueryResultRepository implements QueryResultRepository {

  private final QueryResultRepository queryResultRepository;

  public TrendingQueryResultRepository(QueryResultRepository queryResultRepository) {
    this.queryResultRepository = queryResultRepository;
  }

  @Override public String getQueryAt(int index) {
    return queryResultRepository.getQueryAt(index);
  }
}
