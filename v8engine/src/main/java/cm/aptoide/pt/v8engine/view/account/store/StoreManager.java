package cm.aptoide.pt.v8engine.view.account.store;

import android.content.SharedPreferences;
import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v3.ErrorResponse;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v3.CheckUserCredentialsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SetStoreImageRequest;
import cm.aptoide.pt.dataprovider.ws.v7.SimpleSetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.v8engine.view.account.exception.InvalidImageException;
import cm.aptoide.pt.v8engine.view.account.exception.StoreCreationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;
import rx.Completable;
import rx.Single;

public class StoreManager {

  private static final String ERROR_CODE_2 = "WOP-2";
  private static final String ERROR_CODE_3 = "WOP-3";
  private static final String ERROR_API_1 = "API-1";

  private final AptoideAccountManager accountManager;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final BodyInterceptor<HashMapNotNull<String, RequestBody>> multipartBodyInterceptor;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorV7;
  private final SharedPreferences sharedPreferences;
  private final TokenInvalidator tokenInvalidator;
  private final RequestBodyFactory requestBodyFactory;
  private final ObjectMapper objectMapper;

  public StoreManager(AptoideAccountManager accountManager, OkHttpClient httpClient,
      Converter.Factory converterFactory,
      BodyInterceptor<HashMapNotNull<String, RequestBody>> multipartBodyInterceptor,
      BodyInterceptor<BaseBody> bodyInterceptorV3,
      BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v7.BaseBody> bodyInterceptorV7,
      SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      RequestBodyFactory requestBodyFactory, ObjectMapper objectMapper) {
    this.accountManager = accountManager;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.multipartBodyInterceptor = multipartBodyInterceptor;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.bodyInterceptorV7 = bodyInterceptorV7;
    this.sharedPreferences = sharedPreferences;
    this.tokenInvalidator = tokenInvalidator;
    this.requestBodyFactory = requestBodyFactory;
    this.objectMapper = objectMapper;
  }

  public Completable createOrUpdate(long storeId, String storeName, String storeDescription,
      String storeImagePath, boolean hasNewAvatar, String storeThemeName, boolean storeExists) {
    return Completable.defer(() -> {
      if (storeExists) {
        return updateStore(storeName, storeDescription, storeImagePath, hasNewAvatar,
            storeThemeName);
      }
      return createStore(storeId, storeName, storeDescription, storeImagePath, hasNewAvatar,
          storeThemeName);
    })
        .onErrorResumeNext(err -> {
          if (err instanceof StoreCreationException || err instanceof InvalidImageException) {
            return Completable.error(err);
          }
          if (err instanceof AptoideWsV7Exception) {
            if (((AptoideWsV7Exception) err).getBaseResponse()
                .getErrors()
                .get(0)
                .getCode()
                .equals(ERROR_API_1)) {
              return Completable.error(new InvalidImageException(
                  Collections.singletonList(InvalidImageException.ImageError.API_ERROR)));
            } else {
              return Completable.error(new InvalidImageException(
                  Collections.singletonList(InvalidImageException.ImageError.API_ERROR),
                  err.getMessage()));
            }
          }

          // it's an unknown error
          return Completable.error(err);
        });
  }

  private Completable createStore(long storeId, String storeName, String storeDescription,
      String storeImage, boolean hasNewAvatar, String storeThemeName) {

    if (TextUtils.isEmpty(storeName)) {
      return Completable.error(new StoreValidationException(StoreValidationException.EMPTY_NAME));
    }

    /*
     * To create a store we need to call WS CheckUserCredentials so we can associate a
     * user to a newly created store.
     *
     * Then, if we have more data we either use a SetStore with multi-part request if we have
     * a store image, or a SetStore without image. This is the edit store use case {@link
     * #updateStore}.
     */
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> CheckUserCredentialsRequest.toCreateStore(bodyInterceptorV3, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences, storeName)
            .observe()
            .toSingle()
            .flatMap(data -> {
              final List<ErrorResponse> errors = data.getErrors();
              if (errors != null && !errors.isEmpty() && errors.get(0).code.equals(ERROR_CODE_2)) {
                return Single.error(new StoreCreationException());
              } else if (errors != null && errors.size() > 0 && errors.get(0).code.equals(
                  ERROR_CODE_3)) {
                return Single.error(new StoreCreationException(errors.get(0).code));
              }

              return Single.just(data);
            }))
        .flatMapCompletable(data -> {
          final Completable syncAccount = accountManager.syncCurrentAccount();
          if (needToUploadMoreStoreData(storeDescription, storeImage, hasNewAvatar)) {
            return updateStore(storeName, storeDescription, storeImage, hasNewAvatar,
                storeThemeName).andThen(syncAccount);
          }
          return syncAccount;
        });
  }

  private boolean needToUploadMoreStoreData(String storeDescription, String storeImage,
      boolean hasNewAvatar) {
    return !TextUtils.isEmpty(storeDescription) || (hasNewAvatar && !TextUtils.isEmpty(storeImage));
  }

  private Completable updateStore(String storeName, String storeDescription, String storeImage,
      boolean hasNewAvatar, String storeThemeName) {

    if (TextUtils.isEmpty(storeName)) {
      return Completable.error(new StoreValidationException(StoreValidationException.EMPTY_NAME));
    } else if (hasNewAvatar && TextUtils.isEmpty(storeImage)) {
      return Completable.error(new StoreValidationException(StoreValidationException.EMPTY_AVATAR));
    }

    /*
     * If we have more data we either use a SetStore with multi-part request if we have
     * a store image, or a SetStore without image.
     */
    if (hasNewAvatar) {
      return updateStoreWithAvatar(storeName, storeDescription, storeThemeName, storeImage);
    }

    return updateStoreWithoutAvatar(storeName, storeDescription, storeThemeName);
  }

  private Completable updateStoreWithoutAvatar(String storeName, String storeDescription,
      String storeThemeName) {
    return SimpleSetStoreRequest.of(storeName, storeThemeName, storeDescription, bodyInterceptorV7,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe()
        .toCompletable();
  }

  private Completable updateStoreWithAvatar(String storeName, String storeDescription,
      String storeThemeName, String storeImagePath) {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> SetStoreImageRequest.of(storeName, storeThemeName, storeDescription,
            storeImagePath, multipartBodyInterceptor, httpClient, converterFactory,
            requestBodyFactory, objectMapper, sharedPreferences, tokenInvalidator)
            .observe()
            .toSingle())
        .toCompletable();
  }
}
