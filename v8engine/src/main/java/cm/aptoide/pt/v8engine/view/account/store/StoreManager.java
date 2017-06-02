package cm.aptoide.pt.v8engine.view.account.store;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CheckUserCredentialsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetStoreImageRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.model.v3.ErrorResponse;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;
import rx.Completable;
import rx.Single;

public class StoreManager {

  public static final String ERROR_CODE_2 = "WOP-2";
  public static final String ERROR_CODE_3 = "WOP-3";
  public static final String ERROR_API_1 = "API-1";

  private final AptoideAccountManager accountManager;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final BodyInterceptor<HashMapNotNull<String, RequestBody>> multipartBodyInterceptor;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorV7;
  private final RequestBodyFactory requestBodyFactory;
  private final ObjectMapper objectMapper;

  StoreManager(AptoideAccountManager accountManager, OkHttpClient httpClient,
      Converter.Factory converterFactory,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> multipartBodyInterceptor,
      BodyInterceptor<BaseBody> bodyInterceptorV3,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorV7,
      RequestBodyFactory requestBodyFactory, ObjectMapper objectMapper) {

    this.accountManager = accountManager;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.multipartBodyInterceptor = multipartBodyInterceptor;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.requestBodyFactory = requestBodyFactory;
    this.objectMapper = objectMapper;
  }

  public Completable createOrUpdate(long storeId, String storeName, String storeDescription,
      String storeImage, boolean hasNewAvatar, String storeThemeName, boolean storeExists) {
    return Completable.defer(() -> {
      if (storeExists) {
        return updateStore(storeId, storeName, storeDescription, storeImage, hasNewAvatar,
            storeThemeName);
      }
      return createStore(storeId, storeName, storeDescription, storeImage, hasNewAvatar,
          storeThemeName);
    })
        .onErrorResumeNext(err -> {
          if (err instanceof StoreCreationError || err instanceof NetworkError) {
            return Completable.error(err);
          }
          if (err instanceof AptoideWsV7Exception) {
            if (((AptoideWsV7Exception) err).getBaseResponse()
                .getErrors()
                .get(0)
                .getCode()
                .equals(ERROR_API_1)) {
              return Completable.error(new NetworkError());
            } else {
              return Completable.error(new NetworkError(err.getMessage()));
            }
          }

          // it's an unknown error
          return Completable.error(err);
        });
  }

  /**
   * To create a store we need to call WS CheckUserCredentials so we can associate a
   * user to a newly created store.
   *
   * Then, if we have more data we either use a SetStore with multi-part request if we have
   * a store image, or a SetStore without image. This is the edit store use case {@link
   * #updateStore}.
   */
  private Completable createStore(long storeId, String storeName, String storeDescription,
      String storeImage, boolean hasNewAvatar, String storeThemeName) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> CheckUserCredentialsRequest.toCreateStore(bodyInterceptorV3, httpClient,
            converterFactory, storeName)
            .observe()
            .toSingle()
            .flatMap(data -> {
              final List<ErrorResponse> errors = data.getErrors();
              if (errors != null && !errors.isEmpty() && errors.get(0).code.equals(ERROR_CODE_2)) {
                return Single.error(new StoreCreationError());
              } else if (errors != null && errors.size() > 0 && errors.get(0).code.equals(
                  ERROR_CODE_3)) {
                return Single.error(new StoreCreationErrorWithCode(errors.get(0).code));
              }

              return Single.just(data);
            }))
        .flatMapCompletable(data -> {
          // TODO use response store ID to upload image
          // data.repo
          if (needToUploadMoreStoreData(storeDescription, storeImage, hasNewAvatar)) {
            return updateStore(storeId, storeName, storeDescription, storeImage, hasNewAvatar,
                storeThemeName);
          }
          return Completable.complete();
        });
  }

  private boolean needToUploadMoreStoreData(String storeDescription, String storeImage,
      boolean hasNewAvatar) {
    return !TextUtils.isEmpty(storeDescription) || (hasNewAvatar && !TextUtils.isEmpty(storeImage));
  }

  /**
   * If we have more data we either use a SetStore with multi-part request if we have
   * a store image, or a SetStore without image.
   */
  private Completable updateStore(long storeId, String storeName, String storeDescription,
      String storeImage, boolean hasNewAvatar, String storeThemeName) {

    if (hasNewAvatar) {
      return updateStoreWithAvatar(storeName, storeDescription, storeThemeName, storeImage);
    }

    return updateStoreWithoutAvatar(storeName, storeDescription, storeThemeName);
  }

  private Completable updateStoreWithoutAvatar(String storeName, String storeDescription,
      String storeThemeName) {
    return SimpleSetStoreRequest.of(storeName, storeThemeName, storeDescription, bodyInterceptorV7,
        httpClient, converterFactory)
        .observe()
        .toCompletable();
  }

  private Completable updateStoreWithAvatar(String storeName, String storeDescription,
      String storeThemeName, String storeImage) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> SetStoreImageRequest.of(storeName, storeThemeName, storeDescription,
            storeImage, multipartBodyInterceptor, httpClient, converterFactory, requestBodyFactory,
            objectMapper)
            .observe()
            .toSingle())
        .toCompletable();
  }

  static class StoreCreationErrorWithCode extends Exception {
    private final String errorCode;

    StoreCreationErrorWithCode(String errorCode) {
      this.errorCode = errorCode;
    }

    public String getErrorCode() {
      return errorCode;
    }
  }

  static class StoreCreationError extends Exception {
  }

  static class NetworkError extends Exception {
    private final boolean apiError;
    private final String error;

    NetworkError() {
      this.apiError = true;
      this.error = null;
    }

    NetworkError(String error) {
      this.apiError = false;
      this.error = error;
    }

    public String getError() {
      return error;
    }

    public boolean isApiError() {
      return apiError;
    }
  }
}
