package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.logger.Logger;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by franciscocalado on 19/06/18.
 */

public class ABTestService {
  private static final String IMPRESSION = "IMPRESSION";
  private static final String EXPERIMENT_OVER = "EXPERIMENT_EXPIRED";
  private static final String EXPERIMENT_PAUSED = "EXPERIMENT_PAUSED";
  private static final String EXPERIMENT_NOT_FOUND = "EXPERIMENT_NOT_FOUND";
  private static final String EXPERIMENT_DRAFT = "EXPERIMENT_IN_DRAFT_STATE";

  private ServiceV7 service;
  private String aptoideId;

  public ABTestService(ServiceV7 service, String aptoideId) {
    this.service = service;
    this.aptoideId = aptoideId;
  }

  public Observable<ExperimentModel> getExperiment(ABTestManager.ExperimentType experimentToGet) {
    return service.getExperiment(experimentToGet.getName(), aptoideId)
        .map((ABTestImpressionResponse response) -> mapToExperimentModel(response, false))
        .onErrorReturn(response -> new ExperimentModel(new Experiment(), true));
  }

  public Observable<Boolean> recordImpression(ABTestManager.ExperimentType experiment) {
    return service.recordImpression(experiment.getName(), aptoideId,
        new ABTestRequestBody(IMPRESSION))
        .doOnNext(voidResponse -> Logger.getInstance()
            .d(this.getClass()
                .getName(), "response : " + voidResponse.isSuccessful()))
        .doOnError(throwable -> throwable.printStackTrace())
        .map(__ -> true);
  }

  public Observable<Boolean> recordAction(ABTestManager.ExperimentType experimentType,
      String assignment) {
    return service.recordAction(experimentType.getName(), aptoideId,
        new ABTestRequestBody(assignment))
        .map(__ -> true);
  }

  private ExperimentModel mapToExperimentModel(ABTestImpressionResponse response,
      boolean hasError) {
    return new ExperimentModel(new Experiment(response.getPayload(), response.getAssignment(),
        mapExperimentStatus(response)), hasError);
  }

  private boolean mapExperimentStatus(ABTestImpressionResponse response) {
    return response.getStatus()
        .equals(EXPERIMENT_OVER) || response.getStatus()
        .equals(EXPERIMENT_PAUSED) || response.getStatus()
        .equals(EXPERIMENT_NOT_FOUND) || response.getStatus()
        .equals(EXPERIMENT_DRAFT);
  }

  public interface ServiceV7 {
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
