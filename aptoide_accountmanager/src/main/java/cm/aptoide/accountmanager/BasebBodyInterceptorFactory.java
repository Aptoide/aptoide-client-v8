package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;

public interface BasebBodyInterceptorFactory {

  BodyInterceptor<BaseBody> createV7(AptoideAccountManager accountManager);

  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> createV3(
      AptoideAccountManager accountManager);
}
