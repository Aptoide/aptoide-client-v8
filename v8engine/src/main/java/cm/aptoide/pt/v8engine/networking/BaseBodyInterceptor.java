package cm.aptoide.pt.v8engine.networking;

import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.utils.q.QManager;

/**
 * Created by neuro on 15-05-2017.
 */

public abstract class BaseBodyInterceptor<T> implements BodyInterceptor<T> {

  protected final String aptoideMd5sum;
  protected final String aptoidePackage;
  protected final IdsRepository idsRepository;
  protected final QManager qManager;

  protected BaseBodyInterceptor(String aptoideMd5sum, String aptoidePackage,
      IdsRepository idsRepository, QManager qManager) {
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.idsRepository = idsRepository;
    this.qManager = qManager;
  }
}
