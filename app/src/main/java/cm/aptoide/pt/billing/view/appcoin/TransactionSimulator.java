package cm.aptoide.pt.billing.view.appcoin;

import cm.aptoide.pt.logger.Logger;

/**
 * Created by jose_messejana on 31-10-2017.
 */

public class TransactionSimulator {
  private Estado status;
  public static final int TIME_FOR_TEST_TRANSACTION = 15000; //10s


  public TransactionSimulator(){
    Logger.d("TAG123","TScreated");
    status = Estado.PENDING;
  }

  public Estado   getStatus(){
    return status;
  }

  public void startThread(){
    Logger.d("TAG123","completed");
    status = Estado.COMPLETED;
  }

  public enum Estado{
    PENDING,COMPLETED,OTHER,FAILED,CANCELED;
  }
}
