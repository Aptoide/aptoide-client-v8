package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.autoupdate.AbSearchGroupResponse;
import retrofit2.http.GET;
import rx.Observable;

public class SearchAbTestService {
  private final Service service;

  public SearchAbTestService(Service service) {
    this.service = service;
  }

  public Observable<String> getExperimentForSearchAbTest() {
    return service.getAbTestForSearch()
        .map(AbSearchGroupResponse::getAbSearchId);
  }

  public interface Service {
    @GET("search_ab_test_mobile.json") Observable<AbSearchGroupResponse> getAbTestForSearch();
  }
}
