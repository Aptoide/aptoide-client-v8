package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;

public interface BasebBodyInterceptorFactory {

  BodyInterceptor<BaseBody> createV7(AptoideAccountManager accountManager);

  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> createV3();
}
