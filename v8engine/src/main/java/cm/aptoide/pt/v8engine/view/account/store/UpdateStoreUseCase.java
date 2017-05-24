package cm.aptoide.pt.v8engine.view.account.store;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.v8engine.networking.StoreBodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class UpdateStoreUseCase implements UseCase<Void> {

  private final ManageStoreModel storeModel;
  private final AptoideAccountManager accountManager;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final StoreBodyInterceptor bodyInterceptor;
  private BodyInterceptor<BaseBody> bodyInterceptorV3;

  public UpdateStoreUseCase(ManageStoreModel storeModel, AptoideAccountManager accountManager,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      StoreBodyInterceptor bodyInterceptor, BodyInterceptor<BaseBody> bodyInterceptorV3) {
    this.storeModel = storeModel;
    this.accountManager = accountManager;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.bodyInterceptor = bodyInterceptor;
    this.bodyInterceptorV3 = bodyInterceptorV3;
  }

  @Override public Observable<Void> execute() {
    return Observable.defer(() -> {
      if (storeModel.storeExists()) {
        return new EditStoreUseCase(storeModel, accountManager, httpClient, converterFactory,
            bodyInterceptor).execute();
      }
      return new CreateStoreUseCase(storeModel, accountManager, httpClient, converterFactory,
          bodyInterceptor, bodyInterceptorV3).execute();
    });
  }
}
