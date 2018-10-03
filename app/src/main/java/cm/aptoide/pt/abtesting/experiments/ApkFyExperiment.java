package cm.aptoide.pt.abtesting.experiments;

import cm.aptoide.pt.abtesting.ABTestManager;
import rx.Completable;
import rx.Scheduler;
import rx.functions.Action0;

/**
 * Created by franciscoaleixo on 21/09/2018.
 */

public class ApkFyExperiment {
  private static final String EXPERIMENT_ID = "apkfy";

  private ABTestManager abTestManager;
  private Scheduler scheduler;

  public ApkFyExperiment(ABTestManager abTestManager, Scheduler scheduler){
    this.abTestManager = abTestManager;
    this.scheduler = scheduler;
  }
  public void performAbTest(Action0 oldDialogAction, Action0 newDialogAction){
    abTestManager.getExperiment(EXPERIMENT_ID)
        .flatMapCompletable(experiment -> {
          String experimentAssigment = "default";
          if(!experiment.isExperimentOver() && experiment.isPartOfExperiment()){
            experimentAssigment = experiment.getAssignment();
          }
          switch (experimentAssigment){
            case "default":
            case "old_dialogue":
              return Completable.fromAction(oldDialogAction);
            case "newdialog":
              return Completable.fromAction(newDialogAction);
          }
          return Completable.complete();
        })
        .observeOn(scheduler)
        .subscribe();
  }
}
