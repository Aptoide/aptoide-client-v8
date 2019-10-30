package cm.aptoide.pt.abtesting;

interface BaseExperiment {

   ExperimentType getType();

   enum ExperimentType{
     RAKAM, WASABI
   }
}
