package cm.aptoide.pt.database;

import cm.aptoide.pt.abtesting.Experiment;
import cm.aptoide.pt.abtesting.ExperimentModel;
import cm.aptoide.pt.abtesting.ExperimentPersistence;
import cm.aptoide.pt.database.room.ExperimentDAO;
import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import rx.Completable;

public class RoomExperimentPersistence implements ExperimentPersistence {

  private final ExperimentDAO experimentDAO;
  private final RoomExperimentMapper mapper;

  public RoomExperimentPersistence(ExperimentDAO experimentDAO, RoomExperimentMapper mapper) {
    this.experimentDAO = experimentDAO;
    this.mapper = mapper;
  }

  @Override public Completable save(String experimentName, Experiment experiment) {
    return RxJavaInterop.toV1Completable(io.reactivex.Completable.create(completableEmitter -> {
      experimentDAO.save(mapper.map(experimentName, experiment));
      completableEmitter.onComplete();
    })
        .subscribeOn(Schedulers.io()));
  }

  @Override public rx.Single<ExperimentModel> get(String identifier) {
    return RxJavaInterop.toV1Single(experimentDAO.get(identifier)
        .subscribeOn(Schedulers.io())
        .flatMap(roomExperiment -> {
          if (roomExperiment == null) {
            return Single.just(new ExperimentModel(new Experiment(), true));
          } else {
            return Single.just(new ExperimentModel(mapper.map(roomExperiment), false));
          }
        }))
        .onErrorReturn(throwable -> new ExperimentModel(new Experiment(), true))
        .doOnError(Throwable::printStackTrace);
  }
}
