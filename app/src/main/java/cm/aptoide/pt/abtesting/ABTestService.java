package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.networking.IdsRepository;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import rx.Scheduler;

/**
 * Created by franciscocalado on 19/06/18.
 */

public class ABTestService {
  private static final String IMPRESSION = "IMPRESSION";
  private static final String EXPERIMENT_OVER = "EXPERIMENT_EXPIRED";
  private static final String EXPERIMENT_PAUSED = "EXPERIMENT_PAUSED";
  private static final String EXPERIMENT_NOT_FOUND = "EXPERIMENT_NOT_FOUND";
  private static final String EXPERIMENT_DRAFT = "EXPERIMENT_IN_DRAFT_STATE";

  private IdsRepository idsRepository;
  private Scheduler scheduler;
  private ABTestServiceProvider abTestServiceProvider;

  public ABTestService(ABTestServiceProvider abTestServiceProvider, IdsRepository idsRepository,
      Scheduler scheduler) {
    this.abTestServiceProvider = abTestServiceProvider;
    this.idsRepository = idsRepository;
    this.scheduler = scheduler;
  }

  public Observable<ExperimentModel> getExperiment(String identifier,
      BaseExperiment.ExperimentType type) {
    return getAptoideId().flatMap(aptoideId -> abTestServiceProvider.getService(type)
        .getExperiment(identifier, aptoideId)
        .subscribeOn(scheduler))
        .map((ABTestImpressionResponse response) -> mapToExperimentModel(response, false))
        .onErrorReturn(response -> new ExperimentModel(new Experiment(), true));
  }

  public Observable<Boolean> recordImpression(String identifier,
      BaseExperiment.ExperimentType type) {
    return getAptoideId().flatMap(aptoideId -> abTestServiceProvider.getService(type)
        .recordImpression(identifier, aptoideId, new ABTestRequestBody(IMPRESSION)))
        .doOnNext(voidResponse -> Logger.getInstance()
            .d(this.getClass()
                .getName(), "response : " + voidResponse.isSuccessful()))
        .doOnError(throwable -> throwable.printStackTrace())
        .map(__ -> true);
  }

  public Observable<Boolean> recordAction(String identifier, String assignment,
      BaseExperiment.ExperimentType type) {
    return getAptoideId().flatMap(aptoideId -> abTestServiceProvider.getService(type)
        .recordAction(identifier, aptoideId, new ABTestRequestBody(assignment)))
        .doOnNext(voidResponse -> Logger.getInstance()
            .d(this.getClass()
                .getName(), "response : " + voidResponse.isSuccessful()))
        .doOnError(throwable -> throwable.printStackTrace())
        .map(__ -> true);
  }

  private ExperimentModel mapToExperimentModel(ABTestImpressionResponse response,
      boolean hasError) {
    return new ExperimentModel(
        new Experiment(System.currentTimeMillis(), response.getPayload(), response.getAssignment(),
            mapExperimentStatus(response)), hasError);
  }

  private boolean mapExperimentStatus(ABTestImpressionResponse response) {
    return response.getStatus()
        .equals(EXPERIMENT_OVER) || response.getStatus()
        .equals(EXPERIMENT_PAUSED) || response.getStatus()
        .equals(EXPERIMENT_NOT_FOUND) || response.getStatus()
        .equals(EXPERIMENT_DRAFT);
  }

  private Observable<String> getAptoideId() {
    return idsRepository.getUniqueIdentifier()
        .toObservable();
  }

  public interface ABTestingService {
    @GET("assignments/applications/Android/experiments/{experimentName}/users/{aptoideId}")
    Observable<ABTestImpressionResponse> getExperiment(
        @Path(value = "experimentName") String experimentName,
        @Path(value = "aptoideId") String aptoideId);

    @POST("events/applications/Android/experiments/{experimentName}/users/{aptoideId}")
    Observable<Response<Void>> recordImpression(
        @Path(value = "experimentName") String experimentName,
        @Path(value = "aptoideId") String aptoideId, @Body ABTestRequestBody body);

    @POST("events/applications/Android/experiments/{experimentName}/users/{aptoideId}")
    Observable<Response<Void>> recordAction(@Path(value = "experimentName") String experimentName,
        @Path(value = "aptoideId") String aptoideId, @Body ABTestRequestBody body);
  }
}
