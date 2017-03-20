package cm.aptoide.pt.v8engine.account;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.BodyInterceptorFactory;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.v8engine.BaseBodyInterceptor;

public class BaseBodyInterceptorFactory implements BodyInterceptorFactory {

  private final AptoideClientUUID aptoideClientUUID;

  public BaseBodyInterceptorFactory(AptoideClientUUID aptoideClientUUID) {
    this.aptoideClientUUID = aptoideClientUUID;
  }

  @Override public BodyInterceptor<BaseBody> create(AptoideAccountManager accountManager) {
    return new BaseBodyInterceptor(aptoideClientUUID, accountManager);
  }
}